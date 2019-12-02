package me.android.library.utils.http;

import android.content.Context;

import me.android.library.common.service.AbstractService;
import me.android.library.common.utils.PackageUtils;
import me.android.library.common.utils.ToastUtils;
import me.java.library.common.Callback;

/**
 * Created by sylar on 15/8/17.
 */
public abstract class AbstractAppUpgradeService extends AbstractService {

    protected String apkName;

    public abstract void checkVersion(Callback<AppVersion> callback);

    @Override
    public void init(Context cx, Object... params) {
        super.init(cx, params);
        apkName = cx.getPackageName() + ".apk";
    }

    public void checkAndUpgrade() {
        checkVersion(new Callback<AppVersion>() {
            @Override
            public void onSuccess(AppVersion appVer) {
                int curCode = PackageUtils.getVersionCode(cx);

                if (appVer.getVersionCode() > curCode) {
                    onNewest(appVer);
                } else {
                    onWithout();
                }
            }

            @Override
            public void onFailure(Throwable t) {
                onCheckFailure(t);
            }
        });
    }


    protected void onNewest(AppVersion appVer) {
        // 有新版本
        download(appVer.getDownloadUrl(), "版本更新", null);
    }

    protected void download(String downUrl, String title, String description) {
        try {
            DownloadService.newAppDownloadTask(cx, getClass().getSimpleName(),
                    downUrl).download(apkName, title, description);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void onWithout() {
        // 没有新版本
        ToastUtils.showShort("当前已是最新版");
    }

    protected void onCheckFailure(Throwable t) {
        // 检查版本出错
        ToastUtils.showShort("检查更新失败");
    }
}
