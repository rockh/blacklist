package me.caketalk.blacklist;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;
import android.util.Log;
import com.android.internal.telephony.ITelephony;
import me.caketalk.blacklist.dao.BlacklistDao;
import me.caketalk.blacklist.dao.IBlacklistDao;
import me.caketalk.blacklist.manager.BlacklistManager;
import me.caketalk.blacklist.manager.IBlacklistManager;
import me.caketalk.blacklist.model.PhoneAction;

import java.util.Date;

/**
 * @author Rock Huang
 * @version 0.3 02/04/2013
 */
public class CallReceiver extends BroadcastReceiver {

    private final static String TAG = "me.caketalk.blacklist.CallReceiver";

    private ITelephony telephony;
    private IBlacklistDao dao;
    private IBlacklistManager manager;

    public CallReceiver(ITelephony telephony, Context ctx) {
        this.telephony = telephony;
        this.dao = new BlacklistDao(ctx);
        this.manager = new BlacklistManager(ctx);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String incomingNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
        //Log.d(CallReceiver.class.getName(), "Incoming Phone Number is: " + incomingNumber);

        String action = intent.getAction();
        //Log.d(CallReceiver.class.getName(), "Intent Action is: " + action);

        if ("android.intent.action.PHONE_STATE".equals(action)) { // Intercepts phone call
            String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
            //Log.i(CallReceiver.class.getName(), "Phone State is: " + state);

            // Non existing number: -1, Both: 0, Calls: 1, SMS: 2
            int blockOptId = dao.findBlockOptId(incomingNumber);
            boolean blockCall = blockOptId != -1 && blockOptId != 2;

            if (state.equalsIgnoreCase(TelephonyManager.EXTRA_STATE_RINGING) && blockCall) {
                try {
                    telephony.endCall();
                    // records history
                    manager.recordsHistory(incomingNumber, PhoneAction.CALL, null);
                    Log.d(CallReceiver.class.getName(), String.format("# Blocked a phone call => { number:%s, date:%s }", incomingNumber, new Date()));
                } catch (Exception e) {
                    Log.w(this.getClass().getName(), e);
                }
            }
        } else if ("android.provider.Telephony.SMS_RECEIVED".equals(action)) { // Intercepts SMS
            //SmsMessage sms = getMessagesFromIntent(intent)[0];
            SmsMessage sms = getMessage(intent);
            String smsFrom = sms.getOriginatingAddress();
            String smsBody = sms.getMessageBody();

            // Non existing number: -1, Both: 0, Calls: 1, SMS: 2
            int blockOptId = dao.findBlockOptId(smsFrom);
            Log.d(CallReceiver.class.getName(), "Block option id => " + blockOptId);
            if (blockOptId != -1 && blockOptId != 1) {
                abortBroadcast();
                manager.recordsHistory(smsFrom, PhoneAction.SMS, smsBody);
                Log.d(TAG, String.format("Receiving SMS is blocked => { From: %s, SMS Body: %s }", smsFrom, smsBody));
            }

        }
    }


    /********************* Private Methods *********************/

    private SmsMessage getMessage(Intent intent) {
        Bundle pudsBundle = intent.getExtras();
        Object[] pdus = (Object[]) pudsBundle.get("pdus");
        return SmsMessage.createFromPdu((byte[]) pdus[0]);
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

}