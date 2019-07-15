package com.lucky.deviceinfo.info;

import android.content.Context;

import java.util.Map;

/**
 * 获取信息的接口
 */
public interface IInfo {

    Map<String, String> getInfo(Context context);
}
