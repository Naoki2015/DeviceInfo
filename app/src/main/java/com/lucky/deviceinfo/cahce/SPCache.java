package com.lucky.deviceinfo.cahce;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

public class SPCache {

    private SPCache() {

    }

    private static SPCache spCache;

    public static SPCache getInstance() {
        if (spCache == null) {
            spCache = new SPCache();
        }
        return spCache;
    }

    SharedPreferences.Editor sp;

    @SuppressLint("CommitPrefEdits")
    public void putSP(Context context, String key, String value) {
        sp = context.getSharedPreferences("info", Context.MODE_PRIVATE).edit();
        sp.putString(key, value).apply();
    }

    public String getSP(Context context, String key) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("info",
                Context.MODE_PRIVATE);
        return sharedPreferences.getString(key, "");
    }
}
