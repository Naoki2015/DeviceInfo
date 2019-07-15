package com.lucky.dangerinfo.emulator;

import android.annotation.SuppressLint;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Build;
import android.util.Log;

import com.lucky.dangerinfo.utils.CommandUtil;
import com.lucky.dangerinfo.utils.CommonUtil;
import com.lucky.dangerinfo.utils.FileUtil;
import com.lucky.dangerinfo.utils.StringUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import static android.content.Context.SENSOR_SERVICE;
import static com.lucky.dangerinfo.utils.CommonUtil.isBluePackageName;

/**
 * 检测模拟器
 * <p>
 * 检测凤凰os模拟器以及remix模拟器
 */
public class Emulator {

    private static Emulator emulator;

    public static Emulator getInstance() {
        if (emulator == null) {
            emulator = new Emulator();
        }
        return emulator;
    }

    //蓝叠模拟器 读取不到
    //cpuinfo信息  判断是否存在Intel
    //"/data/data/com.bluestacks.home", "/system/priv-app/com.bluestacks.settings.apk"
    public static int TYPE_BLUESTACKS = 1;
    private static final String STRING_TYPE_BLUESTACKS = "BlueStacks";
    @SuppressLint("SdCardPath")
    //,
    // "/sdcard/Android/data/com.bluestacks.appstore",
    private String[] bsAppName = {"/sdcard/Android/data/com.bluestacks.home",
            "/sdcard/Android" + "/data/com.bluestacks.settings", "/sdcard/windows/BstSharedFolder"
            , "/data/app-lib/com.bluestacks.settings"};
    private String[] bsAppPackage = {"com.bluestacks.appmart", "com.bluestacks.settings"};

    //MuMu模拟器
    public static int TYPE_MUMU = 2;
    private static final String STRING_TYPE_MUMU = "MUMU";
    @SuppressLint("SdCardPath")
    private String[] mmAppName = {"/data/data/com.mumu.launcher", "/data/data/com.mumu.store",
            "/data/data/com.netease.mumu.cloner", "/data/dalvik-cache/profiles/com.mumu.launcher"
            , "/data/dalvik-cache/profiles/com.mumu.store"};
    private String[] mmAppPackage = {"com.mumu.store", "com.mumu.launcher", "com.mumu.audio"};

    //逍遥模拟器
    public static int TYPE_XIAOYAO = 3;
    private static final String STRING_TYPE_XIAOYAO = "XIAOYAO";
    @SuppressLint("SdCardPath")
    private String[] xyAppName = {"/data/data/com.microvirt.launcher",
            "/data/data/com.microvirt" + ".download", "/data/data/com.microvirt.guide",
            "/data" + "/data/com" + ".microvirt" + ".installer", "/data/data/com.microvirt" +
            ".market", "/data" + "/data/com.microvirt" + ".memuime"};
    private String[] xyAppPackage = {"com.microvirt.launcher", "com.microvirt.download", "com" +
            ".microvirt.guide", "com.microvirt.installer", "com.microvirt.market", "com.microvirt"
            + ".memuime",};

    //天天模拟器
    public static int TYPE_TIANTIAN = 4;
    private static final String STRING_TYPE_TIANTIAN = "TIANTIAN";
    @SuppressLint("SdCardPath")
    private String[] ttAppName = {"/data/data/com.tiantian.ime", "/system/priv-app" +
            "/TianTianLauncher/TianTianLauncher.apk", "/init.ttVM_x86.rc", "/ueventd.ttVM_x86.rc"
            , "/fstab.ttVM_x86", "/system/bin/ttVM-prop"};
    private String[] ttAppPackage = {"com.tiantian.ime"};

    //雷电模拟器
    public static int TYPE_LEIDIAN = 5;
    private static final String STRING_TYPE_LEIDIAN = "LEIDIAN";
    @SuppressLint("SdCardPath")
    private String[] ldAppName = {"/sdcard/ldAppStore", "/system/app/ldAppStore/ldAppStore.apk",
            "/lib/libldutils.so", "lib/hw/gps.ld.so"};
    private String[] ldAppPackage = {"", ""};

