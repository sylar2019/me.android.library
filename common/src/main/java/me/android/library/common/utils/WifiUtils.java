package me.android.library.common.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiConfiguration.KeyMgmt;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.MulticastLock;
import android.os.Handler;

import com.google.common.base.Preconditions;

import java.util.List;

/**
 * 需要
 */
public class WifiUtils {

    static public final int CipherType_NONE = 1;
    static public final int CipherType_WEP = 2;
    static public final int CipherType_WAPE_PSK = 3;
    static final String LOCK_MULTICAST = "lock_multicast";
    static MulticastLock multicastLock;

    static WifiManager getMng(Context cx) {
        return (WifiManager) cx.getSystemService(Context.WIFI_SERVICE);
    }

    /**
     * wifi是否可用
     */
    static public boolean isEnabled(Context cx) {
        return getMng(cx).isWifiEnabled();
    }

    /**
     * wifi状态
     */
    static public int getWifiState(Context cx) {
        return getMng(cx).getWifiState();
    }

    /**
     * 打开WIFI
     */
    static public void openWifi(Context cx) {
        getMng(cx).setWifiEnabled(true);
    }

    /**
     * 关闭WIFI
     */
    static public void closeWifi(Context cx) {
        getMng(cx).setWifiEnabled(false);
    }

    /**
     * wifi是否连接
     */
    static public boolean isWifiConnected(Context cx) {
        ConnectivityManager cm = (ConnectivityManager) cx
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        return ni.isConnected();
    }

    /**
     * 比较ssid是否相等
     */
    static public boolean isSameSSID(String ssid1, String ssid2) {
        if (ssid1 == null || ssid2 == null)
            return false;
        String Format = "\"%s\"";

        String str1 = String.format(Format, ssid1);
        String str2 = String.format(Format, ssid2);
        return ssid1.equals(ssid2) || ssid1.equals(str2) || str1.equals(ssid2)
                || str1.equals(str2);
    }

    /**
     * 是否加密
     */
    static public boolean isEncrypted(ScanResult sr) {
        int type = getCipherType(sr);
        return type != CipherType_NONE;
    }

    /**
     * 获取网络安全性
     */
    static public int getCipherType(ScanResult result) {
        if (result.capabilities.contains("WEP")) {
            return CipherType_WEP;
        } else if (result.capabilities.contains("PSK")) {
            return CipherType_WAPE_PSK;
        }
        return CipherType_NONE;
    }

    /**
     * 立刻扫描并返回热点列表
     */
    static public List<ScanResult> getScanResults(Context cx) {
        return getMng(cx).getScanResults();
    }

    /**
     * 允许组播 android 组播需要先解锁，费电
     * 需要权限：android.permission.CHANGE_WIFI_MULTICAST_STATE
     */
    static public void allowMulticast(Context cx) {
        boolean isPermit = PermissionUtils.checkPermission(cx,
                PermissionUtils.CHANGE_WIFI_MULTICAST_STATE);
        Preconditions.checkState(isPermit, "没有组播权限");

        if (multicastLock == null) {
            multicastLock = getMng(cx).createMulticastLock(LOCK_MULTICAST);
            multicastLock.acquire();
        }
    }

    /**
     * 禁用组播
     */
    static public void disableMulticast() {
        if (multicastLock != null && multicastLock.isHeld()) {
            multicastLock.release();
            multicastLock = null;
        }
    }

    /**
     * 获取当前连接的wifiInfo
     */
    static public WifiInfo getCurrentWifiInfo(Context cx) {
        return getMng(cx).getConnectionInfo();
    }

    /**
     * 获取本地 mac 地址
     *
     * @return 若未打开wifi开关或未联网，可能返回null
     */
    static public String getLocalMac(Context cx) {
        WifiInfo wi = getCurrentWifiInfo(cx);
        if (wi != null)
            return wi.getMacAddress();
        else
            return null;
    }

