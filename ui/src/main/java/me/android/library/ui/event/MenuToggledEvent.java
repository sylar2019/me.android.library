package me.android.library.ui.event;

import me.android.library.ui.LayoutLoader;
import me.java.library.utils.base.guava.AbstractEvent;

public class MenuToggledEvent extends AbstractEvent<LayoutLoader, Boolean> {
    public MenuToggledEvent(LayoutLoader layoutLoader, Boolean aBoolean) {
        super(layoutLoader, aBoolean);
    }

    public boolean isShownMenu() {
        return getContent();
    }

}