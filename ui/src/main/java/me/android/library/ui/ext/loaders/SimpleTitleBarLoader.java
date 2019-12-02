package me.android.library.ui.ext.loaders;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import me.android.library.ui.Page;
import me.android.library.ui.R;
import me.android.library.ui.UIService;
import me.android.library.ui.ext.views.TitleBar;

/**
 * Created by sylar on 15/7/26.
 */
public class SimpleTitleBarLoader extends AbstractTitleBarLoader {


    @Override
    protected void onPageActivated(TitleBar bar, String pageKey) {
        Page page = UIService.getInstance().getPage(pageKey);

        // 设置中间标题文本
        bar.setTitle(page.getPageTitle());

        //设置左侧菜单icon
        View iconView = getMenuIcon(bar.getContext());
        if (iconView != null) {
            bar.replaceLeft(iconView);
        }
    }

    protected View getMenuIcon(Context cx) {

        // 设置menu图标
        final boolean isMainForm = UIService.getInstance().isMainForm();
        final boolean isHome = UIService.getInstance().isHomePage(pageKey);
        int iconResid = (isMainForm && isHome) ? getMenuIconResid() : getBackIconResid();

        if (iconResid == 0) return null;

        ImageView view = TitleBar.newTitleIconView(cx, 0, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isMainForm) {
                    if (isHome)
                        UIService.getInstance().getMain().toggleMenu();
                    else
                        UIService.getInstance().popBack();
                } else {
                    if (isHome)
                        UIService.getInstance().getMain().getActivity().finish();
                    else
                        UIService.getInstance().popBack();
                }
            }
        });


        view.setImageResource(iconResid);

        return view;
    }

    protected int getMenuIconResid() {
        return R.mipmap.ic_titlebar_menu;
    }

    protected int getBackIconResid() {
        return R.mipmap.ic_titlebar_return;
    }

}
