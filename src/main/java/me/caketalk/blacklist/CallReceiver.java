package me.caketalk.blacklist;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;
import android.util.Log;
import com.android.internal.telephony.ITelephony;
import me.caketalk.blacklist.dao.BlacklistDao;

/**
 * @author Rock Huang
 * @version 0.2
 */
public class CallReceiver extends BroadcastReceiver {

    private ITelephony telephony;
    private BlacklistDao dao;

    public CallReceiver(ITelephony telephony, Context ctx) {
        this.telephony = telephony;
        this.dao = new BlacklistDao(ctx);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String incomingNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
        Log.d(CallReceiver.class.getName(), "Incoming Phone Number is: " + incomingNumber);

        String action = intent.getAction();
        Log.d(CallReceiver.class.getName(), "Intent Action is: " + action);


        if ("android.intent.action.PHONE_STATE".equals(action)) { // Intercepts phone call
            String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
            Log.i(CallReceiver.class.getName(), "Phone State is: " + state);

            if (state.equalsIgnoreCase(TelephonyManager.EXTRA_STATE_RINGING) && dao.isExist(incomingNumber)) {
                try {
                    telephony.endCall();
                } catch (Exception e) {
                    Log.w(this.getClass().getName(), e);
                }
            }
        } else if ("android.provider.Telephony.SMS_RECEIVED".equals(action)) { // Intercepts SMS
//            SmsMessage sms = getMessagesFromIntent(intent)[0];
//            String number = sms.getOriginatingAddress();
//            Log.d("SMS Receiving", "Phone Number: " + number);
//            // number = trimSmsNumber("+86", number);//把国家代码去除掉
//            if (dao.isExist(incomingNumber) && number.equals(incomingNumber)) {
//                abortBroadcast();//这句很重要，中断广播后，其他要接收短信的应用都没法收到短信广播了
//            }
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