    /**
     * 获取路由 mac 地址
     *
     * @return 若未联网，返回null
     */
    static public String getRouteMac(Context cx) {
        WifiInfo wi = getCurrentWifiInfo(cx);
        if (wi != null)
            return wi.getBSSID();
        else
            return null;
    }

    static public ScanResult getCurrentScanResult(Context cx) {
        WifiInfo wi = getCurrentWifiInfo(cx);
        if (wi != null)
            return getScanResultBySsid(cx, wi.getSSID());
        else
            return null;
    }

    static public String getCurrentSsid(Context cx) {
        String ssid = getMng(cx).getConnectionInfo().getSSID();
        if (ssid.startsWith("\"") && ssid.endsWith("\"")) {
            ssid = ssid.substring(1, ssid.length() - 1);
        }
        return ssid;
    }

    static public ScanResult getScanResultBySsid(Context cx, String ssid) {
        List<ScanResult> list = getScanResults(cx);
        if (list != null && list.size() > 0)
            for (ScanResult sr : list) {
                if (isSameSSID(ssid, sr.SSID))
                    return sr;
            }
        return null;
    }

    static public WifiConfiguration getCurrentConfiguration(Context cx) {
        String ssid = getCurrentWifiInfo(cx).getSSID();
        return getConfigurationBySsid(cx, ssid);
    }

    static public WifiConfiguration getConfigurationBySsid(Context cx,
                                                           String ssid) {
        List<WifiConfiguration> cfgs = getWifiConfigurations(cx);
        if (cfgs != null) {
            for (WifiConfiguration wc : cfgs) {
                if (isSameSSID(ssid, wc.SSID))
                    return wc;
            }
        }
        return null;
    }

    static List<WifiConfiguration> getWifiConfigurations(Context cx) {
        return getMng(cx).getConfiguredNetworks();
    }

    static public boolean connectConfiguration(Context cx, String ssid) {
        WifiConfiguration wc = getConfigurationBySsid(cx, ssid);
        return wc != null && getMng(cx).enableNetwork(wc.networkId, true);
    }

    /**
     * 启动持续扫描
     */
    static public WifiScanner startScan(Context cx, WifiScanCallback callback) {
        WifiScanner scanner = new WifiScanner(cx, callback, true);
        scanner.startScanning();
        return scanner;
    }

    /**
     * 查看扫描结果
     */
    static public StringBuilder lookUpScan(Context cx) {
        StringBuilder stringBuilder = new StringBuilder();
        List<ScanResult> list = getScanResults(cx);
        for (int i = 0; i < list.size(); i++) {
            stringBuilder.append(String.format("Index_%s:", i + 1));
            // 将ScanResult信息转换成一个字符串包
            // 其中把包括：BSSID、SSID、capabilities、frequency、level
            stringBuilder.append((list.get(i)).toString());
            stringBuilder.append("\n");
        }
        return stringBuilder;
    }

    /**
     * 添加一个网络并连接
     */
    static public void addNetwork(Context cx, WifiConfiguration wcg) {
        WifiManager wm = getMng(cx);
        int wcgID = wm.addNetwork(wcg);
        wm.enableNetwork(wcgID, true);
        wm.saveConfiguration();
    }

    /**
     * 添加未加密网络
     */
    static public void addNetworkWithoutCipher(Context cx, String ssid) {
        WifiManager wm = getMng(cx);
        WifiConfiguration config = new WifiConfiguration();
        config.SSID = "\"" + ssid + "\"";
        config.allowedKeyManagement.set(KeyMgmt.NONE);

        int networkId = wm.addNetwork(config);
        if (networkId != -1) {
            wm.enableNetwork(networkId, false);
            wm.saveConfiguration();
        }
    }

    /**
     * 断开指定ID的网络
     */
    static public void disconnectWifi(Context cx, int netId) {
        WifiManager wm = getMng(cx);
        wm.disableNetwork(netId);
        wm.disconnect();
    }

