package me.android.library.ui.ext;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import me.android.library.ui.AbstractPage;
import me.android.library.ui.R;
import me.android.library.ui.ext.views.TitleBar;

/**
 * HeadPage, 使页面背景图片与 TitleBar 融为一体
 *
 * @author sylar
 */
abstract public class HeadPage extends AbstractPage {

    protected View rootView;
    protected TitleBar titleBar;
    protected View contentView;
    protected FrameLayout pnlMain;
    protected boolean isOverlayTitleBackground = true;

    protected abstract View onCreateContentView(LayoutInflater inflater,
                                                ViewGroup container, Bundle savedInstanceState);

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.abs_header_page, container, false);
        titleBar = rootView.findViewById(R.id.titleBar);
        pnlMain = rootView.findViewById(R.id.pnlMain);
        contentView = onCreateContentView(inflater, pnlMain, savedInstanceState);
        pnlMain.addView(contentView);

        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setRootBg();
        setTitleBg();

        Drawable dr = contentView.getBackground();
        if (isOverlayTitleBackground && dr != null) {
            rootView.setBackgroundDrawable(dr);
            contentView.setBackgroundDrawable(null);
        }
    }

    public TitleBar getTitleBar() {
        return titleBar;
    }

    protected void setRootBg() {
        setRootBgRes(R.drawable.default_background);
    }

    protected void setTitleBg() {
        setTitleBgRes(R.drawable.default_background_title);
    }

    protected void setRootBgColor(int color) {
        rootView.setBackgroundColor(color);
    }

    protected void setRootBgRes(int resid) {
        rootView.setBackgroundResource(resid);
    }

    protected void setTitleBgColor(int color) {
        titleBar.setBackgroundColor(color);
    }

    protected void setTitleBgRes(int resid) {
        titleBar.setBackgroundResource(resid);
    }

}
