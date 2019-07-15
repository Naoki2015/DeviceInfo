package com.lucky.deviceinfo.info.impl.recognite;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * 能够多开的应用
 * 平行空间、VirtualApp、双开助手、DualSpace、Go双开、双开精灵
 */
public class MultipleApp {

    static {
        System.loadLibrary("bsanti");
    }

    private static final MultipleApp ourInstance = new MultipleApp();

    public static MultipleApp getInstance() {
        return ourInstance;
    }

    private MultipleApp() {
    }

    public String multiAppTags(Context context) {
        return detectFakeUid() + "" + detectFakePath() + isRunInVirtual();
    }

    public String isMultiApp(Context context) {
        return multiAppTags(context).contains("1") ? "1" : "0";
    }

    private native int detectFakeUid();

    private native int detectFakePath();

    private String isRunInVa(Context cxt) {
        if (cxt == null) {
            //throw new IllegalArgumentException("context must be notnull");
            return "0";
        }
        int pid = android.os.Process.myPid();
        String process = getProcessName(cxt, pid);
        if (TextUtils.isEmpty(process)) {
            //throw new RuntimeException("get process name failed");
            return "0";
        }
        String pkg = cxt.getPackageName();
        if (TextUtils.isEmpty(pkg)) {
            //throw new RuntimeException("get package name failed");
            return "0";
        }
        return pkg.equals(process) + "";
    }

    /**
     * 过滤当前非主进程的进程
     *
     * @param cxt
     * @param pid
     * @return
     */
    private  String getProcessName(Context cxt, int pid) {
        try {
            ActivityManager am = (ActivityManager) cxt.getSystemService(Context.ACTIVITY_SERVICE);
            List<ActivityManager.RunningAppProcessInfo> runningApps = am.getRunningAppProcesses();
            if (runningApps != null && !runningApps.isEmpty()) {
                for (ActivityManager.RunningAppProcessInfo procInfo : runningApps) {
                    if (procInfo.pid == pid) {
                        return procInfo.processName;
                    }
                }
            }
        } catch (Exception ignored) {
        }
        return null;
    }

    /**
     * 如果满足同一uid下的两个进程对应的包名，在"/data/data"下有两个私有目录，则该应用被多开了。
     *
     * @return
     */
    private String isRunInVirtual() {

        String filter = getUidStrFormat();
        if (filter == null || filter.length() == 0) {
            return "0";
        }

        String result = exec("ps");
        if (result == null || result.isEmpty()) {
            return "0";
        }

        String[] lines = result.split("\n");
        if (lines == null || lines.length <= 0) {
            return "0";
        }

        int exitDirCount = 0;

        for (int i = 0; i < lines.length; i++) {
            if (lines[i].contains(filter)) {
                int pkgStartIndex = lines[i].lastIndexOf(" ");
                String processName = lines[i].substring(pkgStartIndex <= 0 ? 0 :
                        pkgStartIndex + 1, lines[i].length());
                File dataFile = new File(String.format("/data/data/%s", processName, Locale.CHINA));
                Log.d("bbb", dataFile.getAbsolutePath());
                if (dataFile.exists()) {
                    Log.d("bbb", exitDirCount + "");
                    exitDirCount++;
                }
            }
        }
        if (exitDirCount > 1) {
            return "1";
        }
        return "0";
    }


    private  String exec(String command) {
        BufferedOutputStream bufferedOutputStream = null;
        BufferedInputStream bufferedInputStream = null;
        Process process = null;
        try {
            process = Runtime.getRuntime().exec("sh");
            bufferedOutputStream = new BufferedOutputStream(process.getOutputStream());

            bufferedInputStream = new BufferedInputStream(process.getInputStream());
            bufferedOutputStream.write(command.getBytes());
            bufferedOutputStream.write('\n');
            bufferedOutputStream.flush();
            bufferedOutputStream.close();

            process.waitFor();

            String outputStr = getStrFromBufferInputSteam(bufferedInputStream);
            return outputStr;
        } catch (Exception e) {
            return null;
        } finally {
            if (bufferedOutputStream != null) {
                try {
                    bufferedOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (bufferedInputStream != null) {
                try {
                    bufferedInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (process != null) {
                process.destroy();
            }
        }
    }

    private  String getStrFromBufferInputSteam(BufferedInputStream bufferedInputStream) {
        if (null == bufferedInputStream) {
            return "";
        }
        int BUFFER_SIZE = 512;
        byte[] buffer = new byte[BUFFER_SIZE];
        StringBuilder result = new StringBuilder();
        try {
            while (true) {
                int read = bufferedInputStream.read(buffer);
                if (read > 0) {
                    result.append(new String(buffer, 0, read));
                }
                if (read < BUFFER_SIZE) {
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result.toString();
    }

    @SuppressLint("DefaultLocale")
    private  String getUidStrFormat() {
        String filter = exec("cat /proc/self/cgroup");
        if (filter == null || filter.length() == 0) {
            return null;
        }

        int uidStartIndex = filter.lastIndexOf("uid");
        int uidEndIndex = filter.lastIndexOf("/pid");
        if (uidStartIndex < 0) {
            return null;
        }
        if (uidEndIndex <= 0) {
            uidEndIndex = filter.length();
        }

        filter = filter.substring(uidStartIndex + 4, uidEndIndex);
        try {
            String strUid = filter.replaceAll("\n", "");
            if (isNumericZidai(strUid)) {
                int uid = Integer.valueOf(strUid);
                filter = String.format("u0_a%d", uid - 10000);
                return filter;
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private  boolean isNumericZidai(String str) {
        if (str == null || str.length() == 0) {
            return false;
        }
        for (int i = 0; i < str.length(); i++) {
            if (!Character.isDigit(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }
}
