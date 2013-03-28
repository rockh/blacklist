package me.caketalk.blacklist.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.telephony.TelephonyManager;
import android.text.style.SuperscriptSpan;
import android.util.Log;
import android.widget.Toast;
import com.android.internal.telephony.ITelephony;
import me.caketalk.blacklist.CallReceiver;

import java.lang.reflect.Method;

/**
 * @author Rock Huang
 * @version 0.1
 */
public class BlacklistService extends Service {

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
            Method m = TelephonyManager.class.getDeclaredMethod("getITelephony");
            m.setAccessible(true);
            ITelephony telephony = (ITelephony) m.invoke(telephonyManager);
            callReceiver = new CallReceiver(telephony, this);
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction("android.intent.action.PHONE_STATE");
            intentFilter.addAction("android.provider.Telephony.SMS_RECEIVED");
            registerReceiver(callReceiver, intentFilter);
            Log.i("BlacklistService", "BlacklistService has been created.");
        } catch (Exception e) {
            Log.w("BlacklistService::onCreate", e);
        }
    }

    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(callReceiver);
        Log.i("BlacklistService", "BlacklistService has been destroyed.");
    }

}
