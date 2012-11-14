package me.caketalk.blacklist;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;
import android.util.Log;
import com.android.internal.telephony.ITelephony;

import java.util.List;

/**
 * @author Rock Huang
 * @version 0.1
 */
public class CallReceiver extends BroadcastReceiver {

    public static List<String> cachedBlacklist;

    private ITelephony telephony;

    public CallReceiver(ITelephony telephony) {
        this.telephony = telephony;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Log.d("CallReceiver", "Phone Action:" + action);

        // todo: checks cached blacklist
        Log.d("CallReceiver", "Current blocked phone number is: " + cachedBlacklist);

        String incomingNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
        Log.d("CallReceiver", "Incoming Phone Number: " + incomingNumber);

        boolean block = false;
        String unwantedPhone = null;
        for (String phone: cachedBlacklist) {
            if (incomingNumber != null && phone != null && incomingNumber.equals(phone)) {
                Log.d(this.getClass().getName(), "Unwanted phone call has been discovered: " + phone);
                unwantedPhone = phone;
                block = true;
                break;
            }
        }

        if ("android.intent.action.PHONE_STATE".equals(action)) {//拦截电话
            String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
            Log.i("CallReceiver", "Phone State: " + state);

            if (state.equalsIgnoreCase(TelephonyManager.EXTRA_STATE_RINGING) && block) {//电话正在响铃

                //if (number != null && number.equals(CallReceiver.cachedBlacklist)) {//拦截指定的电话号码
                    //先静音处理
                    AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
                    audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                    Log.d(this.getClass().getName(), "Turn Ringtone Silent");

                    try {
                        //挂断电话
                        telephony.endCall();
                    } catch (Exception e) {
                        Log.w(this.getClass().getName(), e);
                    }

                    //再恢复正常铃声
                    audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                //}
            }
        } else if ("android.provider.Telephony.SMS_RECEIVED".equals(action)) {//拦截短信
            SmsMessage sms = getMessagesFromIntent(intent)[0];
            String number = sms.getOriginatingAddress();
            Log.d("SMS Receiving", "Phone Number: " + number);
            number = trimSmsNumber("+86", number);//把国家代码去除掉
            if (number.equals(unwantedPhone)) {
                abortBroadcast();//这句很重要，中断广播后，其他要接收短信的应用都没法收到短信广播了
            }
        }
    }

    private SmsMessage[] getMessagesFromIntent(Intent intent) {
        Object[] messages = (Object[]) intent.getSerializableExtra("pdus");
        byte[][] pduObjs = new byte[messages.length][];
        for (int i = 0; i < messages.length; i++) {
            pduObjs[i] = (byte[]) messages[i];
        }

        byte[][] pdus = new byte[pduObjs.length][];
        int pduCount = pdus.length;
        SmsMessage[] msgs = new SmsMessage[pduCount];

        for (int i = 0; i < pduCount; i++) {
            pdus[i] = pduObjs[i];
            msgs[i] = SmsMessage.createFromPdu(pdus[i]);
        }

        return msgs;
    }

    private String trimSmsNumber(String prefix, String number) {
        String s = number;

        if (prefix.length() > 0 && number.startsWith(prefix)) {
            s = number.substring(prefix.length());
        }

        return s;
    }
}