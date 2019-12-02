package me.android.library.common.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.PowerManager;

import com.google.common.base.Objects;

import me.android.library.common.event.ScreenPowerChangedEvent;

public class ScreenPowerService extends AbstractService {

    final public static int OFF = 0;
    final public static int ON = 1;
    final public static String TAG = ScreenPowerService.class.getSimpleName();

    // 电源管理器
    PowerManager pm;
    // 唤醒锁
    PowerManager.WakeLock wakeLock;
    private BroadcastReceiver receiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent in) {
            String inAction = in.getAction();
            if (Objects.equal(inAction, Intent.ACTION_SCREEN_OFF)) {
                postEvent(new ScreenPowerChangedEvent(OFF));
            } else if (Objects.equal(inAction, Intent.ACTION_SCREEN_ON)) {
                postEvent(new ScreenPowerChangedEvent(ON));
            }
        }
    };


    private ScreenPowerService() {
    }

    public static ScreenPowerService getInstance() {
        return ScreenPowerService.SingletonHolder.instance;
    }

    @Override
    public void init(Context cx, Object... params) {
        super.init(cx, params);
        registReceiver();

        pm = (PowerManager) cx.getSystemService(Context.POWER_SERVICE);
    }

    @Override
    public void dispose() {
        super.dispose();
        unregistReceiver();

        if (wakeLock != null) {
            wakeLock.release();
            wakeLock = null;
        }
    }

    public boolean isScreenOn() {
        PowerManager pm = (PowerManager) cx
                .getSystemService(Context.POWER_SERVICE);
        boolean flag = pm.isScreenOn();
        return flag;
    }

    public void wakeup() {
        if (isScreenOn()) return;

        if (wakeLock != null) {
            wakeLock.release();
            wakeLock = null;
        }

        wakeLock = pm.newWakeLock
                (PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.SCREEN_DIM_WAKE_LOCK, TAG);
        wakeLock.acquire();
    }

    public void gotoSleep() {
        if (wakeLock != null) {
            wakeLock.release();
            wakeLock = null;
        }

        PowerManager.WakeLock wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, TAG);
        wakeLock.acquire();
    }

    private void registReceiver() {
        IntentFilter screenStatusIF = new IntentFilter();
        screenStatusIF.addAction(Intent.ACTION_SCREEN_ON);
        screenStatusIF.addAction(Intent.ACTION_SCREEN_OFF);
        cx.registerReceiver(receiver, screenStatusIF);
    }

    private void unregistReceiver() {
        cx.unregisterReceiver(receiver);
    }

    private static class SingletonHolder {
        private static ScreenPowerService instance = new ScreenPowerService();
    }
}
