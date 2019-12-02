package me.android.library.ui.pojo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Preconditions;

import me.android.library.ui.DefaultMenuListener;
import me.android.library.ui.MenuListener;

/**
 * Created by sylar on 15/6/3.
 */
public class MenuInfo extends AbstractInfo {

    @JsonProperty("valid")
    public boolean valid = true;

    private MenuListener listener;

    @JsonIgnore
    public MenuListener getMenuListener() {
        if (clazz == null)
            return DefaultMenuListener.getInstance();

        if (listener == null) {
            listener = (MenuListener) getReflectObj();
            Preconditions.checkNotNull(listener, "invalid MenuInfo:"
                    + clazz);
        }

        return listener;
    }
}
