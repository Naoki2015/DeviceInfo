package com.lucky.dangerinfo.multiboxing;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.LocalServerSocket;
import android.text.TextUtils;
import android.util.Log;

import com.lucky.dangerinfo.utils.CommonUtil;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.BindException;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

/**
 * Project Name:checkMultiApk
 * Package Name:com.lahm.library
 * Created by lahm on 2018/5/14 下午4:11
 */
public class MultiBoxing {
    private String TAG = "test";
    private static volatile MultiBoxing singleInstance;

    private MultiBoxing() {
    }

    public static MultiBoxing getSingleInstance() {
        if (singleInstance == null) {
            synchronized (MultiBoxing.class) {
                if (singleInstance == null) {
                    singleInstance = new MultiBoxing();
                }
            }
        }
        return singleInstance;
    }

    /**
     * 维护一份市面多开应用的包名列表
     */
    private String[] virtualPkgs = {"com.bly.dkplat",//多开分身本身的包名
//            "dkplugin.pke.nnp",//多开分身克隆应用的包名会随机变换
            "com.by.chaos",//chaos引擎
            "com.lbe.parallel",//平行空间
            "com.excelliance.dualaid",//双开助手
            "com.lody.virtual",//VirtualXposed，VirtualApp
            "com.qihoo.magic"//360分身大师

            , "com.lbe.parallel.intl"   //平行空间
    };

    /**
     * 通过检测app私有目录，多开后的应用路径会包含多开软件的包名
     *
     * @param context
     * @param callback
     * @return
     */
    public boolean checkByPrivateFilePath(Context context) {
        String path = context.getFilesDir().getPath();
        for (String virtualPkg : virtualPkgs) {
            if (path.contains(virtualPkg)) {

                return true;
            }
        }
        return false;
    }

    /**
     * 检测原始的包名，多开应用会hook处理getPackageName方法
     * 顺着这个思路，如果在应用列表里出现了同样的包，那么认为该应用被多开了
     *
     * @param context
     * @param
     * @return
     */
    public boolean checkByOriginApkPackageName(Context context) {
        try {
            if (context == null)
                throw new IllegalArgumentException("you have to set context first");
            int count = 0;
            String packageName = context.getPackageName();
            PackageManager pm = context.getPackageManager();
            List<PackageInfo> pkgs = pm.getInstalledPackages(0);
            for (PackageInfo info : pkgs) {
                Log.d("hhh", info.packageName);
                for (int i = 0; i < virtualPkgs.length; i++) {
                    if(virtualPkgs[i].equals(info.packageName)){
                        count++;
                    }
                }
            }
            return count > 1;
        } catch (Exception ignore) {
        }
        return false;
    }
}
