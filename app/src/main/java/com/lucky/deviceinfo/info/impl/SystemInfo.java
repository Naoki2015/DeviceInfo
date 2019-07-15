package com.lucky.deviceinfo.info.impl;

import android.content.Context;
import android.os.Build;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import com.lucky.deviceinfo.MainActivity;
import com.lucky.deviceinfo.info.IInfo;
import com.lucky.deviceinfo.utils.*;

/**
 * 对系统信息的采集
 */
public class SystemInfo implements IInfo {

    @Override
    public Map<String, String> getInfo(Context context) {
        Map<String, String> map = new HashMap<>();
        map.put("设备品牌", Build.BRAND);
        map.put("设备型号", Build.MODEL);
        map.put("设备版本号", Build.ID);
        map.put("产品的名称\t", Build.PRODUCT);
        map.put("制造商", Build.MANUFACTURER);
        map.put("设备驱动名称", Build.DEVICE);
        map.put("硬件名称", Build.HARDWARE);
        map.put("显示屏参数", Build.DISPLAY);
        map.put("串口序列号", Build.SERIAL);
        map.put("设备版本类型", Build.TYPE);
        map.put("设备标签", Build.TAGS);
        map.put("设备主机地址", Build.HOST);
        map.put("设备主板", Build.BOARD);
        map.put("支持的cpu架构", getSupportCpuAbi());
        map.put("当前开发代号", Build.VERSION.CODENAME);
        map.put("源码控制版本号", Build.VERSION.INCREMENTAL);
        map.put("主板引导程序", Build.BOOTLOADER);
        map.put("编译时间", DateUtil.stampToTime(Build.TIME));
        map.put("系统版本值", Build.VERSION.SDK_INT + "");
        map.put("系统版本", Build.VERSION.RELEASE);
        map.put("当前系统语言", Locale.getDefault().getLanguage());
        return map;
    }

    private String getSupportCpuAbi() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            String cpuabi = "";
            String[] supportedAbis = Build.SUPPORTED_ABIS;
            for (String supportedAbi : supportedAbis) {
                cpuabi = cpuabi + "," + supportedAbi;
            }
            return cpuabi;
        } else {
            return Build.CPU_ABI + "," + Build.CPU_ABI2;
        }
    }

}
