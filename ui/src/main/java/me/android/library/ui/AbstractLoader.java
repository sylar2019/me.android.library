package me.android.library.ui;

import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

import me.android.library.common.service.AbstractService;
import me.android.library.ui.pojo.PageInfo;


abstract public class AbstractLoader extends AbstractService implements LayoutLoader {

    static final String TAG = me.android.library.ui.UI.TAG;

    protected FragmentActivity main;
    protected FragmentManager fm;
    protected Resources r;
    protected String pageKey;
    protected String loaderKey;

    public void setKey(String key) {
        this.loaderKey = key;
    }

    @Override
    public void layout(FragmentActivity main) {
        this.main = main;
        this.fm = main.getSupportFragmentManager();
        this.r = main.getResources();
    }

    synchronized protected Page addFragment(int containerResId,
                                            String pageKey, Bundle args) {

        Preconditions.checkNotNull(pageKey);
        Page page = UIService.getInstance().createPage(pageKey);
        Fragment frm = (Fragment) page;

        String frmTag = pageKey;

        if (fm.findFragmentByTag(frmTag) != null) {
            Log.d(TAG, String.format("page exist: %s", pageKey));
            return page;
        }

        if (args != null) {
            frm.setArguments(args);
        }

        FragmentTransaction ft = fm.beginTransaction();
        setPageAnim(pageKey, ft);

        boolean isAdded = frm.isAdded();
        if (isAdded) {
            ft.detach(frm).add(containerResId, frm, frmTag)
                    .addToBackStack(frmTag).attach(frm)
                    .commitAllowingStateLoss();

            Log.d(TAG,
                    String.format("page added: %s", pageKey));
        } else {
            ft.add(containerResId, frm, frmTag).addToBackStack(frmTag)
                    .commitAllowingStateLoss();

            Log.d(TAG, String.format("page add: %s", pageKey));
        }

        return page;
    }

    @Override
    public void onPageInActivated(String pageKey) {

    }

    @Override
    public void onPageActivated(String pageKey) {
        this.pageKey = pageKey;
    }

    @Override
    public String toString() {
        if (!Strings.isNullOrEmpty(loaderKey)) {
            return loaderKey;
        } else {
            return getClass().getName();
        }
    }

    void setPageAnim(String pageKey, FragmentTransaction ft) {
        PageInfo pi = UIService.getInstance().getPageInfo(pageKey);
        int pageIn = pi.getAnimInResId(cx);
        int pageOut = pi.getAnimOutResId(cx);

        int animIn = pageIn > 0 ? pageIn : android.R.anim.fade_in;
        int animOut = pageOut > 0 ? pageOut : android.R.anim.fade_out;

        ft.setTransitionStyle(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.setCustomAnimations(animIn, animOut);
    }

}
