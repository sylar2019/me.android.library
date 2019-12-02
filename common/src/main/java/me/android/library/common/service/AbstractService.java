package me.android.library.common.service;

import android.content.Context;

import com.google.common.eventbus.Subscribe;

import me.android.library.common.enums.ConnectivityMode;
import me.android.library.common.event.ConnectionModeChangedEvent;
import me.java.library.common.Initializable;
import me.java.library.common.service.Serviceable;
import me.java.library.utils.base.guava.AsyncEventUtils;

public abstract class AbstractService implements Serviceable, Initializable<Context> {

    protected Context cx;
    protected Object[] params;

    public AbstractService() {
        AsyncEventUtils.regist(this);
    }

    @Override
    public void init(Context cx, Object... params) {
        this.cx = cx;
        this.params = params;
    }


    @Override
    public void dispose() {
        AsyncEventUtils.unregist(this);
    }

    @Subscribe
    public void onEvent(ConnectionModeChangedEvent event) {
        ConnectivityMode mode = event.getContent();
        onConnectionModeChanged(mode);
    }

    protected void postEvent(Object event) {
        AsyncEventUtils.postEvent(event);
    }


    protected void onConnectionModeChanged(ConnectivityMode mode) {
    }

}
