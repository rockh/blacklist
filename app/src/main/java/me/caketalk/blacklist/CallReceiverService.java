package me.caketalk.blacklist;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.lang.reflect.Method;

/**
 * @author Rock Huang
 * @version 0.1
 */
public class CallReceiverService extends Service {

    private CallReceiver callReceiver;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        // Using reflection to obtain hidden endCall method
        try {
            TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            Method getITelephony = TelephonyManager.class.getDeclaredMethod("getITelephony");
            getITelephony.setAccessible(true);
            Object telephony = getITelephony.invoke(telephonyManager);
            callReceiver = new CallReceiver(telephony, this);
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.setPriority(9999);
            intentFilter.addAction("android.intent.action.PHONE_STATE");
            intentFilter.addAction("android.provider.Telephony.SMS_RECEIVED");
            registerReceiver(callReceiver, intentFilter);
            Log.i(this.getClass().getName(), "CallReceiverService has been created.");
        } catch (Exception e) {
            Log.w(this.getClass().getName(), e);
        }
    }

    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(callReceiver);
        Log.i("CallReceiverService", "CallReceiverService has been destroyed.");
    }

}
