package me.android.library.ui.pojo;

import android.content.Context;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Preconditions;

import me.android.library.common.utils.ResourcesUtils;
import me.android.library.ui.Page;

/**
 * Created by sylar on 15/6/3.
 */
public class PageInfo extends AbstractInfo {

    @JsonProperty
    protected String animInRes;

    @JsonProperty
    protected String animOutRes;

    @JsonIgnore
    public Page getPage() {
        Page page = (Page) getReflectObj();
        Preconditions.checkNotNull(page, "invalid PageInfo:" + clazz);

        page.setPageKey(id);
        page.setPageTitle(getName());
        return page;
    }

    @Override
    public String getName() {
        return name;
    }

    public int getAnimInResId(Context cx) {
        return ResourcesUtils.getResId(animInRes);
    }

    public int getAnimOutResId(Context cx) {
        return ResourcesUtils.getResId(animOutRes);
    }
}
