package me.android.library.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;

import androidx.fragment.app.FragmentActivity;

import com.google.common.eventbus.Subscribe;

import me.android.library.common.App;
import me.android.library.common.enums.ConnectivityMode;
import me.android.library.common.event.ActivityResultEvent;
import me.android.library.common.event.ConnectionModeChangedEvent;
import me.android.library.common.utils.MemoryUtils;
import me.java.library.utils.base.guava.AsyncEventUtils;


public abstract class AbstractActivity extends FragmentActivity implements Form {

    public final static String WillShowPageKey = "WillShowPageKey";

    protected App app;
    protected String formKey;
    protected boolean isExit = false;
    protected Handler mainHandler = new Handler(Looper.getMainLooper());

    abstract protected String createFormKey();

    @Override
    public String getFormKey() {
        return formKey;
    }

    @Override
    protected void onCreate(Bundle savedState) {
        super.onCreate(savedState);
        Log.i("app", "Activity onCreate:" + this.getClass().getSimpleName());
        AsyncEventUtils.regist(this);

        Bundle bd = getIntent().getExtras();
        String pageKey = null;
        if (bd != null) {
            pageKey = bd.getString(WillShowPageKey);
        }

        formKey = createFormKey();
        app = (App) getApplication();
        app.addActivity(this);

        requestWindowFeature();
        setContentView();
        attachActivity(pageKey);
        initOnCreate();

        if (savedState != null) {
            restoreState(savedState);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        UIService.getInstance().setTopActivity(formKey);
        Log.d("app", "top key:" + formKey);
    }

    @Override
    protected void onDestroy() {
        app.removeActivity(this);
        AsyncEventUtils.unregist(this);
        UIService.getInstance().detachActivity(formKey);
        View rootView = getWindow().getDecorView().findViewById(android.R.id.content);
        MemoryUtils.disposeView(rootView);
        super.onDestroy();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ActivityResultEvent.ActivityResult activityResult = new ActivityResultEvent.ActivityResult(requestCode, resultCode, data);
        AsyncEventUtils.postEvent(new ActivityResultEvent(this, activityResult));
    }

    @Subscribe
    public void onEvent(ConnectionModeChangedEvent event) {
        onConnectionModeChanged(event.getContent());
    }

    protected void onConnectionModeChanged(ConnectivityMode mode) {
    }

    // -------------------------------------------------------------------------------
    // onCreate
    // -------------------------------------------------------------------------------

    protected void requestWindowFeature() {
    }

    protected void setContentView() {
        // setContentView(R.layout.activity_layout);
    }

    protected void attachActivity(String pageKey) {
        UIService.getInstance().attachActivity(formKey, this, pageKey);
    }

    protected void initOnCreate() {
    }

    protected void restoreState(Bundle savedState) {
    }

    // -------------------------------------------------------------------------------
    // onKeyDown
    // -------------------------------------------------------------------------------

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        Page page = UIService.getInstance().getFormManager(formKey).getCurrentPage();
        if (page != null && page.onKeyDown(keyCode, event)) {
            return true;
        } else {
            switch (keyCode) {
                case KeyEvent.KEYCODE_MENU:
                    onKeyDown_Menu();
                    return true;
                case KeyEvent.KEYCODE_BACK:
                    onKeyDown_Back();
                    return true;
                default:
                    return super.onKeyDown(keyCode, event);
            }
        }
    }

    protected void onKeyDown_Menu() {
        FormManager fm = UIService.getInstance().getFormManager(formKey);
        if (fm != null) {
            boolean isHome = fm.isHome();
            if (isHome) {
                fm.toggleMenu();
            }
        }

    }

    protected void onKeyDown_Back() {

        FormManager fm = UIService.getInstance().getTop();
        if (fm == null) {
            exit();
        } else {
            if (fm.isHome()) {
                exit();
            } else {
                UIService.getInstance().popBack();
            }
        }

    }

    protected void exit() {

        if (!isExit) {
            isExit = true;

            showTipWhenExit();

            mainHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    isExit = false;
                }
            }, 2000);


        } else {
            finish();
            app.exit();
        }
    }

    protected void showTipWhenExit() {
        // ToastUtils.showShort(R.string.app_exit);
    }

    protected void postEvent(Object event) {
        AsyncEventUtils.postEvent(event);
    }
}
