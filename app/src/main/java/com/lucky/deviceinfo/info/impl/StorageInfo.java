package com.lucky.deviceinfo.info.impl;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Environment;
import android.os.StatFs;
import android.text.format.Formatter;

import com.lucky.deviceinfo.info.IInfo;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class StorageInfo implements IInfo {
    @Override
    public Map<String, String> getInfo(Context context) {
        Map<String, String> map = new HashMap<>();
        map.put("总存储大小", getSDTotalSize(context));
        map.put("可用存储大小", getSDAvailableSize(context));
        map.put("总内存大小", getSysteTotalMemorySize(context));
        map.put("可用内存大小", getSystemAvaialbeMemorySize(context));
        return map;
    }

    /**
     * 获得SD卡总大小
     *
     * @return
     */
    private String getSDTotalSize(Context context) {
        File path = Environment.getExternalStorageDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long totalBlocks = stat.getBlockCount();
        return Formatter.formatFileSize(context, blockSize * totalBlocks);
    }

    /**
     * 获得sd卡剩余容量，即可用大小
     *
     * @return
     */
    private String getSDAvailableSize(Context context) {
        File path = Environment.getExternalStorageDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getAvailableBlocks();
        return Formatter.formatFileSize(context, blockSize * availableBlocks);
    }

    /**
     * 获取系统内存大小
     *
     * @return
     */
    private String getSysteTotalMemorySize(Context context) {
        //获得ActivityManager服务的对象
        ActivityManager mActivityManager =
                (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        //获得MemoryInfo对象
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        //获得系统可用内存，保存在MemoryInfo对象上
        mActivityManager.getMemoryInfo(memoryInfo);
        long memSize = memoryInfo.totalMem;
        //字符类型转换
        //String availMemStr = formateFileSize(memSize);
        return memSize + "";
    }

    /**
     * 获取系统可用的内存大小
     *
     * @return
     */
    private String getSystemAvaialbeMemorySize(Context context) {
        //获得ActivityManager服务的对象
        ActivityManager mActivityManager =
                (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        //获得MemoryInfo对象
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        //获得系统可用内存，保存在MemoryInfo对象上
        mActivityManager.getMemoryInfo(memoryInfo);
        long memSize = memoryInfo.availMem;

        //字符类型转换
        //String availMemStr = formateFileSize(memSize);

        return memSize + "";
    }
}