    //夜神模拟器(/system/app/NoxHelp_zh.apk 该路径会卡死)
    public static int TYPE_NOX = 6;
    private static final String STRING_TYPE_NOX = "NOX";
    @SuppressLint("SdCardPath")
    private String[] ysAppName = {"/storage/emulated/legacy/BigNoxHD", "/lib/libnoxd.so",
            "/lib" + "/libnoxspeedup.so", "/data/property/persist.nox.androidid", "system/app" +
            "/Helper" + "/NoxHelp_zh.apk", "/data" + "/dalvik-cache/profiles/com.bignox.app" +
            ".store" + ".hd"};
    private String[] ysAppPackage = {"com.bignox.google.installer", "com.bignox.app.store.hd"};

    //Genymontion模拟器
    public static int TYPE_GENY = 7;
    private static final String STRING_TYPE_GENY = "GENY";
    @SuppressLint("SdCardPath")
    private String[] geAppName = {"/data/data/com.google.android.launcher.layouts.genymotion",
            "/system/app/GenymotionLayout/GenymotionLayout.apk", "/dev" + "/socket/baseband_genyd"
            , "system/bin/genymotion-vbox-sf"};
    private String[] geAppPackage = {"com.genymotion.superuser", "com.genymotion.genyd", "com" +
            ".genymotion.systempatcher"};

    //itools模拟器(附属于genymontion)
    public static int TYPE_ITOOLS = 11;
    private static final String STRING_TYPE_ITOOLS = "ITOOLS";
    @SuppressLint("SdCardPath")
    private String[] itoolsAppName = {"/data/data/cn.itools.vm.launcher",
            "/data/data/cn.itools" + ".avdmarket", "/data/data/cn.itools.vm.proxy"};
    private String[] itoolsAppPackage = {"cn.itools.vm.launcher", "Emulator", "cn" + ".itools.vm" +
            ".proxy"};

    //LDS模拟器（鲁大师模拟器）
    public static int TYPE_LDX = 8;
    private static final String STRING_TYPE_LDS = "LDSBOX";
    @SuppressLint("SdCardPath")
    private String[] lbAppName = {"/ldsboxshare", "/init.ludashi.rc", "/init.ludashi.sh"};


    //51模拟器
    public static int TYPE_51 = 9;
    private static final String STRING_TYPE_51 = "51";
    private String[] w1AppName = {"/mnt/prebundledapps/downloads/51service.apk", "/mnt/sdcard/" +
            ".51service"};

    //海马玩模拟器
    public static int TYPE_Drod4X = 10;
    private static final String STRING_TYPE_Drod4X = "Drod4X";
    private String[] hmwAppName = {"/system/bin/droid4x", "/system/bin/droid4x-prop",
            "/system" + "/lib/libdroid4x.so", "/system/etc/init.droid4x.sh"};
    private String[] hmwAppPackage = {"com.haimawan.push", "com.haima.helpcenter", "me.haima" +
            ".androidassist"};


    //原生模拟器
    public static int TYPE_AS = 10;
    private static final String STRING_TYPE_AS = "AS";
    private String[] asAppName = {"/sys/module/goldfish_audio", "/sys/module/goldfish_sync"};

    //未知（未能识别的模拟器）
    private static final String STRING_TYPE_UNKNOWN = "unknown";

    //对于特定模拟器的阀值
    private static final int MAX_INDEX = 2;
    //对于其他未能识别模拟器名字的模拟器阀值
    private static final int OTHER_MAX_INDEX = 5;

    private String isMark(String[] appName) {
        StringBuilder builder = new StringBuilder();
        for (String s : appName) {
            if (FileUtil.checkPath(s).equals("1")) {
                builder.append("1");
            } else {
                builder.append("0");
            }
        }
        return builder.toString();
    }

    private String isvm;
    private String vmName;
    private String vmTag1;
    private String vmTag2;

