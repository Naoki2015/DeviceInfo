package com.lucky.dangerinfo.xposed;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Xposed {

    private Context context;

    public String isXposed() {
        try {
            if (checkPackage(context) || checkProc() || checkStack()) {
                return "1";
            }
        } catch (Throwable e) {
            BSLog.e("isXposed" + e.getMessage());
        }
        return "0";
    }

    public String xposedTags() {
        StringBuilder stringBuilder = new StringBuilder("");
        try {
            if (checkPackage(context)) {
                stringBuilder.append("1");
            } else {
                stringBuilder.append("0");
            }
            if (checkPackage(context)) {
                stringBuilder.append("1");
            } else {
                stringBuilder.append("0");
            }
            if (checkPackage(context)) {
                stringBuilder.append("1");
            } else {
                stringBuilder.append("0");
            }
        } catch (Throwable e) {
            BSLog.e("xposedTags:" + e.getMessage());
        }
        return stringBuilder.toString();
    }

    /**
     * 用PakageManager类来检测包名来判断是否安装了Xposed框架和CydiaSubstrate框架。
     * 1表示Xposed
     * 2表示CydiaSubstrate
     * 0表示上面两种框架都没装
     *
     * @param context
     * @return
     */
    private boolean checkPackage(Context context) {
        try {
            PackageManager packageManager = context.getApplicationContext().getPackageManager();
            List<ApplicationInfo> appliacationInfoList =
                    packageManager.getInstalledApplications(PackageManager.GET_META_DATA);
            for (ApplicationInfo item : appliacationInfoList) {
                if (item.packageName.equals("de.robv.android.xposed.installer")) {
                    return true;
                }
                if (item.packageName.equals("com.saurik.substrate")) {
                    return true;
                }
            }
        } catch (Throwable e) {
            BSLog.w("Collect cheatCheck1 Error");
        }
        return false;
    }

    /**
     * 1表示Substrate is active on the device.
     * 2表示A method on the stack trace has been hooked using Substrate.
     * 3表示Xposed is active on the device.
     * 4表示A method on the stack trace has been hooked using Xposed.
     *
     * @return
     */
    private boolean checkStack() {
        try {
            throw new Exception("Deteck hook");
        } catch (Exception e) {
            //StringBuffer buffer = new StringBuffer("");
            int zygoteInitCallCount = 0;
            for (StackTraceElement item : e.getStackTrace()) {
                // 检测"com.android.internal.os.ZygoteInit"是否出现两次，如果出现两次，则表明Substrate框架已经安装
                if (item.getClassName().equals("com.android.internal.os.ZygoteInit")) {
                    zygoteInitCallCount++;
                    if (zygoteInitCallCount == 2) {
                        BSLog.w("Substrate is active on the device.");
                        //buffer.append("1");
                        return true;
                    }
                }
                if (item.getClassName().equals("com.saurik.substrate.MS$2") && item.getMethodName().equals("invoke")) {
                    BSLog.w("A method on the stack trace has been hooked using Substrate.");
                    return true;
                }
                if (item.getClassName().equals("de.robv.android.xposed.XposedBridge") && item.getMethodName().equals("main")) {
                    BSLog.w("Xposed is active on the device.");
                    return true;
                }
                if (item.getClassName().equals("de.robv.android.xposed.XposedBridge") && item.getMethodName().equals("handleHookedMethod")) {
                    BSLog.w("A method on the stack trace has been hooked using Xposed.");
                    return true;
                }
            }
            return false;
        }
    }

    //4.用 /proc/[pid]/maps来探测内存中可疑的对象和JARs对象。
    private boolean checkProc() {
        Set<String> libraries = new HashSet<String>();
        String mapsFilename = "/proc/" + android.os.Process.myPid() + "/maps";
        try {
            BufferedReader reader = new BufferedReader(new FileReader(mapsFilename));
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.endsWith(".so") || line.endsWith(".jar")) {
                    int n = line.lastIndexOf(" ");
                    libraries.add(line.substring(n + 1));
                }
            }
            for (String library : libraries) {
                if (library.contains("com.saurik.substrate")) {
                    BSLog.w("Substrate shared object found: " + library);
                    return true;
                }
                if (library.contains("XposedBridge.jar")) {
                    BSLog.w("Xposed JAR found: " + library);
                    return true;
                }
            }
            reader.close();
        } catch (Exception e) {
            BSLog.w("Collect cheatCheck3 Error");
        }
        return false;
    }

    private static class BSLog {

        public static void w(String value) {
            Log.w("ddd", value);
        }

        public static void e(String value) {
            Log.e("ddd", value);
        }
    }

}
