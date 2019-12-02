package me.android.library.ui;

import android.os.Bundle;

import androidx.fragment.app.FragmentActivity;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;

import java.util.List;
import java.util.Map;

import me.android.library.common.service.AbstractService;
import me.android.library.common.utils.ResourcesUtils;
import me.android.library.ui.pojo.FormInfo;
import me.android.library.ui.pojo.MenuInfo;
import me.android.library.ui.pojo.PageInfo;
import me.android.library.ui.pojo.UIConfig;

public class UIService extends AbstractService {

    private UIConfig config;
    private Map<String, Page> pageMap;
    private Map<String, FormManager> formMap;
    private String topFormKey;

    private UIService() {
        pageMap = Maps.newHashMap();
        formMap = Maps.newHashMap();
    }

    public static UIService getInstance() {
        return UIService.SingletonHolder.instance;
    }

    public void loadConfig(int configResId) {
        loadConfig(ResourcesUtils.raw2String(configResId));
    }

    public void loadConfig(String content) {

        if (config != null)
            return;

        config = UIConfig.loadConfig(content);
        Preconditions.checkNotNull(config, "UIConfig is null");
    }

    public FormManager getMain() {
        FormManager fm = getFormManager(config.getMainFormKey());
        Preconditions.checkNotNull(fm, "main FormManager is null");
        return fm;
    }

    public FormManager getTop() {
        if (Strings.isNullOrEmpty(topFormKey))
            return getMain();
        else
            return getFormManager(topFormKey);
    }

    public FormManager getFormManager(String formKey) {
        FormManager fm = formMap.get(formKey);
        return fm;
    }

    public LayoutLoader getLoader() {
        return getTop().getLoader();
    }

    public LayoutLoader getLoader(String formKey) {
        return getFormManager(formKey).getLoader();
    }

    public List<MenuInfo> getMenus() {
        return config.getValidMenus();
    }

    public boolean isMainForm() {
        boolean isMain = Objects.equal(getTop().getFormKey(),
                config.getMainFormKey());
        return isMain;
    }

    public boolean isHome() {
        return getTop().isHome();
    }

    public boolean isHomePage(String pageKey) {
        return getTop().isHomePage(pageKey);
    }

    public boolean isCurrentPage(String pageKey) {
        return Objects.equal(getTop().getCurrentPageKey(), pageKey);
    }

    public void attachActivity(String formKey, FragmentActivity main) {
        attachActivity(formKey, main, null);
    }

    public void attachActivity(String formKey, FragmentActivity main,
                               String pageKey) {
        if (Strings.isNullOrEmpty(formKey))
            return;

        FormInfo fi = getFormInfo(formKey);
        if (fi == null)
            return;

        setTopActivity(formKey);
        FormManager fm = new FormManager(fi);
        formMap.put(formKey, fm);

        fm.attachActivity(main, pageKey);
    }

    public void detachActivity(String formKey) {
        if (Strings.isNullOrEmpty(formKey))
            return;

        FormManager fm = getFormManager(formKey);
        if (fm != null) {
            fm.detachActivity();
        }
        formMap.remove(formKey);
    }

    public void setTopActivity(String formKey) {
        if (Strings.isNullOrEmpty(formKey))
            return;

        topFormKey = formKey;
    }

    public UIService postPage(String pageKey) {
        return postPage(pageKey, null);
    }

    public UIService postPage(String pageKey, Bundle args) {
        getTop().postPage(pageKey, args);
        return this;
    }

    public UIService popBack() {
        getTop().popBack();
        return this;
    }

    public UIService returnHome() {
        getTop().returnHome();
        return this;
    }

    public void toggleMenu() {
        getTop().toggleMenu();
    }

    public Page createPage(String pageKey) {

        PageInfo pi = getPageInfo(pageKey);

        Page page = null;
        try {
            page = pi.getPage();
            pageMap.put(pageKey, page);

        } catch (Exception e) {
            e.printStackTrace();
        }

        Preconditions.checkNotNull(page, "invalid page:" + pageKey);
        return page;
    }

    public Page getPage(String pageKey) {
        return pageMap.get(pageKey);
    }

    public PageInfo getPageInfo(String pageKey) {
        PageInfo pi = config.getPageInfo(pageKey);
        Preconditions.checkNotNull(pi, "invalid pageKey:" + pageKey);
        return pi;
    }

    void removePage(String pageKey) {
        pageMap.remove(pageKey);
    }

    private FormInfo getFormInfo(String formKey) {
        if (config == null)
            return null;
        return config.getFormInfo(formKey);
    }

    private static class SingletonHolder {
        private static UIService instance = new UIService();
    }

}
