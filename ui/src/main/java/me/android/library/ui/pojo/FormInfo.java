package me.android.library.ui.pojo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

import me.android.library.ui.AbstractLoader;
import me.android.library.ui.LayoutLoader;
import me.java.library.common.model.pojo.AbstractIdPojo;

/**
 * Created by sylar on 15/6/3.
 */
public class FormInfo extends AbstractIdPojo<String> {

    @JsonProperty("homePage")
    public String homePageKey;

    @JsonProperty("loader")
    protected String loaderImp;

    private LayoutLoader loader = null;

    @JsonIgnore
    public LayoutLoader getLoader() {

        if (loader == null) {

            Preconditions.checkState(!Strings.isNullOrEmpty(loaderImp),
                    "loaderImp is null");

            try {
                Class<?> c = Class.forName(loaderImp);
                loader = (LayoutLoader) c.newInstance();
                if (loader instanceof AbstractLoader) {
                    ((AbstractLoader) loader).setKey(id);
                }
            } catch (Exception e) {
            }
        }

        Preconditions.checkNotNull(loader, "loader is null");
        return loader;
    }
}
