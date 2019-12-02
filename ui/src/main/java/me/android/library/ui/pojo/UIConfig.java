package me.android.library.ui.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;

import java.util.List;

import me.android.library.ui.MenuListener;
import me.java.library.common.model.pojo.AbstractPojo;
import me.java.library.utils.base.JsonUtils;


/**
 * Created by sylar on 15/6/3.
 */
public class UIConfig extends AbstractPojo {

    @JsonProperty("main")
    private String mainFormKey;
    @JsonProperty("forms")
    private List<FormInfo> forms;
    @JsonProperty("menus")
    private List<MenuInfo> menus;
    @JsonProperty("pages")
    private List<PageInfo> pages;

    public static UIConfig loadConfig(String content) {

        Preconditions.checkState(!Strings.isNullOrEmpty(content),
                "invalid ui config");

        UIConfig res = null;

        try {
            res = JsonUtils.parseObject(content, UIConfig.class);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Preconditions.checkState(!Strings.isNullOrEmpty(res.mainFormKey),
                "mainFormKey is null");

        return res;
    }

    public String getMainFormKey() {
        return mainFormKey;
    }

    public PageInfo getPageInfo(String pageKey) {
        for (PageInfo pi : pages) {
            if (pi.getId().equals(pageKey))
                return pi;
        }
        return null;
    }

    public MenuListener getMenuListener(String menuKey) {
        MenuInfo mi = getMenuInfo(menuKey);
        return mi.getMenuListener();
    }

    public MenuInfo getMenuInfo(String menuKey) {
        for (MenuInfo mi : menus) {
            if (mi.getId().equals(menuKey))
                return mi;
        }
        return null;
    }

    public List<MenuInfo> getAllMenus() {
        return menus;
    }

    public List<MenuInfo> getValidMenus() {
        List<MenuInfo> list = Lists.newArrayList();
        for (MenuInfo menu : menus) {
            if (menu.valid) {
                list.add(menu);
            }

        }
        return list;
    }

    public FormInfo getFormInfo(String formKey) {
        for (FormInfo fi : forms) {
            if (fi.getId().equals(formKey))
                return fi;
        }
        return null;
    }


}
