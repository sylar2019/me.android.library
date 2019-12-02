package me.android.library.common.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import me.android.library.common.enums.ConnectivityMode;
import me.android.library.common.event.ConnectionModeChangedEvent;
import me.android.library.common.utils.NetworkUtils;

public class ConnectivtyService extends AbstractService {
    static final String TAG = "app";
    static final String CONNECTIVITY_CHANGE = ConnectivityManager.CONNECTIVITY_ACTION;
    private ConnectivityManager cm;
    private BroadcastReceiver receiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            checkConnectivty();
        }
    };

    private ConnectivtyService() {
    }

    public static ConnectivtyService getInstance() {
        return SingletonHolder.instance;
    }

    @Override
    public void init(Context cx, Object... params) {
        super.init(cx, params);
        cm = (ConnectivityManager) cx
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        cx.registerReceiver(receiver, new IntentFilter(CONNECTIVITY_CHANGE));
    }

    @Override
    public void dispose() {
        super.dispose();
        cx.unregisterReceiver(receiver);
    }

    public void checkConnectivty() {
        ConnectivityMode mode = ConnectivityMode.Broken;
        NetworkInfo netInfo;
        boolean useWifi = false;
        boolean useMobile = false;
        try {
            netInfo = cm.getActiveNetworkInfo();
            if (netInfo != null) {
                useWifi = netInfo.getType() == ConnectivityManager.TYPE_WIFI;
                useMobile = netInfo.getType() == ConnectivityManager.TYPE_MOBILE;
            }

            if (useWifi) {
                mode = ConnectivityMode.Wifi;
                String localIP = NetworkUtils.getLocalIp();
                String gateIP = NetworkUtils.getGatewayIpAddress(cx);

                Log.d(TAG, String.format("[wifi] Gate:%s Local:%s", gateIP,
                        localIP));

            } else if (useMobile) {
                mode = ConnectivityMode.GSM;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            postEvent(new ConnectionModeChangedEvent(mode));
        }

    }

    private static class SingletonHolder {
        private static ConnectivtyService instance = new ConnectivtyService();
    }

}
