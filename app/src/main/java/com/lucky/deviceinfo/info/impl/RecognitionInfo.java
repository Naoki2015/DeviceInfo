package com.lucky.deviceinfo.info.impl;

import android.content.Context;

import com.lucky.deviceinfo.info.IInfo;
import com.lucky.deviceinfo.info.impl.recognite.Debug;
import com.lucky.deviceinfo.info.impl.recognite.Emulator;
import com.lucky.deviceinfo.info.impl.recognite.MultipleApp;
import com.lucky.deviceinfo.info.impl.recognite.Root;
import com.lucky.deviceinfo.info.impl.recognite.Xposed;

import java.util.HashMap;
import java.util.Map;

public class RecognitionInfo implements IInfo {

    /**
     * 识别危险检测
     */
    @Override
    public Map<String, String> getInfo(Context context) {
        Map<String, String> map = new HashMap<>();
        /*
         * 模拟器检测
         */
        Emulator.getInstance().distinguishVM(context);
        map.put("是否为模拟器", Emulator.getInstance().getVM());
        map.put("模拟器名字", Emulator.getInstance().getVmName());

        /**
         * Debug检测
         */
        map.put("是否处于调试", Debug.getInstance().isDebug(context));

        /**
         * 是否处于双开
         */
        map.put("是否被双开", MultipleApp.getInstance().isMultiApp(context));

        /**
         * 是否root
         */
        map.put("是否root", Root.getInstance().isroot(context));

        /**
         * 是否使用Xposed等作弊软件
         */
        map.put("是否Xposed", Xposed.getInstance().isXposed(context));

        return map;
    }
}
