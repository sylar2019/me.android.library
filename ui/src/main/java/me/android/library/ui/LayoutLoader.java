package me.android.library.ui;

import android.os.Bundle;

import androidx.fragment.app.FragmentActivity;

public interface LayoutLoader {

    void layout(FragmentActivity main);

    boolean toggleMenu();

    Page switchContent(String pageKey, Bundle args);

    void onPageInActivated(String pageKey);

    void onPageActivated(String pageKey);
}
