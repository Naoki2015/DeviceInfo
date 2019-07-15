package com.lucky.deviceinfo.info.impl;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;

import com.lucky.deviceinfo.info.IInfo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SensorInfo implements IInfo {

    @Override
    public Map<String, String> getInfo(Context context) {
        Map<String, String> map = new HashMap<>();
        SensorManager sm = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        List<Sensor> allSensors = sm.getSensorList(Sensor.TYPE_ALL);
        for (int i = 0; i < allSensors.size(); i++) {
            map.put(allSensors.get(i).getName(), allSensors.get(i).getType() + "");
        }
        return map;
    }
}
