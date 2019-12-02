package me.android.library.common.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.google.common.base.Preconditions;


public abstract class AbstractReceiver extends BroadcastReceiver {

    protected Context cx;

    //动态注册广播
    public void start(Context cx) {
        Preconditions.checkNotNull(cx);

        this.cx = cx;
        IntentFilter filter = new IntentFilter();
        filter.addAction(getClass().getSimpleName());
        Intent intent = cx.registerReceiver(this, filter);
        cx.sendBroadcast(intent);
    }

    public void stop() {
        Preconditions.checkNotNull(cx);
        cx.unregisterReceiver(this);
    }

}
