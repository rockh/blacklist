package me.caketalk.blacklist.util;

import android.app.ActivityManager;
import android.content.Context;
import android.util.Log;

import java.util.List;

/**
 * @author Rock Huang
 * @version 0.1
 */
public final class ServiceUtil {

    private final static String TAG = "ServiceUtil";

    private ServiceUtil() {
    }

    public static boolean isServiceRunning(Context context,String className) {
        boolean isRunning = false;
        ActivityManager activityManager = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> serviceList = activityManager.getRunningServices(Integer.MAX_VALUE);

        if (!(serviceList.size()>0)) {
            return false;
        }

        for (ActivityManager.RunningServiceInfo aServiceList : serviceList) {
            if (aServiceList.service.getClassName().equals(className)) {
                isRunning = true;
                break;
            }
        }

        Log.i(TAG, className + " " + (isRunning ? "is" : "isn't") + " running.");

        return isRunning;

    }

}
