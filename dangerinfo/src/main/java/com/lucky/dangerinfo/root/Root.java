package com.lucky.dangerinfo.root;

import android.content.Context;

import com.lucky.dangerinfo.utils.CommandUtil;
import com.lucky.dangerinfo.utils.FileUtil;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * 检测Root
 * 1、主流的root工具包名
 * //市场上常用的的权限管理app的包名   com.qihoo.permmgr  com.noshufou.android.su  eu.chainfire.supersu   com
 * .kingroot.kinguser  com.kingouser.com  com.koushikdutta.superuser
 * com.dianxinos.superuser  com.lbe.security.shuame com.geohot.towelroot 。。。。。
 * 2、检测某些文件夹是否存在su文件
 * 3、
 */

public class Root {

    private static Root root;
    private static String LOG_TAG = Root.class.getName();
    private String[] rootPackage = {"com.qihoo.permmgr", "com.noshufou.android.su", "eu.chainfire"
            + ".supersu", "com.kingroot.kinguser", "com.kingouser.com", "com.koushikdutta" +
            ".superuser", "com.dianxinos.superuser", "com.lbe.security.shuame", "com.geohot" +
            ".towelroot", "com.genymotion.superuser", "com.speedsoftware.superuser", "com" +
            ".thirdparty.superuser", "com.topjohnwu.magisk"};

    public static Root getInstance() {
        if (root == null) {
            root = new Root();
        }
        return root;
    }

    public String isRoot(Context context) {
        String ss = isDeviceRooted(context);
        if (ss.contains("1")) {
            return "1";
        }
        return "0";
    }

    public String isDeviceRooted(Context context) {
        return checkRootPackage(context) + "-" + checkSecure() + "-" + checksuFile() + "-" + checkRootWhichSU();
    }

    public String checkRootPackage(Context context) {
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < rootPackage.length; i++) {
            String value = FileUtil.checkPackageName(rootPackage[i], context);
            buffer.append(value);
        }
        return buffer.toString();
    }

    public String checkSecure() {
        int secureProp = getroSecureProp();
        if (secureProp == 0) {
            return "1";
        }//eng/userdebug版本，自带root权限
        return "0";
    }

    private String checksuFile() {
        String[] paths = {"/sbin/su", "/system/bin/su", "/system/xbin/su", "/data/local/xbin/su",
                "/data/local/bin/su", "/system/sd/xbin/su", "/system/bin/failsafe/su", "/data" +
                "/local/su"};
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < paths.length; i++) {
            buffer.append(FileUtil.checkPath(paths[i]));
        }
        return buffer.toString();
    }

    private int getroSecureProp() {
        int secureProp;
        String roSecureObj = CommandUtil.getInstance().getProperty("ro.secure");
        if (roSecureObj == null) secureProp = 1;
        else {
            if ("0".equals(roSecureObj)) secureProp = 0;
            else secureProp = 1;
        }
        return secureProp;
    }

    public static String checkRootWhichSU() {
        Process process = null;
        try {
            process = Runtime.getRuntime().exec(new String[]{"which", "su"});
            BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));
            if (in.readLine() != null) {
                return "1";
            }
            return "0";
        } catch (Throwable t) {
            return "0";
        } finally {
            if (process != null) process.destroy();
        }
    }
}