    /**
     * 创建wifi配置信息
     */
    static public WifiConfiguration CreateWifiInfo(Context cx, String SSID,
                                                   String Password, int Type) {
        WifiManager wm = getMng(cx);
        WifiConfiguration config = new WifiConfiguration();
        config.allowedAuthAlgorithms.clear();
        config.allowedGroupCiphers.clear();
        config.allowedKeyManagement.clear();
        config.allowedPairwiseCiphers.clear();
        config.allowedProtocols.clear();
        config.SSID = "\"" + SSID + "\"";

        WifiConfiguration tempConfig = IsExsits(cx, SSID);
        if (tempConfig != null) {
            wm.removeNetwork(tempConfig.networkId);
        }

        if (Type == 1) // WIFICIPHER_NOPASS
        {
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
        }
        if (Type == 2) // WIFICIPHER_WEP
        {
            config.hiddenSSID = true;
            config.wepKeys[0] = "\"" + Password + "\"";
            config.allowedAuthAlgorithms
                    .set(WifiConfiguration.AuthAlgorithm.SHARED);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
            config.allowedGroupCiphers
                    .set(WifiConfiguration.GroupCipher.WEP104);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            config.wepTxKeyIndex = 0;
        }
        if (Type == 3) // WIFICIPHER_WPA
        {
            config.preSharedKey = "\"" + Password + "\"";
            config.hiddenSSID = true;
            config.allowedAuthAlgorithms
                    .set(WifiConfiguration.AuthAlgorithm.OPEN);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
            config.allowedPairwiseCiphers
                    .set(WifiConfiguration.PairwiseCipher.TKIP);
            // config.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            config.allowedPairwiseCiphers
                    .set(WifiConfiguration.PairwiseCipher.CCMP);
            config.status = WifiConfiguration.Status.ENABLED;
        }
        return config;
    }

    /**
     * wifi配置是否存在
     */
    static private WifiConfiguration IsExsits(Context cx, String SSID) {
        WifiManager wm = getMng(cx);
        List<WifiConfiguration> existingConfigs = wm.getConfiguredNetworks();
        for (WifiConfiguration existingConfig : existingConfigs) {
            if (existingConfig.SSID.equals("\"" + SSID + "\"")) {
                return existingConfig;
            }
        }
        return null;
    }

    public interface WifiScanCallback {
        void onScanWifi(List<ScanResult> scanList);
    }

    // -------------------------------------------------------------------
    // static
    // -------------------------------------------------------------------

    // -------------------------------------------------------------------
    // WifiScanner
    // -------------------------------------------------------------------

    static public class WifiScanner extends BroadcastReceiver {

        private Context cx;
        private Handler handler;
        private WifiManager wifiMgr;
        private boolean isRepeat, isScanning;
        private WifiScanCallback callback;

        public WifiScanner(Context cx, WifiScanCallback callback,
                           boolean isRepeat) {
            this.cx = cx;
            this.wifiMgr = ((WifiManager) cx.getSystemService(Context.WIFI_SERVICE));
            this.callback = callback;
            this.isRepeat = isRepeat;

            this.handler = new Handler(cx.getMainLooper());
        }

        public WifiScanner(Context cx, WifiScanCallback callback) {
            this(cx, callback, false);
        }

        public boolean isScanning() {
            return this.isScanning;
        }

        public void startScanning() {
            if (isScanning)
                return;

            this.isScanning = true;
            this.cx.registerReceiver(this, new IntentFilter(
                    "android.net.wifi.SCAN_RESULTS"));
            this.wifiMgr.startScan();
        }

        public void stopScanning() {
            this.cx.unregisterReceiver(this);
            this.isScanning = false;
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            this.callback.onScanWifi(wifiMgr.getScanResults());
            if (isRepeat) {

                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        wifiMgr.startScan();
                    }
                }, 1000);
            } else {
                stopScanning();
            }
        }
    }

}
