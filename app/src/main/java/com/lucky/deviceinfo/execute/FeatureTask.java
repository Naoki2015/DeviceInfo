package com.lucky.deviceinfo.execute;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.telephony.TelephonyManager;
import android.telephony.cdma.CdmaCellLocation;
import android.telephony.gsm.GsmCellLocation;

import com.lucky.deviceinfo.cahce.SPCache;
import com.lucky.deviceinfo.utils.LogUtil;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class FeatureTask extends AsyncTask<Void, Void, Boolean> {

    @SuppressLint("StaticFieldLeak")
    private Context context;

    public FeatureTask(Context context) {
        this.context = context;
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        if (context == null)
            return false;
        String basestation = basestation(context);
        String coordinate = Coordinate(context);
        String outIP=getOutip();
        SPCache.getInstance().putSP(context,"base", basestation);
        SPCache.getInstance().putSP(context,"coordinate", coordinate);
        SPCache.getInstance().putSP(context, "ip", outIP);
        return true;
    }

    //获取当前基站信息
    static String basestation(Context context) {
        try {
            if (context != null) {
                TelephonyManager manager = (TelephonyManager)
                        context.getSystemService(Context.TELEPHONY_SERVICE);
                String operator = manager.getNetworkOperator();
                int type = manager.getNetworkType();
                if (type == TelephonyManager.NETWORK_TYPE_CDMA
                        || type == TelephonyManager.NETWORK_TYPE_1xRTT
                        || type == TelephonyManager.NETWORK_TYPE_EVDO_0
                        || type == TelephonyManager.NETWORK_TYPE_EVDO_A) {
                    if (operator.length() >= 4) {
                        int mcc = Integer.parseInt(operator.substring(0, 3));
                        @SuppressLint("MissingPermission") CdmaCellLocation location = (CdmaCellLocation) manager.getCellLocation();
                        if (location != null) {
                            return mcc + "," + location.getSystemId() + ","
                                    + location.getBaseStationId() + "," + location.getNetworkId();
                        } else {
                            return "";
                        }
                    }
                } else {
                    if (operator.length() >= 4) {
                        int mcc = Integer.parseInt(operator.substring(0, 3));
                        int mnc = Integer.parseInt(operator.substring(3));
                        @SuppressLint("MissingPermission") GsmCellLocation location = (GsmCellLocation) manager.getCellLocation();
                        if (location != null) {
                            return mcc + "," + mnc + "," +
                                    location.getCid() + "," + location.getLac();
                        } else {
                            return "";
                        }
                    }
                }
            } else {
                return "";
            }
        } catch (Exception e) {
            LogUtil.w("基站信息",e.getMessage());
        }
        return "";
    }

    static String Coordinate(Context context) {
        try {
            double latitude1 = 0;
            double longitude1 = 0;
            LocationManager locationManager = (LocationManager) context.getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
            @SuppressLint("MissingPermission") final Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            if (location != null) {
                latitude1 = location.getLatitude();
                longitude1 = location.getLongitude();
            }
            return "[" + latitude1 + "," + longitude1 + "]";
        } catch (Throwable e) {
            LogUtil.w("位置信息",e.getMessage());
            return "";
        }
    }

    static String getOutip() {
        {
            URL infoUrl = null;
            InputStream inStream = null;
            String ipLine = "";
            HttpURLConnection httpConnection = null;
            try {
//            infoUrl = new URL("http://ip168.com/");
                infoUrl = new URL("http://pv.sohu.com/cityjson?ie=utf-8");
                URLConnection connection = infoUrl.openConnection();
                httpConnection = (HttpURLConnection) connection;
                int responseCode = httpConnection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    inStream = httpConnection.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(inStream,
                            "utf-8"));
                    StringBuilder strber = new StringBuilder();
                    String line = null;
                    while ((line = reader.readLine()) != null) {
                        strber.append(line + "\n");
                    }
                    Pattern pattern = Pattern.compile("((?:(?:25[0-5]|2[0-4]\\d|((1\\d{2})|" +
                            "([1-9]?\\d)))\\.){3}(?:25[0-5]|2[0-4]\\d|((1\\d{2})|([1-9]?\\d))))");
                    Matcher matcher = pattern.matcher(strber.toString());
                    if (matcher.find()) {
                        ipLine = matcher.group();
                        return ipLine;
                    }
                }
            } catch (Throwable e) {
                LogUtil.w("外网ip",e.getMessage());
            }
            return "";
        }
    }
}
