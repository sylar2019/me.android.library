package me.android.library.common.utils;

import android.content.Context;
import android.telephony.CellLocation;
import android.telephony.TelephonyManager;
import android.telephony.cdma.CdmaCellLocation;
import android.telephony.gsm.GsmCellLocation;

/**
 * 需要权限 ： <uses-permission android:name="android.permission.READ_PHONE_STATE"/>
 *
 * @author sylar
 */
public class TelephonyUtils {

    static public String getPhoneNumber(Context cx) {
        if (!checkPermission(cx))
            return null;

        TelephonyManager tm = (TelephonyManager) cx
                .getSystemService(Context.TELEPHONY_SERVICE);
        return tm.getLine1Number();
    }

    static public String getIMEI(Context cx) {
        if (!checkPermission(cx))
            return null;

        TelephonyManager tm = (TelephonyManager) cx
                .getSystemService(Context.TELEPHONY_SERVICE);
        return tm.getDeviceId();
    }

    static public String getTelephonyProperties(Context cx) {
        if (!checkPermission(cx))
            return null;

        TelephonyManager tm = (TelephonyManager) cx
                .getSystemService(Context.TELEPHONY_SERVICE);

        String RN = System.getProperty("line.separator");

        StringBuilder sb = new StringBuilder();
        sb.append("telephony properties:").append(RN);
        sb.append("====================:").append(RN);
        sb.append(String.format("getCallState: %s", tm.getCallState())).append(
                RN);
        sb.append(String.format("getDataActivity: %s", tm.getDataActivity()))
                .append(RN);
        sb.append(String.format("getDataState: %s", tm.getDataState())).append(
                RN);
        sb.append(String.format("getDeviceId: %s", tm.getDeviceId()))
                .append(RN);
        sb.append(
                String.format("getDeviceSoftwareVersion: %s",
                        tm.getDeviceSoftwareVersion())).append(RN);
        sb.append(String.format("getLine1Number: %s", tm.getLine1Number()))
                .append(RN);
        sb.append(
                String.format("getNeighboringCellInfo: %s",
                        tm.getNeighboringCellInfo())).append(RN);
        sb.append(
                String.format("getNetworkCountryIso: %s",
                        tm.getNetworkCountryIso())).append(RN);
        sb.append(
                String.format("getNetworkOperator: %s", tm.getNetworkOperator()))
                .append(RN);
        sb.append(
                String.format("getNetworkOperatorName: %s",
                        tm.getNetworkOperatorName())).append(RN);
        sb.append(String.format("getNetworkType: %s", tm.getNetworkType()))
                .append(RN);
        sb.append(String.format("getPhoneType: %s", tm.getPhoneType())).append(
                RN);
        sb.append(String.format("getSimCountryIso: %s", tm.getSimCountryIso()))
                .append(RN);
        sb.append(String.format("getSimOperator: %s", tm.getSimOperator()))
                .append(RN);
        sb.append(
                String.format("getSimOperatorName: %s", tm.getSimOperatorName()))
                .append(RN);
        sb.append(String.format("getSimState: %s", tm.getSimState()))
                .append(RN);
        sb.append(String.format("getSubscriberId: %s", tm.getSubscriberId()))
                .append(RN);
        sb.append(
                String.format("getVoiceMailAlphaTag: %s",
                        tm.getVoiceMailAlphaTag())).append(RN);
        sb.append(
                String.format("getVoiceMailNumber: %s", tm.getVoiceMailNumber()))
                .append(RN);

        sb.append("").append(RN);
        sb.append("telephony cellLocation:").append(RN);
        sb.append("====================:").append(RN);
        CellLocation cl = tm.getCellLocation();
        if (cl instanceof GsmCellLocation) {
            GsmCellLocation gcl = (GsmCellLocation) cl;
            sb.append(String.format("getCellLocation: %s", gcl)).append(RN);
            sb.append(String.format("getCid: %s", gcl.getCid())).append(RN);
            sb.append(String.format("getLac: %s", gcl.getLac())).append(RN);
            sb.append(String.format("getPsc: %s", gcl.getPsc())).append(RN);
        } else if (cl instanceof CdmaCellLocation) {
            CdmaCellLocation ccl = (CdmaCellLocation) cl;
            sb.append(String.format("getCellLocation: %s", ccl)).append(RN);
            sb.append(
                    String.format("getBaseStationId: %s",
                            ccl.getBaseStationId())).append(RN);
            sb.append(
                    String.format("getBaseStationLatitude: %s",
                            ccl.getBaseStationLatitude())).append(RN);
            sb.append(
                    String.format("getBaseStationLongitude: %s",
                            ccl.getBaseStationLongitude())).append(RN);
            sb.append(String.format("getNetworkId: %s", ccl.getNetworkId()))
                    .append(RN);
            sb.append(String.format("getSystemId: %s", ccl.getSystemId()))
                    .append(RN);
        }

        return sb.toString();
    }

    static private boolean checkPermission(Context cx) {
        return PermissionUtils.checkPermission(cx,
                PermissionUtils.READ_PHONE_STATE);
    }
}
