package me.android.library.ui.ext;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;

import me.android.library.ui.AbstractPage;
import me.android.library.ui.R;

public class BasePage extends AbstractPage {

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Drawable dr = rootView.getBackground();
        if (dr == null) {
            setRootBg();
        }
    }

    protected void setRootBg() {
        setRootBgRes(R.drawable.default_background);
    }

    protected void setRootBgColor(final int color) {
        if (rootView != null) {
            rootView.setBackgroundColor(color);
        }
    }

    protected void setRootBgRes(final int resid) {
        if (rootView != null) {
            rootView.setBackgroundResource(resid);
        }
    }

}
