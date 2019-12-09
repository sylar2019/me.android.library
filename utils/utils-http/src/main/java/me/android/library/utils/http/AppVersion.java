package me.android.library.utils.http;

/**
 * Created by sylar on 16/8/17.
 */
public interface AppVersion {

    int getTerminalType();

    int getProductType();

    int getVersionCode();

    String getVersionName();

    String getVersionDescription();

    String getDownloadUrl();
}
