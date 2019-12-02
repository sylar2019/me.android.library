package me.android.library.ui.event;

import me.android.library.ui.LayoutLoader;
import me.java.library.utils.base.guava.AbstractEvent;

public class PageChangedEvent extends AbstractEvent<LayoutLoader, String> {

    public PageChangedEvent(LayoutLoader layoutLoader, String s) {
        super(layoutLoader, s);
    }

    public String getPageKey() {
        return getContent();
    }
}
