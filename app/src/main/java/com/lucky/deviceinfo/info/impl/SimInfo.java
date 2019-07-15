package com.lucky.deviceinfo.info.impl;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import com.lucky.deviceinfo.cahce.SPCache;
import com.lucky.deviceinfo.info.IInfo;
import com.lucky.deviceinfo.utils.LogUtil;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class SimInfo implements IInfo {
    @Override
    public Map<String, String> getInfo(Context context) {
        Map<String, String> map = new HashMap<>();
        if (getSIMState(context)) {
            map.put("IMSI", getImsi(context));
            map.put("SIM序列号", getSimSerialNumber(context));
            map.put("基站信息", getBaseStation(context));
            map.put("电话号码", getNativePhoneNumber(context));
            map.put("运营商", getProvidersName(context));
        }
        map.put("IMEI", getImei(context));
        map.put("语音号码", getVoiceMailNumber(context));

        //map.put("手机信息", getPhoneInfo(context));
        return map;
    }

    private boolean getSIMState(Context context) {
        try {
            TelephonyManager telephonyManager =
                    (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            int simState = telephonyManager.getSimState();
            return simState == TelephonyManager.SIM_STATE_ABSENT;
        } catch (Throwable e) {
            LogUtil.w("手机卡状态", e.getMessage());
        }
        return false;
    }

    @SuppressLint("MissingPermission")
    private String getImsi(Context context) {
        String ret = null;
        try {
            TelephonyManager telephonyManager =
                    (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            ret = telephonyManager.getSubscriberId();
        } catch (Throwable e) {
            // LoggerUtils.e("---------" + e.getMessage());
        }
        if (!TextUtils.isEmpty(ret)) {
            return ret;
        } else {
            return "";
        }
    }

    @SuppressLint("MissingPermission")
    private String getImei(Context context) {
        //String imei;
        if (Build.VERSION.SDK_INT < 21) {
            //如果获取系统的IMEI/MEID，14位代表meid 15位是imei
            return getImei1(context);
            // 21版本是5.0，判断是否是5.0以上的系统  5.0系统直接获取IMEI1,IMEI2,MEID
        } else if (Build.VERSION.SDK_INT < 24) {
            Map<String, String> map = getImei2(context);
            if (map != null) {
                return map.toString();
            }
        } else if (Build.VERSION.SDK_INT < 28) {
            String imei1 = getImei3(0, context);
            String imei2 = getImei3(1, context);
            return imei1 + "," + imei2;
        } else {
            return getImei4(context);
        }
        return "";
    }

    /**
     * 系统4.0的时候
     * 获取手机IMEI 或者Meid
     *
     * @return 手机IMEI
     */
    @SuppressLint("MissingPermission")
    private String getImei1(Context ctx) {
        TelephonyManager tm = (TelephonyManager) ctx.getSystemService(Context.TELEPHONY_SERVICE);
        if (tm != null) {
            return tm.getDeviceId();
        }
        return "";
    }

    /**
     * 针对5.0，6.0处理
     * 参考https://blog.csdn.net/yangbin0513/article/details/68490291
     *
     * @param ctx
     * @return
     */
    @TargetApi(Build.VERSION_CODES.M)
    @SuppressLint("MissingPermission")
    private Map getImei2(Context ctx) {
        Map<String, String> map = new HashMap<String, String>();
        TelephonyManager mTelephonyManager = (TelephonyManager) ctx.getSystemService(Context.TELEPHONY_SERVICE);
        Class<?> clazz = null;
        Method method = null;//(int slotId)
        try {
            clazz = Class.forName("android.os.SystemProperties");
            method = clazz.getMethod("get", String.class, String.class);
            String gsm = (String) method.invoke(null, "ril.gsm.imei", "");
            String meid = (String) method.invoke(null, "ril.cdma.meid", "");
            map.put("meid", meid);
            if (!TextUtils.isEmpty(gsm)) {
                //the value of gsm like:xxxxxx,xxxxxx
                String imeiArray[] = gsm.split(",");
                if (imeiArray != null && imeiArray.length > 0) {
                    map.put("imei1", imeiArray[0]);

                    if (imeiArray.length > 1) {
                        map.put("imei2", imeiArray[1]);
                    } else {
                        map.put("imei2", mTelephonyManager.getDeviceId(1));
                    }
                } else {
                    map.put("imei1", mTelephonyManager.getDeviceId(0));
                    map.put("imei2", mTelephonyManager.getDeviceId(1));
                }
            } else {
                map.put("imei1", mTelephonyManager.getDeviceId(0));
                map.put("imei2", mTelephonyManager.getDeviceId(1));
            }
            return map;
        } catch (Throwable e) {
            LogUtil.w("IMEI2", e.getMessage());
        }
        return null;
    }

    /**
     * 7.0和7.1使用该方法获取
     *
     * @param phoneid
     * @param context
     * @return
     */
    private  String getImei3(int phoneid, Context context) {
        TelephonyManager telephonyManager =
                (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        Class<TelephonyManager> telephonyManagerClass = TelephonyManager.class;
        String deviceId = null;
        try {
            Method method = telephonyManagerClass.getMethod("getImei", new Class[]{int.class});
            method.setAccessible(true);
            Object object = method.invoke(telephonyManager, phoneid);
            deviceId = (String) object;
            return deviceId;
        } catch (Throwable e) {
            LogUtil.w("IMEI3", e.getMessage());
        }
        return "";
    }

    /**
     * 8.0及以上使用该方法获取
     *
     * @param context
     * @return
     */
    @SuppressLint("MissingPermission")
    private  String getImei4(Context context) {
        String imei="";
        try {
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            if (tm != null) {
                String imei1 = tm.getImei(0);
                if(imei1!=null) {
                    imei = "imei1:" + imei1;
                }
                String imei2 = tm.getImei(1);
                if (imei2 != null) {
                    imei = imei + ",imei2:" + imei2;
                }
                String meid = tm.getMeid(0);
                if (meid != null) {
                    imei = imei + ",meid:" + meid;
                }
                return imei;
            }
        } catch (Throwable e) {
            LogUtil.w("IMEI4", e.getMessage());
        }
        return "";
    }


    //SIM卡序列号
    @SuppressLint("MissingPermission")
    private  String getSimSerialNumber(Context context) {
        String ret = null;
        try {
            TelephonyManager telephonyManager =
                    (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            ret = telephonyManager.getSimSerialNumber();
        } catch (Exception e) {
            // LoggerUtils.e("---------" + e.getMessage());
        }
        if (!TextUtils.isEmpty(ret)) {
            return ret;
        } else {
            return "";
        }
    }

    //语音号码
    @SuppressLint("MissingPermission")
    private  String getVoiceMailNumber(Context context) {
        String ret = null;
        try {
            TelephonyManager telephonyManager =
                    (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            ret = telephonyManager.getVoiceMailNumber();
        } catch (Throwable e) {
            // LoggerUtils.e("---------" + e.getMessage());
        }
        if (!TextUtils.isEmpty(ret)) {
            return ret;
        } else {
            return "";
        }
    }

    //获取基站信息
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private  String getBaseStation(Context context) {
        return SPCache.getInstance().getSP(context, "base");
    }

    //获取电话号码
    @SuppressLint("MissingPermission")
    private String getNativePhoneNumber(Context context) {
        String nativePhoneNumber = "";
        try {
            TelephonyManager telephonyManager =
                    (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            nativePhoneNumber = telephonyManager.getLine1Number();
        } catch (Throwable e) {
            return "";
        }
        return nativePhoneNumber;
    }

    //获取手机服务商信息
    private String getProvidersName(Context context) {
        String providersName = "";
        TelephonyManager telephonyManager =
                (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String NetworkOperator = telephonyManager.getNetworkOperator();
        //IMSI号前面3位460是国家，紧接着后面2位00 02是中国移动，01是中国联通，03是中国电信。
//        Flog.d(TAG,”NetworkOperator=” + NetworkOperator);
        if (NetworkOperator.equals("46000") || NetworkOperator.equals("46002")) {
            providersName = "中国移动";//中国移动
        } else if (NetworkOperator.equals("46001")) {
            providersName = "中国联通";//中国联通
        } else if (NetworkOperator.equals("46003")) {
            providersName = "中国电信";//中国电信
        }
        return providersName;
    }

//    private String getPhoneInfo(Context context) {
//        TelephonyManager tm =
//                (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
//        StringBuffer sb = new StringBuffer();
//
//        sb.append("\nLine1Number = " + tm.getLine1Number());
//        sb.append("\nNetworkOperator = " + tm.getNetworkOperator());//移动运营商编号
//        sb.append("\nNetworkOperatorName = " + tm.getNetworkOperatorName());//移动运营商名称
//        sb.append("\nSimCountryIso = " + tm.getSimCountryIso());
//        sb.append("\nSimOperator = " + tm.getSimOperator());
//        sb.append("\nSimOperatorName = " + tm.getSimOperatorName());
//        sb.append("\nSimSerialNumber = " + tm.getSimSerialNumber());
//        sb.append("\nSubscriberId(IMSI) = " + tm.getSubscriberId());
//        return sb.toString();
//    }
}
