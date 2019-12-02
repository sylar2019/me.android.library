package me.android.library.ui.ext.loaders;

import android.os.Bundle;

import me.android.library.ui.AbstractLoader;
import me.android.library.ui.Page;
import me.android.library.ui.R;
import me.android.library.ui.UIService;
import me.android.library.ui.ext.HeadPage;
import me.android.library.ui.ext.views.TitleBar;

abstract public class AbstractTitleBarLoader extends AbstractLoader {

    abstract protected void onPageActivated(TitleBar bar, String pageKey);

    @Override
    public boolean toggleMenu() {
        return false;
    }

    @Override
    public Page switchContent(String pageKey, Bundle args) {
        return addFragment(R.id.main_fragment, pageKey, args);
    }

    @Override
    public void onPageActivated(String pageKey) {
        super.onPageActivated(pageKey);
        Page page = UIService.getInstance().getPage(pageKey);
        if (page instanceof HeadPage) {

            HeadPage hp = (HeadPage) page;
            TitleBar bar = hp.getTitleBar();
            if (bar != null) {
                onPageActivated(bar, pageKey);
            }
        }
    }


}