package me.android.library.ui;

import android.view.KeyEvent;

public interface Page {

    String getPageKey();

    void setPageKey(String pageKey);

    String getPageTitle();

    void setPageTitle(String pageTitle);

    void onPageInActivated();

    void onPageActivated();

    boolean onKeyDown(int keyCode, KeyEvent event);

}