package me.android.library.utils.http;

import android.app.Activity;
import android.app.DownloadManager;
import android.app.DownloadManager.Request;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.webkit.MimeTypeMap;

import java.util.Objects;

import me.android.library.common.utils.PackageUtils;
import me.android.library.common.utils.PermissionUtils;
import me.java.library.common.Callback;

public class DownloadService {

    private final static String SubPath = Environment.DIRECTORY_DOWNLOADS;

    static public DownloadTask newFileDownloadTask(Context cx,
                                                   String key,
                                                   String url,
                                                   DownloadListener listener) {
        return new DownloadTask(cx, key, url, listener);
    }

    static public AppDownloadTask newAppDownloadTask(Context cx,
                                                     String key,
                                                     String url) {

        return new AppDownloadTask(cx, key, url);
    }

    public interface DownloadListener {
        void onDownloadCompleted(Uri uri);
    }

    static public class AppDownloadTask extends DownloadTask {

        public AppDownloadTask(Context cx, String key, String downUrl) {
            super(cx, key, downUrl, null);
        }

        @Override
        protected void onFinished(Uri uri) {
            if (uri == null)
                return;

            if (cx instanceof Activity) {
                Activity activity = (Activity) cx;
                PackageUtils.installApk(activity, uri);
            } else {
                PackageUtils.installApk(cx, uri);
            }
        }
    }

    static public class DownloadTask extends BroadcastReceiver {

        Context cx;
        DownloadManager dm;
        SharedPreferences prefs;

        String key;
        String downUrl;

        boolean hasPermit;
        DownloadListener listener;

        public DownloadTask(Context cx, String key, String downUrl,
                            DownloadListener listener) {
            this.cx = cx;
            this.key = key;
            this.downUrl = downUrl;
            this.listener = listener;

            dm = (DownloadManager) cx
                    .getSystemService(Context.DOWNLOAD_SERVICE);
            prefs = PreferenceManager.getDefaultSharedPreferences(cx);

            hasPermit = PermissionUtils.checkPermission(cx,
                    PermissionUtils.ACCESS_DOWNLOAD_MANAGER);
        }

        @Override
        public void onReceive(Context cx, Intent intent) {
            // 这里可以取得下载的id，这样就可以知道哪个文件下载完成了。适用与多个下载任务的监听
            long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0);
            if (prefs.getLong(key, 0) == id) {
                if (Objects.equals(intent.getAction(), DownloadManager.ACTION_DOWNLOAD_COMPLETE)) {
                    cx.unregisterReceiver(this);
                    Uri uri = dm.getUriForDownloadedFile(id);
                    onFinished(uri);
                }
            }
        }

        protected void onFinished(Uri uri) {
            if (listener != null) {
                listener.onDownloadCompleted(uri);
            }
        }

        public void download(String fileName) throws Exception {
            download(fileName, null);
        }

        public void download(String fileName, String titleOnNotification)
                throws Exception {

            if (hasPermit)
                download(fileName, titleOnNotification, null);
            else
                downloadByHttp(fileName);
        }

        public void download(String fileName, String titleOnNotification,
                             String descriptionOnNotification) throws Exception {

            String url = downUrl;
            Uri uri = Uri.parse(url);

            DownloadManager.Request request = new DownloadManager.Request(uri);
            request.setAllowedNetworkTypes(Request.NETWORK_MOBILE
                    | Request.NETWORK_WIFI);

            request.allowScanningByMediaScanner();
            request.setAllowedOverRoaming(true);
            // 设置文件类型
            MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
            String mimeString = mimeTypeMap
                    .getMimeTypeFromExtension(MimeTypeMap
                            .getFileExtensionFromUrl(url));
            request.setMimeType(mimeString);
            // 在通知栏中显示
            request.setNotificationVisibility(Request.VISIBILITY_VISIBLE);
            // request.setShowRunningNotification(true);// 默认是true，改为false需要权限
            request.setVisibleInDownloadsUi(true);
            request.setTitle(titleOnNotification);
            request.setDescription(descriptionOnNotification);

            // sdcard的目录下的download文件夹
            request.setDestinationInExternalPublicDir(SubPath, fileName);

            cx.registerReceiver(this, new IntentFilter(
                    DownloadManager.ACTION_DOWNLOAD_COMPLETE));

            dm = (DownloadManager) cx
                    .getSystemService(Context.DOWNLOAD_SERVICE);

            try {
                long id = dm.enqueue(request);

                // 保存id
                prefs = PreferenceManager.getDefaultSharedPreferences(cx);
                prefs.edit().putLong(key, id).commit();
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        }

        private void downloadByHttp(String fileName) {

            RestfulService.getInstance().downFile(downUrl, fileName,
                    new Callback<Uri>() {

                        @Override
                        public void onSuccess(Uri uri) {
                            onFinished(uri);
                        }

                        @Override
                        public void onFailure(Throwable t) {
                            t.printStackTrace();
                        }
                    });

        }
    }

}