    public void distinguishVM(Context context) {
        vmTag2 = isOther(context);
        if (StringUtil.getNumber(isXiaoYao(context)) > MAX_INDEX) {
            isvm = "1";
            vmName = STRING_TYPE_XIAOYAO;
            vmTag1 = isXiaoYao(context);
            return;
        } else if (StringUtil.getNumber(isNox(context)) > MAX_INDEX) {
            isvm = "1";
            vmName = STRING_TYPE_NOX;
            vmTag1 = isNox(context);
            return;
        } else if (StringUtil.getNumber(isBlueStacks(context)) > MAX_INDEX) {
            if (StringUtil.getNumber(is51(context)) > 0) {
                isvm = "1";
                vmName = STRING_TYPE_51;
                vmTag1 = is51(context);
            } else {
                isvm = "1";
                vmName = STRING_TYPE_BLUESTACKS;
                vmTag1 = isBlueStacks(context);
            }
            return;
        } else if (StringUtil.getNumber(isMuMu(context)) > MAX_INDEX) {
            isvm = "1";
            vmName = STRING_TYPE_MUMU;
            vmTag1 = isMuMu(context);
            return;
        } else if (StringUtil.getNumber(isGenymon(context)) > MAX_INDEX) {
            if (StringUtil.getNumber(isITools(context)) > MAX_INDEX) {
                isvm = "1";
                vmName = STRING_TYPE_ITOOLS;
                vmTag1 = isITools(context);
            } else {
                isvm = "1";
                vmName = STRING_TYPE_GENY;
                vmTag1 = isGenymon(context);
            }
            return;
        } else if (StringUtil.getNumber(isTianTian(context)) > MAX_INDEX) {
            isvm = "1";
            vmName = STRING_TYPE_TIANTIAN;
            vmTag1 = isTianTian(context);
            return;
        } else if (StringUtil.getNumber(isDroid4x(context)) > MAX_INDEX) {
            isvm = "1";
            vmName = STRING_TYPE_Drod4X;
            vmTag1 = isDroid4x(context);
            return;
        } else if (StringUtil.getNumber(isLeiDian(context)) > MAX_INDEX) {
            isvm = "1";
            vmName = STRING_TYPE_LEIDIAN;
            vmTag1 = isLeiDian(context);
            return;
        } else if (StringUtil.getNumber(isAS(context)) >= MAX_INDEX) {
            isvm = "1";
            vmName = STRING_TYPE_AS;
            vmTag1 = isAS(context);
            return;
        } else if (StringUtil.getNumber(isOther(context)) > OTHER_MAX_INDEX) {
            isvm = "1";
            vmName = STRING_TYPE_UNKNOWN;
            return;
        } else {
            isvm = "0";
            //vmName = STRING_TYPE_UNKNOWN;
            return;
        }
    }

    public String getVM() {
        return isvm;
    }

    public String getVmName() {
        return vmName;
    }

    public String getVmTag1() {
        return vmTag1;
    }

    public String getVmTag2() {
        return vmTag2;
    }

    /**
     * 检测逍遥模拟器
     * 模拟器版本6.1.0
     * 安卓版本5.1.1
     */
    private String isXiaoYao(Context context) {
        String value = isMark(xyAppName) + "-" + CommonUtil.isAppPackage(context, xyAppPackage);
        return value;
        // Log.d("ggg", "逍遥:" + value);
    }

    /**
     * 检测夜神模拟器
     * 模拟器版本 6.2.8.0003
     * 安卓版本5.1.1
     * <p>
     * <p>
     * 可能误判为52新星模拟器（待改进）都是Nox团队开发，都属于夜神范围
     *
     * @param context
     */
    private String isNox(Context context) {
        String value = isMark(ysAppName) + "-" + CommonUtil.isAppPackage(context, ysAppPackage);
        //String file = FileUtil.getFile("/data/property/persist.nox.wifimac");
        Log.d("ggg", "夜神:" + value);
        //Log.d("ggg", "夜神:" + file);
        return value;
    }

    /**
     * 检测蓝叠模拟器
     * 旧版本:
     * 平台版本号 3.1.20.643
     * 引擎版本号 2.60.88.3420
     * 安卓版本  4.4.2
     * <p>
     * 新版本:
     * 平台版本号 3.1.20.643
     * 引擎版本号 4.50.6.2003
     * 安卓版本  7.1.0
     * <p>
     * <p>
     * 可能误判为51模拟器（修复）
     *
     * @param context
     */
    private String isBlueStacks(Context context) {
        String value = isMark(bsAppName) + "-" + isBluePackageName(context, bsAppPackage);
        //通过读取以下两个文件夹来进行apk匹配，com.bluestacks.BstCommandProcessor.apk，com.bluestacks.settings.apk
        //com.bluestacks.bstfolder.apk
        String file = FileUtil.getFile("/system/priv-app/");
        if (file.contains("com.bluestacks.settings.apk")) {
            value = value + "-" + "1";
        } else {
            value = value + "-" + "0";
        }
        //com.bluestacks.home.apk，com.bluestacks.searchapp，com.bluestacks.setup.apk，com
        // .bluestacks.appfinder.apk
        //com.bluestacks.keymappingtool.apk，suppressed.dbWindowsFileManager.apk，com.bluestacks
        // .appmart.apk
        String file1 = FileUtil.getFile("/data/downloads/");
        if (file1.contains("com.bluestacks.home.apk")) {
            value = value + "-" + "1";
        } else {
            value = value + "-" + "0";
        }
        Log.d("ggg", "蓝叠:" + value);
        //Log.d("ggg", "蓝叠:" + file);
        return value;
    }


