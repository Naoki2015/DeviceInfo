package com.lucky.deviceinfo.info.impl;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.DhcpInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.lucky.deviceinfo.cahce.SPCache;
import com.lucky.deviceinfo.info.IInfo;
import com.lucky.deviceinfo.utils.LogUtil;

import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NetworkInfo implements IInfo {

    @Override
    public Map<String, String> getInfo(Context context) {
        Map<String, String> map = new HashMap<>();
        map.put("wifiMAC地址", macAddress(context));
        if (checkNetwork(context)) {
            String networkType = getNetworkType(context);
            map.put("网络类型", networkType);
            if (networkType.endsWith("WIFI")) {
                map.put("WIFIip", getWifiip(context));
                Map<String, String> ssidMap = currentWiFi(context);
                if (ssidMap != null) {
                    map.put("连接的WIFI名", ssidMap.get("ssid"));
                    map.put("连接的WIFI的物理mac地址", ssidMap.get("bssid"));
                    map.put("周围wifi列表", wifiList(context));
                }
            } else {
                map.put("蜂窝ip", getLocalIpV4Address());
            }
            map.put("外网ip", SPCache.getInstance().getSP(context, "ip"));
            map.put("Http代理", HttpProxy(context));
            map.put("DNS等信息", getWifiNetInfo(context).toString());
        } else {
            map.put("网络状态", "网络不可用");
        }
        return map;
    }

    public Map<String, String> getWifiNetInfo(Context context) {
        Map<String, String> wifiInfo = new HashMap<>();
        WifiManager wifi =
                (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (wifi != null) {
            DhcpInfo info = wifi.getDhcpInfo();
            wifiInfo.put("wifi-dns", intToIp(info.dns1) + ";" + intToIp(info.dns2));
            wifiInfo.put("wifi-gateway", intToIp(info.gateway));
            wifiInfo.put("wifi-ip", intToIp(info.ipAddress));
            wifiInfo.put("wifi-netmask", intToIp(info.netmask));
            wifiInfo.put("wifi-leaseTime", String.valueOf(info.leaseDuration));
            wifiInfo.put("wifi-dhcpServer", intToIp(info.serverAddress));
        }
        return wifiInfo;
    }

    //http代理信息
    private String HttpProxy(Context context) {
        try {
            String proxyHost;
            int proxyPort;
            proxyHost = System.getProperty("http.proxyHost");
            String port = System.getProperty("http.proxyPort");
            proxyPort = Integer.parseInt(port != null ? port : "-1");
            //return proxyHost != null && proxyPort != -1 ? IS_PROXY : NO_PROXY;
            return proxyHost != null && proxyPort != -1 ? (proxyHost + ":" + proxyPort) : "";
        } catch (Throwable e) {
            LogUtil.w("HttpProxy", e.getMessage());
        }
        return "";
    }

    /**
     * 检查当前网络
     */
    private boolean checkNetwork(Context context) {
        ConnectivityManager connect =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        android.net.NetworkInfo net = connect.getActiveNetworkInfo();
        if (net != null && net.isConnected()) {
            return true;
        }
        return false;
    }

    /**
     * 检查当前网络类型
     * 参考:https://www.cnblogs.com/meteoric_cry/p/4627075.html
     */
    private String getNetworkType(Context context) {
        String strNetworkType = "";
        try {
            android.net.NetworkInfo networkInfo =
                    ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isConnected()) {
                if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                    strNetworkType = "WIFI";
                } else if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                    String _strSubTypeName = networkInfo.getSubtypeName();

                    Log.e("cocos2d-x", "Network getSubtypeName : " + _strSubTypeName);

                    // TD-SCDMA   networkType is 17
                    int networkType = networkInfo.getSubtype();
                    switch (networkType) {
                        case TelephonyManager.NETWORK_TYPE_GPRS:
                        case TelephonyManager.NETWORK_TYPE_EDGE:
                        case TelephonyManager.NETWORK_TYPE_CDMA:
                        case TelephonyManager.NETWORK_TYPE_1xRTT:
                        case TelephonyManager.NETWORK_TYPE_IDEN: //api<8 : replace by 11
                            strNetworkType = "2G";
                            break;
                        case TelephonyManager.NETWORK_TYPE_UMTS:
                        case TelephonyManager.NETWORK_TYPE_EVDO_0:
                        case TelephonyManager.NETWORK_TYPE_EVDO_A:
                        case TelephonyManager.NETWORK_TYPE_HSDPA:
                        case TelephonyManager.NETWORK_TYPE_HSUPA:
                        case TelephonyManager.NETWORK_TYPE_HSPA:
                        case TelephonyManager.NETWORK_TYPE_EVDO_B: //api<9 : replace by 14
                        case TelephonyManager.NETWORK_TYPE_EHRPD:  //api<11 : replace by 12
                        case TelephonyManager.NETWORK_TYPE_HSPAP:  //api<13 : replace by 15
                            strNetworkType = "3G";
                            break;
                        case TelephonyManager.NETWORK_TYPE_LTE:    //api<11 : replace by 13
                            strNetworkType = "4G";
                            break;
                        default:
                            // http://baike.baidu.com/item/TD-SCDMA 中国移动 联通 电信 三种3G制式
                            if (_strSubTypeName.equalsIgnoreCase("TD-SCDMA") || _strSubTypeName.equalsIgnoreCase("WCDMA") || _strSubTypeName.equalsIgnoreCase("CDMA2000")) {
                                strNetworkType = "3G";
                            } else {
                                strNetworkType = _strSubTypeName;
                            }

                            break;
                    }
                }
            }
        } catch (Throwable e) {
            LogUtil.w("getNetworkType", e.getMessage());
        }
        return strNetworkType;
    }

    //wifi的ip
    private String getWifiip(Context context) {
        try {
            //获取wifi服务
            WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            //判断wifi是否开启
            if (!wifiManager.isWifiEnabled()) {
                wifiManager.setWifiEnabled(true);
            }
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            int ipAddress = wifiInfo.getIpAddress();
            String ip = intToIp(ipAddress);
            return ip;
        } catch (Throwable ex) {
            LogUtil.w("getWifiip", ex.getMessage());
        }
        return "";
    }

    private String intToIp(int i) {
        return (i & 0xFF) + "." + ((i >> 8) & 0xFF) + "." + ((i >> 16) & 0xFF) + "." + (i >> 24 & 0xFF);
    }

    //Current WiFi
    private Map<String, String> currentWiFi(Context context) {
        try {
            if (context != null) {
                WifiManager wifiManager =
                        (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                if (wifiManager != null) {
                    WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                    Map<String, String> map = new HashMap<>();
                    String BSSID = wifiInfo.getBSSID();
                    map.put("bssid", BSSID);
                    //.replace("\"", "");
                    String SSID = wifiInfo.getSSID().replace("\"", "");
                    map.put("ssid", SSID);
                    //return ("[" + SSID + "," + BSSID + "]").replace("=", "").replace("&", "");
                    return map;
                }
            }
        } catch (Throwable e) {
            LogUtil.w("currentWiFi", e.getMessage());
        }
        return null;
    }

    //写法一,
    //获取蜂窝IP
    private static String getLocalIpAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress() && !inetAddress.isLinkLocalAddress()) {
                        return inetAddress.getHostAddress().toString();
                    }
                }
            }
        } catch (SocketException ex) {
            LogUtil.w("WifiPreferenceIpAddress", ex.toString());
        }
        return "";
    }

    //写法二:

    /**
     * 获取内网ip地址
     *
     * @return
     */
    private String getHostIP() {
        String hostIp = "";
        try {
            Enumeration nis = NetworkInterface.getNetworkInterfaces();
            InetAddress ia = null;
            while (nis.hasMoreElements()) {
                NetworkInterface ni = (NetworkInterface) nis.nextElement();
                Enumeration<InetAddress> ias = ni.getInetAddresses();
                while (ias.hasMoreElements()) {
                    ia = ias.nextElement();
                    if (ia instanceof Inet6Address) {
                        continue;// skip ipv6
                    }
                    String ip = ia.getHostAddress();
                    if (!"127.0.0.1".equals(ip)) {
                        hostIp = ia.getHostAddress();
                        break;
                    }
                }
            }
        } catch (SocketException e) {
            Log.i("yao", "SocketException");
            e.printStackTrace();
        }
        return hostIp;
    }

    //写法三（推荐）：
    private String getLocalIpV4Address() {
        try {
            String ipv4;
            ArrayList<NetworkInterface> nilist =
                    Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface ni : nilist) {
                ArrayList<InetAddress> ialist = Collections.list(ni.getInetAddresses());
                for (InetAddress address : ialist) {
                    if (!address.isLoopbackAddress() && !address.isLinkLocalAddress()) {
                        ipv4 = address.getHostAddress();
                        return ipv4;
                    }
                }
            }
        } catch (SocketException ex) {
            LogUtil.w("getLocalIpV4Address", ex.getMessage());
        }
        return "";
    }

    private String wifiList(Context context) {
        try {
            if (context != null) {
                WifiManager manager =
                        (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                manager.startScan();
                StringBuffer str = new StringBuffer("");
                List<ScanResult> result = manager.getScanResults();
                if (result != null && result.size() > 0) {
                    for (ScanResult scanResult : result) {
                        str.append(scanResult.SSID);
                        str.append(",");
                        str.append(scanResult.BSSID);
                        str.append(",");
                        str.append(scanResult.capabilities.replace("[", "").replace("]", ""));
                        str.append(",");
                    }
                    return (str.substring(0, str.length() - 1) + "]").replace("=", "").replace(
                            "&", "");
                }
            }
        } catch (Throwable e) {
            LogUtil.w("wifiList", e.getMessage());
        }
        return "";
    }


    @SuppressLint("HardwareIds")
    private String macAddress(Context context) {
        if (Build.VERSION.SDK_INT == 23) {
            String str = null;
            Process pp = null;
            InputStreamReader ir = null;
            LineNumberReader input = null;
            try {
                pp = Runtime.getRuntime().exec("cat /sys/class/net/wlan0/address");
                ir = new InputStreamReader(pp.getInputStream(), StandardCharsets.UTF_8);
                input = new LineNumberReader(ir);
                str = input.readLine();
                if (str != null && str.contains(":") && str.length() == 17) {
                    input.close();
                    ir.close();
                    pp.destroy();
                    return str.replace("=", "").replace("&", "").toLowerCase();
                }
            } catch (Throwable ex) {
                LogUtil.w("macAddress1", ex.getMessage());
            }
        } else if (Build.VERSION.SDK_INT < 23) {
            try {
                if (context != null) {
                    String result;
                    WifiManager wifiManager =
                            (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                    WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                    result = wifiInfo.getMacAddress();
                    return result.replace("=", "").replace("&", "").toLowerCase();
                }
            } catch (Throwable e) {
                LogUtil.w("macAddress2", e.getMessage());
            }
        } else {
            try {
                List<NetworkInterface> all =
                        Collections.list(NetworkInterface.getNetworkInterfaces());
                for (NetworkInterface nif : all) {
                    if (!nif.getName().equalsIgnoreCase("wlan0")) continue;
                    byte[] macBytes = nif.getHardwareAddress();
                    if (macBytes == null) {
                        return "";
                    }
                    StringBuilder res1 = new StringBuilder();
                    for (byte b : macBytes) {
                        res1.append(String.format("%02X:", b));
                    }
                    if (res1.length() > 0) {
                        res1.deleteCharAt(res1.length() - 1);
                    }
                    return res1.toString().toLowerCase();
                }
            } catch (Throwable ex) {
                LogUtil.w("macAddress3", ex.getMessage());
            }
        }
        return "";
    }
}
