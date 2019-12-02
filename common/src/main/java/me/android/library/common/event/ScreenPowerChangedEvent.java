package me.android.library.common.event;

import me.java.library.utils.base.guava.AbstractEvent;

/**
 * Created by sylar on 15/8/7.
 */
public class ScreenPowerChangedEvent extends AbstractEvent<Void, Integer> {

    public ScreenPowerChangedEvent(Integer integer) {
        super(integer);
    }
}