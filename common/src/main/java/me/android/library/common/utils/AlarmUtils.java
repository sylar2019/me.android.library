package me.android.library.common.utils;

import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.SystemClock;

/**
 * API 19 后的 AlarmManager 不再提供精准闹钟
 * From API level 19, all repeating alarms are inexact—that is, if our application targets KitKat or above, our repeat alarms will be inexact even if we use setRepeating.
 * If we really need exact repeat alarms, we can use setExact instead, and schedule the next alarm while handling the current one.
 * <p>
 * API23 后加入了白名单机制
 */
public class AlarmUtils {

    final static public int PollingType_Activity = 1;
    final static public int PollingType_Service = 2;
    final static public int PollingType_Broadcast = 3;

    final static public int FLAG = PendingIntent.FLAG_UPDATE_CURRENT;
    final static public int REQUEST_CODE = 12345;


    public static PendingIntent startPollingWithBroadcast(Context cx, Intent intent, long interval) {
        return startPolling(PollingType_Broadcast, cx, intent, interval, REQUEST_CODE);
    }

    public static PendingIntent startPollingWithBroadcast(Context cx, Intent intent, long interval, int requestCode) {
        return startPolling(PollingType_Broadcast, cx, intent, interval, requestCode);
    }

    public static void stopPollingWithBroadcast(Context cx, Intent intent) {
        stopPolling(PollingType_Broadcast, cx, intent, REQUEST_CODE);
    }

    public static void stopPollingWithBroadcast(Context cx, Intent intent, int requestCode) {
        stopPolling(PollingType_Broadcast, cx, intent, requestCode);
    }


    // -------------------------------------------------------------------------------
    // startPolling
    // -------------------------------------------------------------------------------


    public static PendingIntent startPolling(int pollingType, Context cx, Intent intent, long interval, int requestCode) {
        return startPolling(pollingType, cx, intent, interval, requestCode, FLAG);
    }

    @TargetApi(23)
    public static PendingIntent startPolling(int pollingType, Context cx, Intent intent, long interval, int requestCode, int flags) {

        // 获取AlarmManager系统服务
        AlarmManager mgr = (AlarmManager) cx.getSystemService(Context.ALARM_SERVICE);

        PendingIntent pi = getPendingIntent(pollingType, cx, requestCode, intent, flags);
        mgr.cancel(pi);

        // 触发服务的起始时间
        long triggerAtTime = SystemClock.elapsedRealtime();


        int version = Build.VERSION.SDK_INT;
        if (version >= 23) {
            //andorid6.0 (API23)
            //http://blog.csdn.net/qp1203300848/article/details/50823002
            mgr.setExactAndAllowWhileIdle(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime + interval, pi);
        } else if (version >= 19) {
            //android 4.4.2 (API19) 后的 AlarmManager 不再提供精准闹钟，需要用新增的setExact提供单次精准定时
            mgr.setExact(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime + interval, pi);
        } else {
            //API 19 之前的 AlarmManager 能提供精准的 setRepeating 方法
            mgr.setRepeating(AlarmManager.ELAPSED_REALTIME, triggerAtTime, interval, pi);
        }

        return pi;
    }

    // -------------------------------------------------------------------------------
    // stopPolling
    // -------------------------------------------------------------------------------

    public static void stopPolling(int pollingType, Context cx, Intent intent, int requestCode) {
        stopPolling(pollingType, cx, intent, requestCode, FLAG);
    }

    public static void stopPolling(int pollingType, Context cx, Intent intent, int requestCode, int flags) {

        PendingIntent pi = getPendingIntent(pollingType, cx, requestCode, intent, flags);
        if (pi == null) return;

        AlarmManager mgr = (AlarmManager) cx.getSystemService(Context.ALARM_SERVICE);
        mgr.cancel(pi);
    }


    //======================================================================================================================
    static PendingIntent getPendingIntent(int pollingType, Context cx, int requestCode, Intent intent, int flags) {

        PendingIntent pi = null;
        switch (pollingType) {
            case PollingType_Activity:
                pi = PendingIntent.getActivity(cx, requestCode, intent, flags);
                break;
            case PollingType_Service:
                pi = PendingIntent.getService(cx, requestCode, intent, flags);
                break;
            case PollingType_Broadcast:
                pi = PendingIntent.getBroadcast(cx, requestCode, intent, flags);
                break;
            default:
                break;
        }

        return pi;
    }

}
