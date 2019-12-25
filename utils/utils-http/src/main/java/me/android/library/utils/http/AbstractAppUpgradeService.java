package me.android.library.utils.http;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;

import com.google.common.base.Preconditions;

import java.io.File;

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

    public void checkAndUpgrade(Activity activity, Callback<AppVersion> callback) {
        checkVersion(new Callback<AppVersion>() {
            @Override
            public void onSuccess(AppVersion appVer) {
                int curCode = PackageUtils.getVersionCode(cx);

                try {
                    if (appVer.getVersionCode() > curCode) {
                        onNewest(activity, appVer);
                    } else {
                        onWithout();
                    }

                    callback.onSuccess(appVer);
                } catch (Exception e) {
                    callback.onFailure(e);
                }
            }

            @Override
            public void onFailure(Throwable t) {
                onCheckFailure(t);
                callback.onFailure(t);
            }
        });
    }

    public void installFromLocal(Activity activity, File file) {
        Preconditions.checkNotNull(file, "安装文件无效");
        Preconditions.checkState(file.getName().toLowerCase().endsWith(".apk"), "不是安装文件");
        String authority = getProviderAuthority();
        PackageUtils.installApk(activity, file, authority);

    }

    protected void onNewest(Activity activity, AppVersion appVer) throws Exception {
        // 有新版本
        download(activity, appVer);
    }

    protected void onWithout() {
        // 没有新版本
        ToastUtils.showShort("当前已是最新版");
    }

    protected void onCheckFailure(Throwable t) {
        // 检查版本出错
        ToastUtils.showShort("检查更新失败");
    }

    protected void download(Activity activity, AppVersion appVer) throws Exception {
        DownloadService.newFileDownloadTask(activity,
                getClass().getSimpleName(),
                appVer.getDownloadUrl(),
                uri -> afterDownload(activity, appVer, uri)).download(apkName, "app download...");
    }

    protected void afterDownload(Activity activity, AppVersion appVer, Uri uri) {
        installFromLocal(activity, new File(uri.getPath()));
    }

    protected String getProviderAuthority() {
        return String.format("%s.provider", PackageUtils.getApplicationId(cx));
    }
}