    /**
     * 模拟器版本  3.1.3.9
     * 烈风版
     * 安卓版本    4.4.2
     *
     * @param context
     * @return
     */
    private String is51(Context context) {
        String value = isMark(w1AppName);
        return value;
    }


    /**
     * 检测网易MuMu模拟器
     * 模拟器版本 2.1.7
     * 桌面启动器版本 2.3.1
     * 安卓版本6.0.1
     *
     * @param context
     */
    private String isMuMu(Context context) {
        String value = isMark(mmAppName) + "-" + CommonUtil.isAppPackage(context, mmAppPackage);
        String model = Build.MODEL;
        if (model.equals("MuMu")) {
            value = value + "-" + "1";
        } else {
            value = value + "-" + "0";
        }
        Log.d("ggg", "MuMu:" + value);
        //Log.d("ggg", "夜神:" + file);
        return value;
    }


    /**
     * 检测Genymotion模拟器
     * 模拟器版本 3.0.1
     * 安卓版本5.0、9.0
     * <p>
     * 误判为畅玩模拟器或者iTools模拟器（需要进一步判断）
     *
     * @param context
     */
    private String isGenymon(Context context) {
        String value = isMark(geAppName) + "-" + CommonUtil.isSystemApp(context, geAppPackage);
        String model = Build.MODEL;
        if (model.equals("Genymotion")) {
            value = value + "-" + "1";
        } else {
            value = value + "-" + "0";
        }
        String user = Build.USER;
        if (user.equals("genymotion")) {
            value = value + "-" + "1";
        } else {
            value = value + "-" + "0";
        }
        String sensor = getSensorName(context);
        if (sensor.contains("Genymotion")) {
            value = value + "-" + "1";
        } else {
            value = value + "-" + "0";
        }
        //isSystemApp(context);
        Log.d("ggg", "Genymotion:" + value);
        //Log.d("ggg", "夜神:" + file);
        return value;
    }

    /**
     * iTools模拟器和畅玩模拟器一样的
     * iTools模拟器版本:2.0.8.9
     * 安卓版本:4.4.4
     */
    private String isITools(Context context) {
        String value = isMark(itoolsAppName) + "-" + CommonUtil.isSystemApp(context,
                itoolsAppPackage);
        return value;
    }

    /**
     * 检测天天模拟器
     * 模拟器版本 3.2.5
     * 安卓版本6.0.1
     *
     * @param context
     */
    private String isTianTian(Context context) {
        String value = isMark(ttAppName) + "-" + CommonUtil.isAppPackage(context, ttAppPackage);
        String sensor = getSensorName(context);
        if (sensor.contains("TiantianVM")) {
            value = value + "-" + "1";
        } else {
            value = value + "-" + "0";
        }
        if (Build.HARDWARE.equals("ttVM_x86")) {
            value = value + "-" + "1";
        } else {
            value = value + "-" + "0";
        }
        return value;
    }


    /**
     * 检测海马玩模拟器（Droid4x）
     * 模拟器版本 0.10.6.Beta
     * 安卓版本 4.2.2
     */
    private String isDroid4x(Context context) {
        String value = isMark(hmwAppName) + "-" + CommonUtil.isAppPackage(context, hmwAppPackage);
        //String file = FileUtil.getFile("/data/property/persist.nox.wifimac");
        Log.d("ggg", "海马玩:" + value);
        return value;
    }

    /**
     * 检测雷电模拟器
     * 模拟器版本 3.42
     * 安卓版本 5.1.1
     */
    private String isLeiDian(Context context) {
        String value = isMark(ldAppName) + "-" + CommonUtil.isAppPackage(context, ldAppPackage);
        //String file = FileUtil.getFile("/data/property/persist.nox.wifimac");
        Log.d("ggg", "雷电:" + value);
        //return value;
        return value;
    }

