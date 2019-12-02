package me.android.library.ui;


import android.content.Context;

public class DefaultMenuListener implements MenuListener {

    private DefaultMenuListener() {
    }

    public static DefaultMenuListener getInstance() {
        return DefaultMenuListener.SingletonHolder.instance;
    }

    @Override
    public void onMenuClick(Context cx, String menuKey) {

        String pageKey = menuKey;
        UIService.getInstance().postPage(pageKey, null);
    }

    private static class SingletonHolder {
        private static DefaultMenuListener instance = new DefaultMenuListener();
    }

}
