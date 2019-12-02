package me.android.library.common.receiver;

import android.content.Context;
import android.content.Intent;
import android.os.Build;

import me.android.library.common.utils.AlarmUtils;
import me.android.library.common.utils.LogUtils;


/**
 * APP 后台轮询任务
 * <p>
 * Created by sylar on 15/7/25.
 */
public abstract class AbstractPollingReceiver extends AbstractReceiver {

    abstract protected long getPollingPeriod();

    abstract protected int getPollingTaskId();

    abstract protected void onPolling();


    @Override
    public void onReceive(Context cx, Intent i) {

        try {
            onPolling();
        } catch (Exception e) {
            LogUtils.logFIleWithTime("后台轮询出错:" + e.getMessage());
        }

        if (Build.VERSION.SDK_INT >= 19) {
            /** API 19 后的 AlarmManager 不再提供精准闹钟
             *
             * From API level 19, all repeating alarms are inexact—that is, if our application targets KitKat or above, our repeat alarms will be inexact even if we use setRepeating.
             If we really need exact repeat alarms, we can use setExact instead, and schedule the next alarm while handling the current one.
             *
             * **/
            AlarmUtils.startPollingWithBroadcast(cx,
                    getIntent(cx),
                    getPollingPeriod(),
                    getPollingTaskId());

        }

    }

    protected Intent getIntent(Context cx) {
        Intent intent = new Intent(cx, getClass());
        intent.setAction(getClass().getName());
        return intent;
    }

}
