package me.android.library.ui.pojo;

import android.content.Context;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

import me.android.library.common.utils.ResourcesUtils;
import me.java.library.common.model.pojo.AbstractIdNamePojo;

/**
 * Created by sylar on 15/6/3.
 */
public abstract class AbstractInfo extends AbstractIdNamePojo<String> {

    @JsonProperty("clazz")
    protected String clazz;

    @JsonProperty("iconRes")
    protected String iconRes;

    @Override
    public String getId() {
        return id;
    }

    public String getName() {
        return ResourcesUtils.getStringOrFromRes(name);
    }

    public String getClazz() {
        return clazz;
    }

    public int getIconResId(Context cx) {
        return ResourcesUtils.getResId(iconRes);
    }

    protected Object getReflectObj() {
        Preconditions.checkState(!Strings.isNullOrEmpty(clazz), "clazz is null");

        Object obj = null;
        try {
            Class<?> c = Class.forName(clazz);
            obj = c.newInstance();
        } catch (Exception e) {
        }

        return obj;
    }
}
