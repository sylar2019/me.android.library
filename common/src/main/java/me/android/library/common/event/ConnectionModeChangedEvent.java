package me.android.library.common.event;

import me.android.library.common.enums.ConnectivityMode;
import me.java.library.utils.base.guava.AbstractEvent;

/**
 * Created by sylar on 15/8/7.
 */
public class ConnectionModeChangedEvent extends AbstractEvent<Void, ConnectivityMode> {

    public ConnectionModeChangedEvent(ConnectivityMode connectivityMode) {
        super(connectivityMode);
    }
}