    /**
     * 检测原生模拟器（AS自带的）
     * 针对QEMU, KVM, QEMU-KVM 和 Goldfish的理解
     * https://blog.csdn.net/span76/article/details/19165345
     */
    private String isAS(Context context) {
        String value = isMark(asAppName);
        //String file = FileUtil.getFile("/data/property/persist.nox.wifimac");
        if (getSensorName(context).startsWith("Goldfish")) {
            value = value + "-" + "1";
        } else {
            value = value + "-" + "0";
        }

        if (Build.HARDWARE.equals("ranchu")) {
            value = value + "-" + "1";
        } else {
            value = value + "-" + "0";
        }
        return value;
    }

    /**
     * 检测不属于以上名字的模拟器
     */
    private String isOther(Context context) {
        String value =
                checkcpu() + "-" + getBuildInfo() + "-" + isLight(context) + "-" + existQemu() + hasQEmuProps() + existQemuDrivers() + "-" + vBox();
        return value;
    }

    /**
     * 获取所有传感器名字
     *
     * @param context
     * @return
     */
    public String getSensorName(Context context) {
        StringBuilder sb = new StringBuilder();
        SensorManager sensorManager = (SensorManager) context.getSystemService(SENSOR_SERVICE);
        List<Sensor> sensor = sensorManager.getSensorList(Sensor.TYPE_ALL);
        for (Sensor sensor1 : sensor) {
            sb.append(sensor1.getName());
        }
        return sb.toString();
    }


    //其他检测模拟器方案

    /**
     * 判断cpu是否为电脑来判断 模拟器
     * 注意安卓平板可能为intel
     *
     * @return true 为模拟器
     */
    private String checkcpu() {
        String cpuinfo = "";
        String result = "";
        try {
            String[] args = {"/system/bin/cat", "/proc/cpuinfo"};
            ProcessBuilder cmd = new ProcessBuilder(args);
            Process process = cmd.start();
            StringBuffer sb = new StringBuffer();
            String readLine = "";
            BufferedReader responseReader =
                    new BufferedReader(new InputStreamReader(process.getInputStream(), "utf-8"));
            while ((readLine = responseReader.readLine()) != null) {
                sb.append(readLine);
            }
            responseReader.close();
            cpuinfo = sb.toString().toLowerCase();
        } catch (IOException ex) {
        }
        if ((cpuinfo.contains("intel"))) {
            result = result + "1";
        } else {
            result = result + "0";
        }
        if (result.contains("amd")) {
            result = result + "1";
        } else {
            result = result + "0";
        }
        return result;
    }

    /**
     * 根据部分特征参数设备信息来判断是否为模拟器
     *
     * @return true 为模拟器
     */
    public String getBuildInfo() {
        String result = "";

        //检测cpu架构
        if (Build.CPU_ABI.contains("x86") || Build.CPU_ABI2.contains("x86")) {
            result = result + "1";
        } else {
            result = result + "0";
        }


        if (Build.FINGERPRINT.startsWith("generic")) {
            result = result + "1";
        } else {
            result = result + "0";
        }
        if (Build.FINGERPRINT.startsWith("generic_x86")) {
            result = result + "1";
        } else {
            result = result + "0";
        }
        if (Build.FINGERPRINT.toLowerCase().contains("test-keys") || Build.FINGERPRINT.toLowerCase().contains("dev-keys")) {
            result = result + "1";
        } else {
            result = result + "0";
        }


        if (Build.MODEL.contains("Emulator")) {
            result = result + "1";
        } else {
            result = result + "0";
        }
        if (Build.MODEL.contains("google_sdk")) {
            result = result + "1";
        } else {
            result = result + "0";
        }
        if (Build.MODEL.contains("Android SDK built for x86")) {
            result = result + "1";
        } else {
            result = result + "0";
        }
        if (Build.MODEL.contains("Android SDK built for x86_64")) {
            result = result + "1";
        } else {
            result = result + "0";
        }


        //厂商
        if (Build.MANUFACTURER.contains("Genymotion")) {
            result = result + "1";
        } else {
            result = result + "0";
        }
        if (Build.MANUFACTURER.contains("unknown")) {
            result = result + "1";
        } else {
            result = result + "0";
        }
        if (Build.MANUFACTURER.contains("Google")) {
            result = result + "1";
        } else {
            result = result + "0";
        }


        if ((Build.BRAND.startsWith("generic") || Build.BRAND.startsWith("generic_x86"))) {
            result = result + "1";
        } else {
            result = result + "0";
        }


        if (Build.HARDWARE.equals("goldfish")) {
            result = result + "1";
        } else {
            result = result + "0";
        }


        if (Build.DEVICE.equals("vbox86p")) {
            result = result + "1";
        } else {
            result = result + "0";
        }
        if (Build.DEVICE.startsWith("generic") || Build.DEVICE.startsWith("generic_x86") || Build.DEVICE.startsWith("generic_x86_64")) {
            result = result + "1";
        } else {
            result = result + "0";
        }


        //product
        if ("google_sdk".equals(Build.PRODUCT)) {
            result = result + "1";
        } else {
            result = result + "0";
        }
        if ("sdk".equals(Build.PRODUCT)) {
            result = result + "1";
        } else {
            result = result + "0";
        }
        if ("sdk_google".equals(Build.PRODUCT)) {
            result = result + "1";
        } else {
            result = result + "0";
        }
        if ("sdk_x86".equals(Build.PRODUCT)) {
            result = result + "1";
        } else {
            result = result + "0";
        }
        if ("vbox86p".equals(Build.PRODUCT)) {
            result = result + "1";
        } else {
            result = result + "0";
        }
        if ("sdk_google_phone_x86".equals(Build.PRODUCT)) {
            result = result + "1";
        } else {
            result = result + "0";
        }
        return result;
    }


