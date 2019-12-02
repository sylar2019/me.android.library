package me.android.library.common.event;

import android.app.Application;

import me.java.library.utils.base.guava.AbstractEvent;

/**
 * Created by sylar on 15/8/7.
 */
public class AppVisibleEvent extends AbstractEvent<Application, Boolean> {

    public AppVisibleEvent(Boolean aBoolean) {
        super(aBoolean);
    }

    public AppVisibleEvent(Application application, Boolean aBoolean) {
        super(application, aBoolean);
    }
}
