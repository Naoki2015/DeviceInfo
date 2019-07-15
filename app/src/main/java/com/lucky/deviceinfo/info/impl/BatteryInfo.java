package com.lucky.deviceinfo.info.impl;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.lucky.deviceinfo.info.IInfo;

import java.util.HashMap;
import java.util.Map;

public class BatteryInfo implements IInfo {

    @Override
    public Map<String, String> getInfo(Context context) {
        Map<String, String> map = new HashMap<>();
        IntentFilter iFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent intent = context.registerReceiver(null, iFilter);
        int rawLevel = intent.getIntExtra("level", 0);      //获得当前电量
        int scale = intent.getIntExtra("scale", 0);         //获得总电量
        int status = intent.getIntExtra("status", 0);       //电池充电状态
        int health = intent.getIntExtra("health", 0);      //电池健康状况
        int batteryV = intent.getIntExtra("voltage", 0);    //电池电压(mv)
        int temperature = intent.getIntExtra("temperature", 0); //电池温度(数值)
        double t = temperature / 10.0;  //电池摄氏温度，默认获取的非摄氏温度值，需做一下运算转换
        String targetStr = "";
        int level = -1;
        if (rawLevel > 0 && scale > 0) {
            level = (rawLevel * 100) / scale;
            targetStr = level + "|" + scale + "|" + status;
        }
        map.put("当前电量", rawLevel + "");
        map.put("总电量", scale + "");
        map.put("电池状态", status + "");
        map.put("电池健康状态", health + "");
        map.put("电池电压", batteryV + "");
        map.put("电池温度", temperature + "");
        map.put("电池摄氏温度", t + "");
        //context.unregisterReceiver(null);
        return map;
    }
}
