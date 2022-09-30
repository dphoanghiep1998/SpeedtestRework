package com.example.speedtest_rework.common;

import android.annotation.TargetApi;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;

import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteOrder;
import java.util.List;

public class NetworkUtils {
    public static boolean isConnected(@NonNull Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null) {
            NetworkInfo networkInfo = cm.getActiveNetworkInfo();
            return networkInfo != null && networkInfo.isConnected();
        }
        return false;
    }

    public static boolean isWifiConnected(@NonNull Context context) {
        return isConnected(context, ConnectivityManager.TYPE_WIFI);
    }

    public static boolean isWifiConnected(@NonNull Context context, String wifi_name) {
        if (wifi_name.length() > 0 && isWifiConnected(context) && getNameWifi(context) == wifi_name) {
            Log.d("TAG", "isWifiConnected: " + wifi_name);
            return true;
        }
        return false;
    }

    public static boolean isWifiEnabled(@NonNull Context context) {
        WifiManager cm = (WifiManager) context.getApplicationContext().getSystemService(context.WIFI_SERVICE);
        return cm == null && cm.isWifiEnabled();
    }

    public static boolean isMobileConnected(@NonNull Context context) {
        return isConnected(context, ConnectivityManager.TYPE_MOBILE);
    }

    public static NetworkInfo getInforMobileConnected(@NonNull Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(context.CONNECTIVITY_SERVICE);
        if (cm != null) {
            NetworkInfo networkInfo = cm.getActiveNetworkInfo();
            return networkInfo;
        }
        return null;


    }

    private static boolean isConnected(@NonNull Context context, int type) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();

        if (netInfo != null) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                NetworkInfo networkInfo = cm.getNetworkInfo(type);
                return networkInfo != null && networkInfo.isConnected();
            } else {
                return isConnected(cm, type);
            }
        }
        return false;

    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private static boolean isConnected(@NonNull ConnectivityManager cm, int type) {
        Network[] networks = cm.getAllNetworks();
        NetworkInfo networkInfo;
        for (Network network : networks) {
            networkInfo = cm.getNetworkInfo(network);
            if (networkInfo != null && networkInfo.getType() == type && networkInfo.isConnected()) {
                return true;
            }
        }
        return false;
    }

    public static int getDownloadSpeed(@NonNull Context context) {
        int downloadSpeed = 0;
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(context.CONNECTIVITY_SERVICE);
        if (isWifiConnected(context)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                NetworkCapabilities nc = cm.getNetworkCapabilities(cm.getActiveNetwork());
                downloadSpeed = nc.getLinkDownstreamBandwidthKbps();
                return downloadSpeed;
            }
        }
        return downloadSpeed;
    }

    @TargetApi(Build.VERSION_CODES.M)
    public static int getUploadSpeed(@NonNull Context context) {
        int uploadSpeed = 0;
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(context.CONNECTIVITY_SERVICE);

        if (isWifiConnected(context)) {
            NetworkCapabilities nc = cm.getNetworkCapabilities(cm.getActiveNetwork());
            uploadSpeed = nc.getLinkUpstreamBandwidthKbps();
            return uploadSpeed;
        }
        return uploadSpeed;
    }

    public static WifiInfo getWifiInfo(@NonNull Context context) {
        WifiManager cm = (WifiManager) context.getApplicationContext().getSystemService(context.WIFI_SERVICE);
        return cm.getConnectionInfo();
    }
//    public static String getNameWifi(@NonNull Context context) {
//        ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
//        NetworkInfo networkInfo = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
//        if (networkInfo.isConnected()) {
//            WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
//            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
//            return wifiInfo.toString();
//        }
//        return null;
//    }

    public static String getNameWifi(Context context) {
        WifiManager manager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (manager != null && manager.isWifiEnabled()) {
            WifiInfo wifiInfo = manager.getConnectionInfo();
            if (wifiInfo != null) {
                NetworkInfo.DetailedState state = WifiInfo.getDetailedStateOf(wifiInfo.getSupplicantState());
                if (state == NetworkInfo.DetailedState.CONNECTED || state == NetworkInfo.DetailedState.OBTAINING_IPADDR) {
                    return wifiInfo.getSSID().substring(1, wifiInfo.getSSID().length() - 1);
                }
            }
        }
        return null;
    }

    public static String getNameWifi(WifiManager manager) {
        if (manager != null && manager.isWifiEnabled()) {
            WifiInfo wifiInfo = manager.getConnectionInfo();
            if (wifiInfo != null) {
                NetworkInfo.DetailedState state = WifiInfo.getDetailedStateOf(wifiInfo.getSupplicantState());
                if (state == NetworkInfo.DetailedState.CONNECTED || state == NetworkInfo.DetailedState.OBTAINING_IPADDR) {
                    return wifiInfo.getSSID().substring(1, wifiInfo.getSSID().length() - 1);
                }
            }
        }
        return null;
    }

    public static List<ScanResult> getListWifi(Context context, WifiManager manager) {

        if (manager != null && manager.isWifiEnabled()) {
            List<ScanResult> wifiList = manager.getScanResults();
            return wifiList;
        }
        return null;
    }

    public static String wifiIpAddress(Context context) {
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        int ipAddress = wifiManager.getConnectionInfo().getIpAddress();

        // Convert little-endian to big-endianif needed
        if (ByteOrder.nativeOrder().equals(ByteOrder.LITTLE_ENDIAN)) {
            ipAddress = Integer.reverseBytes(ipAddress);
        }

        byte[] ipByteArray = BigInteger.valueOf(ipAddress).toByteArray();

        String ipAddressString;
        try {
            ipAddressString = InetAddress.getByAddress(ipByteArray).getHostAddress();
        } catch (UnknownHostException ex) {
            Log.e("WIFIIP", "Unable to get host address.");
            ipAddressString = null;
        }

        return ipAddressString;
    }

    public static int[] getRangeWifi(int channel, int type) {
        int r_range = 0;
        int l_range = 0;
        switch (type) {
            case 0:
                if (channel <= 2) {
                    r_range = 0;
                    l_range = 4;
                } else {
                    r_range = channel - 2;
                    l_range = channel + 2;
                }
                break;

            case 1:
                if (channel - 4 > 0) {
                    r_range = channel - 2 > 0 ? channel - 2 : 0;
                    l_range = r_range + 8;
                } else if (channel + 4 > 14) {
                    l_range = channel + 2 < 14 ? channel + 2 : 14;
                    r_range = l_range - 8;
                }
                break;

        }

        int result[] = {r_range, l_range};
        return result;
    }

    public static int convertFreqtoChannel(int freq) {
        if (freq == 2484)
            return 14;

        if (freq < 2484)
            return (freq - 2407) / 5;

        return freq / 5 - 1000;
    }

    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        // For 29 api or above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.getActiveNetwork());
            if (capabilities == null) {
                return false;
            }
            if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                return true;
            } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
                return true;
            } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                return true;
            } else {
                return false;
            }
        } else {
            if (connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnectedOrConnecting()) {
                return true;
            } else {
                return false;
            }
        }

    }
    public static boolean canGetLocation(Context context) {
        return isLocationEnabled(context.getApplicationContext()); // application context
    }

    public static boolean isLocationEnabled(Context context) {
        int locationMode = 0;
        String locationProviders;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            try {
                locationMode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE);
            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
            }
            return locationMode != Settings.Secure.LOCATION_MODE_OFF;
        } else {
            locationProviders = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.LOCATION_MODE);
            return !TextUtils.isEmpty(locationProviders);
        }
    }

}