    /**
     * 光传感器检测
     *
     * @param context
     * @return
     */
    public String isLight(Context context) {
        SensorManager sensorManager = (SensorManager) context.getSystemService(SENSOR_SERVICE);
        Sensor sensor8 = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        //光   
        if (null == sensor8) {
            return "1";
        } else {
            return "0";
        }
    }


    //检测ro.kernel.qemu是否为1，内核qemu
    public String hasQEmuProps() {
        String property_value = CommandUtil.getInstance().getProperty("ro.kernel.qemu");
        if (property_value.equals("1")) {
            return "1";
        } else {
            return "0";
        }
    }

    /**
     * 基于qumu的模拟器特定属性
     */
    private String[] known_files = {"/system/lib/libc_malloc_debug_qemu.so", "/sys/qemu_trace",
            "/system/bin/qemu.props", "/system/bin/qemud"};

    private String existQemu() {
        String result = "";
        for (int i = 0; i < known_files.length; i++) {
            String file_name = known_files[i];
            File qemu_file = new File(file_name);
            if (qemu_file.exists()) {
                result = result + "1";
            } else {
                result = result + "0";
            }
        }
        return result;
    }


    /**
     * 读取驱动文件, 检查是否包含已知的qemu驱动
     *
     * @return {@code true} if any known drivers where found to exist or {@code false} if not.
     */
    public String existQemuDrivers() {
        String result = "";
        for (File drivers_file : new File[]{new File("/proc/tty/drivers"), new File("/proc" +
                "/cpuinfo")}) {
            if (drivers_file.exists() && drivers_file.canRead()) {
                // We don't care to read much past things since info we care about should be
                // inside here
                byte[] data = new byte[1024];
                try {
                    InputStream is = new FileInputStream(drivers_file);
                    is.read(data);
                    is.close();
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
                String driver_data = new String(data);
                /**
                 * 基于qemu的驱动文件判断
                 */
                String known_qemu_drivers = "goldfish";
                if (driver_data.contains(known_qemu_drivers)) {
                    result = result + "1";
                } else {
                    result = result + "0";
                }
            }
        }
        return result;
    }


    private String hasEth0Interface() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                if (intf.getName().equals("eth0")) return "1";
            }
        } catch (SocketException ex) {
        }
        return "0";
    }

    private String vBox() {
        String result = isMark(vBoxFile);
        return result;
    }

    private String[] vBoxFile = {"/data/youwave_id", "/dev/vboxguest", "/dev/vboxuser", "/mnt" +
            "/prebundledapps/bluestacks.prop.orig", "/mnt/prebundledapps/propfiles/ics" +
            ".bluestacks" + ".prop.note", "/mnt/prebundledapps/propfiles/ics.bluestacks.prop.s2",
            "/mnt" + "/prebundledapps/propfiles/ics.bluestacks.prop.s3", "/proc/irq/9/vboxguest",
            "/sys" + "/bus/pci/drivers/vboxguest", "/sys/bus/pci/drivers/vboxguest/0000:00:04.0",
            "/sys" + "/bus/pci/drivers/vboxguest/bind", "/sys/bus/pci/drivers/vboxguest/module",
            "/sys/bus" + "/pci/drivers/vboxguest/new_id", "/sys/bus/pci/drivers/vboxguest" +
            "/remove_id", "/sys" + "/bus/pci/drivers/vboxguest/uevent", "/sys/bus/pci/drivers" +
            "/vboxguest/unbind", "/sys" + "/bus/platform/drivers/qemu_pipe", "/sys/bus/platform" +
            "/drivers/qemu_trace", "/sys" + "/class/bdi/vboxsf-c", "/sys/class/misc/vboxguest",
            "/sys/class/misc/vboxuser", "/sys" + "/devices/virtual/bdi/vboxsf-c", "/sys/devices" +
            "/virtual/misc/vboxguest", "/sys/devices" + "/virtual/misc/vboxguest/dev", "/sys" +
            "/devices/virtual/misc/vboxguest/power", "/sys" + "/devices/virtual/misc/vboxguest" +
            "/subsystem", "/sys/devices/virtual/misc/vboxguest" + "/uevent", "/sys/devices" +
            "/virtual/misc/vboxuser", "/sys/devices/virtual/misc/vboxuser" + "/dev", "/sys" +
            "/devices/virtual/misc/vboxuser/power", "/sys/devices/virtual/misc" + "/vboxuser" +
            "/subsystem", "/sys/devices/virtual/misc/vboxuser/uevent", "/sys/module" +
            "/vboxguest", "/sys/module/vboxguest/coresize", "/sys/module/vboxguest/drivers",
            "/sys/module/vboxguest/drivers/pci:vboxguest", "/sys/module/vboxguest/holders", "/sys"
            + "/module/vboxguest/holders/vboxsf", "/sys/module/vboxguest/initsize",
            "/sys/module" + "/vboxguest/initstate", "/sys/module/vboxguest/notes", "/sys/module" +
            "/vboxguest/notes/" + ".note.gnu.build-id", "/sys/module/vboxguest/parameters", "/sys" +
            "/module/vboxguest" + "/parameters/log", "/sys/module/vboxguest/parameters/log_dest",
            "/sys/module" + "/vboxguest/parameters/log_flags", "/sys/module/vboxguest/refcnt",
            "/sys/module" + "/vboxguest/sections", "/sys/module/vboxguest/sections/" +
            ".altinstructions", "/sys" + "/module/vboxguest/sections/.altinstr_replacement",
            "/sys/module/vboxguest/sections/" + ".bss", "/sys/module/vboxguest/sections/.data",
            "/sys/module/vboxguest/sections/" + ".devinit.data", "/sys/module/vboxguest/sections/" +
            ".exit.text", "/sys/module/vboxguest" + "/sections/.fixup", "/sys/module/vboxguest" +
            "/sections/.gnu.linkonce.this_module", "/sys" + "/module/vboxguest/sections/.init" +
            ".text", "/sys/module/vboxguest/sections/.note.gnu" + ".build-id", "/sys/module" +
            "/vboxguest/sections/.rodata", "/sys/module/vboxguest" + "/sections/.rodata.str1.1",
            "/sys/module/vboxguest/sections/.smp_locks", "/sys/module" + "/vboxguest/sections/" +
            ".strtab", "/sys/module/vboxguest/sections/.symtab", "/sys/module" + "/vboxguest" +
            "/sections/.text", "/sys/module/vboxguest/sections/__ex_table", "/sys" + "/module" +
            "/vboxguest/sections/__ksymtab", "/sys/module/vboxguest/sections" +
            "/__ksymtab_strings", "/sys/module/vboxguest/sections/__param", "/sys/module" +
            "/vboxguest/srcversion", "/sys/module/vboxguest/taint", "/sys/module/vboxguest/uevent"
            , "/sys/module/vboxguest/version", "/sys/module/vboxsf", "/sys/module/vboxsf/coresize"
            , "/sys/module/vboxsf/holders", "/sys/module/vboxsf/initsize",
            "/sys/module/vboxsf" + "/initstate", "/sys/module/vboxsf/notes", "/sys/module/vboxsf" +
            "/notes/.note.gnu" + ".build-id", "/sys/module/vboxsf/refcnt", "/sys/module/vboxsf" +
            "/sections", "/sys/module" + "/vboxsf/sections/.bss", "/sys/module/vboxsf/sections/" +
            ".data", "/sys/module/vboxsf" + "/sections/.exit.text", "/sys/module/vboxsf/sections/" +
            ".gnu.linkonce.this_module", "/sys/module/vboxsf/sections/.init.text", "/sys/module" +
            "/vboxsf/sections/.note.gnu" + ".build-id", "/sys/module/vboxsf/sections/.rodata",
            "/sys/module/vboxsf/sections/" + ".rodata.str1.1", "/sys/module/vboxsf/sections/" +
            ".smp_locks", "/sys/module/vboxsf" + "/sections/.strtab", "/sys/module/vboxsf" +
            "/sections/.symtab", "/sys/module/vboxsf" + "/sections/.text", "/sys/module/vboxsf" +
            "/sections/__bug_table", "/sys/module/vboxsf" + "/sections/__param", "/sys/module" +
            "/vboxsf/srcversion", "/sys/module/vboxsf/taint", "/sys/module/vboxsf/uevent", "/sys" +
            "/module/vboxsf/version", "/sys/module/vboxvideo", "/sys/module/vboxvideo/coresize",
            "/sys/module/vboxvideo/holders", "/sys/module" + "/vboxvideo/initsize", "/sys/module" +
            "/vboxvideo/initstate", "/sys/module/vboxvideo" + "/notes", "/sys/module/vboxvideo" +
            "/notes/.note.gnu.build-id", "/sys/module/vboxvideo" + "/refcnt", "/sys/module" +
            "/vboxvideo/sections", "/sys/module/vboxvideo/sections/.data", "/sys/module/vboxvideo" +
            "/sections/.exit.text", "/sys/module/vboxvideo/sections/.gnu" + ".linkonce" +
            ".this_module", "/sys/module/vboxvideo/sections/.init.text", "/sys/module" +
            "/vboxvideo/sections/.note.gnu.build-id", "/sys/module/vboxvideo/sections/.rodata" +
            ".str1.1", "/sys/module/vboxvideo/sections/.strtab", "/sys/module/vboxvideo/sections" +
            "/" + ".symtab", "/sys/module/vboxvideo/sections/.text", "/sys/module/vboxvideo" +
            "/srcversion", "/sys/module/vboxvideo/taint", "/sys/module/vboxvideo/uevent", "/sys" +
            "/module" + "/vboxvideo/version", "/system/app/bluestacksHome.apk", "/system/bin" +
            "/androVM-prop", "/system/bin/androVM-vbox-sf", "/system/bin/androVM_setprop",
            "/system/bin" + "/get_androVM_host", "/system/bin/mount.vboxsf", "/system/etc/init" +
            ".androVM.sh", "/system/etc/init.buildroid.sh", "/system/lib/hw/audio.primary.vbox86" +
            ".so", "/system" + "/lib/hw/camera.vbox86.so", "/system/lib/hw/gps.vbox86.so",
            "/system/lib/hw/gralloc" + ".vbox86.so", "/system/lib/hw/sensors.vbox86.so", "/system" +
            "/lib/modules/3.0" + ".8-android-x86+/extra/vboxguest", "/system/lib/modules/3.0" +
            ".8-android-x86+/extra/vboxguest/vboxguest.ko", "/system/lib/modules/3.0" + ".8" +
            "-android-x86+/extra/vboxsf", "/system/lib/modules/3.0" + ".8-android-x86+/extra" +
            "/vboxsf/vboxsf.ko", "/system/lib/vboxguest.ko", "/system/lib" + "/vboxsf.ko",
            "/system/lib/vboxvideo.ko", "/system/usr/idc/androVM_Virtual_Input.idc", "/system/usr" +
            "/keylayout/androVM_Virtual_Input.kl", "/system/xbin/mount.vboxsf", "/ueventd" +
            ".android_x86.rc", "/ueventd.vbox86.rc", "/ueventd.goldfish.rc", "/fstab" + ".vbox86"
            , "/init.vbox86.rc", "/init.goldfish.rc"};

}
