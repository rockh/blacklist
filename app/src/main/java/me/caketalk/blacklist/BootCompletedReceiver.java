package me.caketalk.blacklist;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import static android.content.Context.MODE_PRIVATE;

public class BootCompletedReceiver extends BroadcastReceiver {

    private static final String TAG = BootCompletedReceiver.class.getName();

    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)){
            SharedPreferences preferences = context.getSharedPreferences(SettingsFragment.SETTINGS, MODE_PRIVATE);
            boolean preferDisabled = preferences.getBoolean(SettingsFragment.K_SERVICE_DISABLED, false);

            Log.d(TAG, "Preferences -> Disabled -> " + preferDisabled);

            if (!preferDisabled) {
                Intent newIntent = new Intent(context, CallReceiverService.class);
                context.startService(newIntent);
                Log.d(TAG, "Service started after device boot completed.");
            }
        }
    }

}
