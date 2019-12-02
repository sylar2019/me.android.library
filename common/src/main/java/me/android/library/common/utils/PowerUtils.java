package me.android.library.common.utils;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.PowerManager;
import android.provider.Settings;

/**
 * Created by sylar on 16/8/18.
 */
public class PowerUtils {

    /**
     * 需要权限：  <uses-permission android:name="android.permission.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS" />
     *
     * @param cx
     */
    @TargetApi(23)
    public static void addWhileIdle(Context cx) {
        if (Build.VERSION.SDK_INT >= 23) {
            if (!isIgnoringBatteryOptimizations(cx)) {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                intent.setData(Uri.parse("package:" + cx.getPackageName()));
                cx.startActivity(intent);
            }
        }
    }

    @TargetApi(23)
    public static void callIgnoreBatteryOptimizationSetting(Context cx) {
        if (Build.VERSION.SDK_INT >= 23) {
            Intent intent = new Intent();
            intent.setAction(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS);
            cx.startActivity(intent);
        }
    }

    @TargetApi(23)
    public static boolean isIgnoringBatteryOptimizations(Context cx) {
        if (Build.VERSION.SDK_INT >= 23) {
            String packageName = cx.getPackageName();
            PowerManager pm = (PowerManager) cx.getSystemService(Context.POWER_SERVICE);
            return pm.isIgnoringBatteryOptimizations(packageName);
        } else {
            return false;
        }
    }
}
