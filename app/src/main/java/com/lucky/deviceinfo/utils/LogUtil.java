package com.lucky.deviceinfo.utils;

import android.util.Log;

public class LogUtil {

    private static final String TAG = "FFF";

    public static void w(String name, String info) {
        Log.w(TAG, "获取信息:" + name + "," + "异常信息" + info);
    }
}
