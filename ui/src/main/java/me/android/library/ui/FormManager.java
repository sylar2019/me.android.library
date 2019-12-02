package me.android.library.ui;

import android.os.Bundle;
import android.util.Log;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.common.base.Objects;
import com.google.common.base.Strings;

import me.android.library.common.utils.SoftInputUtils;
import me.android.library.ui.event.MenuToggledEvent;
import me.android.library.ui.event.PageChangedEvent;
import me.android.library.ui.pojo.FormInfo;
import me.java.library.utils.base.guava.AsyncEventUtils;

public class FormManager {

    private String formKey, homePageKey, curPageKey;
    private LayoutLoader loader;
    private FragmentActivity main;
    private FragmentManager frmMng;
    private boolean isShownMenu;
    private FragmentManager.OnBackStackChangedListener backStackListener = new FragmentManager.OnBackStackChangedListener() {

        @Override
        public void onBackStackChanged() {
            onFragmentChanged();
        }
    };

    public FormManager(FormInfo formInfo) {
        formKey = formInfo.getId();
        homePageKey = formInfo.homePageKey;
        loader = formInfo.getLoader();
    }

    public FragmentActivity getActivity() {
        return main;
    }

    public LayoutLoader getLoader() {
        return loader;
    }

    public String getFormKey() {
        return formKey;
    }

    public String getHomeKey() {
        return homePageKey;
    }

    public String getCurrentPageKey() {
        return curPageKey;
    }

    public Page getCurrentPage() {
        return getCachedPage(curPageKey);
    }

    public boolean isHome() {
        return isHomePage(curPageKey);
    }

    public boolean isHomePage(String pageKey) {
        return homePageKey.equals(pageKey);
    }

    public boolean isShownMenu() {
        return isShownMenu;
    }

    public boolean toggleMenu() {
        isShownMenu = loader.toggleMenu();
        AsyncEventUtils.postEvent(new MenuToggledEvent(loader, isShownMenu));
        return isShownMenu;
    }

    public void attachActivity(FragmentActivity main, String firstPageKey) {

        this.main = main;
        frmMng = this.main.getSupportFragmentManager();

        int backStackCount = frmMng.getBackStackEntryCount();
        for (int i = 0; i < backStackCount; i++) {
            frmMng.popBackStackImmediate();
        }

        frmMng.removeOnBackStackChangedListener(backStackListener);
        frmMng.addOnBackStackChangedListener(backStackListener);

        Log.i("app", "stack count of page:" + frmMng.getBackStackEntryCount());

        if (loader instanceof AbstractLoader) {
            ((AbstractLoader) loader).init(main);
        }

        loader.layout(main);

        postPage(homePageKey, null);
        if (!Strings.isNullOrEmpty(firstPageKey)) {
            postPage(firstPageKey, null);
        }

    }

    public void detachActivity() {
    }

    synchronized public void postPage(String pageKey) {
        postPage(pageKey, null);
    }

    synchronized public void postPage(String pageKey, Bundle args) {
        loader.switchContent(pageKey, args);
    }

    synchronized public void popBack() {

        SoftInputUtils.hide(main);

        int count = frmMng.getBackStackEntryCount();
        if (count > 1) {
            Log.d("fragment", String.format("page removed: %s", curPageKey));

            FragmentTransaction ft = frmMng.beginTransaction();
            Page page = getCachedPage(curPageKey);
            if (page instanceof Fragment) {
                removePage(curPageKey, ft, (Fragment) page);
            }
            frmMng.popBackStack();
            ft.commitAllowingStateLoss();

        } else {
            main.finish();
        }
    }

    synchronized public void returnHome() {

        if (isHome())
            return;

        int count = frmMng.getBackStackEntryCount();

        // 回退至主界面，并释放其它界面
        if (count > 0) {
            FragmentTransaction ft = frmMng.beginTransaction();
            String frmTag;
            for (int i = count - 1; i >= 0; i--) {
                frmTag = frmMng.getBackStackEntryAt(i).getName();
                if (Strings.isNullOrEmpty(frmTag))
                    continue;
                if (Objects.equal(homePageKey, frmTag))
                    continue;

                Fragment frm = frmMng.findFragmentByTag(frmTag);
                removePage(frmTag, ft, frm);
            }

            frmMng.popBackStack(homePageKey, 0);  // 主界面不弹出，仍留在堆栈中
            ft.commitAllowingStateLoss();
        }

        // 若堆栈中无主界面，则清空堆栈，并加载主界面
        if (frmMng.findFragmentByTag(homePageKey) == null) {
            frmMng.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            postPage(homePageKey);
        }
    }

    private void onFragmentChanged() {

        SoftInputUtils.hide(main);

        int count = frmMng.getBackStackEntryCount();
        Log.d("fragment", String.format("page count: %s", count));

        if (count == 0)
            return;

        String pageKey = frmMng.getBackStackEntryAt(count - 1).getName();
        Log.d("fragment", String.format("page current: %s", pageKey));

        loader.onPageInActivated(curPageKey);
        Page prePage = getCachedPage(curPageKey);
        if (prePage != null) {
            prePage.onPageInActivated();
        }

        curPageKey = pageKey;
        loader.onPageActivated(curPageKey);
        Page curPage = getCachedPage(curPageKey);
        curPage.onPageActivated();

        AsyncEventUtils.postEvent(new PageChangedEvent(loader, curPageKey));
    }

    private Page getCachedPage(String pageKey) {
        return UIService.getInstance().getPage(pageKey);
    }

    private void removePage(String pageKey, FragmentTransaction ft, Fragment frm) {
        if (frm != null) {
            if (frm instanceof Page) {
                ((Page) frm).onPageInActivated();
            }

            ft.remove(frm);
        }

        UIService.getInstance().removePage(pageKey);
    }
}
