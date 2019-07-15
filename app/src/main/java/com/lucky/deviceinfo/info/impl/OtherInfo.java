package com.lucky.deviceinfo.info.impl;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;

import com.lucky.deviceinfo.info.IInfo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class OtherInfo implements IInfo {
    @Override
    public Map<String, String> getInfo(Context context) {
        Map<String, String> map = new HashMap<>();
        map.put("SDCid", getSDCid());
        map.put("ANDROID_ID", getAndroidId(context));
        map.put("蓝牙地址", bluetoothMacAddress(context));
        getSDinfo();
        return map;
    }

    private String getSDCid() {
        StringBuilder stringB = new StringBuilder();
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            try {
                File input = new File("/sys/class/mmc_host/mmc1");
                String cid_directory = null;
                int i = 0;
                File[] sid = input.listFiles();

                for (i = 0; i < sid.length; i++) {
                    if (sid[i].toString().contains("mmc1:")) {
                        cid_directory = sid[i].toString();
                        String SID =
                                (String) sid[i].toString().subSequence(cid_directory.length() - 4
                                        , cid_directory.length());
                        //Log.e(TAG, " SID of MMC = " + SID);
//                        stringB.append("==SID=="+SID);
                        break;
                    }
                }
                BufferedReader CID = new BufferedReader(new FileReader(cid_directory + "/cid"));
                String sd_cid = CID.readLine();
                //Log.e(TAG, "CID of the MMC = " + sd_cid);
                stringB.append("==sd_cid==" + sd_cid);

            } catch (Exception e) {
                //Log.e(TAG, "Can not read SD-card cid");
            }

        } else {
            //Log.e(TAG, "External Storage Not available!!");
        }

        return stringB.toString();
    }

    private String getAndroidId(Context context) {
        try {
            String androidId = Settings.System.getString(context.getContentResolver(),
                    Settings.System.ANDROID_ID);

            return androidId;
        } catch (Throwable e) {
            //return "";
        }
        return "";
    }

    //Get Bluetooth
    //In Android 6.0 02:00:00:00:00
    private String bluetoothMacAddress(Context context) {
        try {
            if (Build.VERSION.SDK_INT >= 18 && Build.VERSION.SDK_INT < 28) {
                String blue = Settings.Secure.getString(context.getContentResolver(),
                        "bluetooth_address");
                if (blue != null && !blue.isEmpty()) {
                    return blue.toLowerCase();
                }
            } else if (Build.VERSION.SDK_INT < 18) {
                BluetoothAdapter bAdapt = BluetoothAdapter.getDefaultAdapter();
                if (bAdapt != null && bAdapt.isEnabled()) {
                    @SuppressLint("HardwareIds") String address = bAdapt.getAddress();
                    if (address != null && !address.isEmpty()) {
                        return address.toLowerCase();
                    }
                }
            } else {
                //参考  https://segmentfault.com/a/1190000014597132
                BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                String bluetoothMacAddress = "";
                Field mServiceField = bluetoothAdapter.getClass().getDeclaredField("mService");
                mServiceField.setAccessible(true);

                Object btManagerService = mServiceField.get(bluetoothAdapter);

                if (btManagerService != null) {
                    bluetoothMacAddress = (String) btManagerService.getClass().getMethod("getAddress").invoke(btManagerService);
                    Log.d("ddd","蓝牙地址"+bluetoothMacAddress);
                    return bluetoothMacAddress;
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return "";
    }

    private void getSDinfo() {
        Object localOb;
        String str1 = null;

        try {
            localOb = new FileReader("/sys/block/mmcblk0/device/type");
            localOb = new BufferedReader((Reader) localOb).readLine()
                    .toLowerCase().contentEquals("sd");
            if (localOb != null) {
                str1 = "/sys/block/mmcblk0/device/";
            }
        } catch (Exception e1) {
            System.out.println(e1.getMessage());
        }
        try {
            localOb = new FileReader("/sys/block/mmcblk1/device/type");
            localOb = new BufferedReader((Reader) localOb).readLine()
                    .toLowerCase().contentEquals("sd");
            if (localOb != null) {
                str1 = "/sys/block/mmcblk1/device/";
            }
        } catch (Exception e1) {
            System.out.println(e1.getMessage());
        }
        try {

            localOb = new FileReader("/sys/block/mmcblk2/device/type");
            localOb = new BufferedReader((Reader) localOb).readLine()
                    .toLowerCase().contentEquals("sd");
            if (localOb != null) {
                str1 = "/sys/block/mmcblk2/device/";
            }
        } catch (Exception e1) {
            System.out.println(e1.getMessage());
        }
        localOb = "";
        try {
            localOb = new FileReader(str1 + "name"); // 厂商
            String sd_name = new BufferedReader((Reader) localOb).readLine();
            System.out.println("name: " + sd_name);
        } catch (Exception e1) {
            System.out.println(e1.getMessage());
        }

        try {
            localOb = new FileReader(str1 + "cid"); // SD Card ID
            String sd_cid = new BufferedReader((Reader) localOb).readLine();
            System.out.println("cid: " + sd_cid);
        } catch (Exception e1) {
            System.out.println(e1.getMessage());
        }

        try {
            localOb = new FileReader(str1 + "csd");
            String sd_csd = new BufferedReader((Reader) localOb).readLine();
            System.out.println("csd: " + sd_csd);
        } catch (Exception e1) {
            System.out.println(e1.getMessage());
        }

        try {
            localOb = new FileReader(str1 + "fwrev"); // 固件编号
            String sd_fwrev = new BufferedReader((Reader) localOb).readLine();
            System.out.println("fwrev: " + sd_fwrev);
        } catch (Exception e1) {
            System.out.println(e1.getMessage());
        }

        try {
            localOb = new FileReader(str1 + "hwrev"); // 硬件版本
            String sd_hwrev = new BufferedReader((Reader) localOb).readLine();
            System.out.println("hwrev: " + sd_hwrev);
        } catch (Exception e1) {
            System.out.println(e1.getMessage());
        }

        try {
            localOb = new FileReader(str1 + "manfid"); // manufacture 制造
            String sd_manfid = new BufferedReader((Reader) localOb).readLine();
            System.out.println("manfid: " + sd_manfid);
        } catch (Exception e1) {
            System.out.println(e1.getMessage());
        }

        try {
            localOb = new FileReader(str1 + "oemid"); // 原始设备制造商
            String sd_oemid = new BufferedReader((Reader) localOb).readLine();
            System.out.println("oemid: " + sd_oemid);
        } catch (Exception e1) {
            System.out.println(e1.getMessage());
        }

        try {
            localOb = new FileReader(str1 + "scr");
            String sd_scr = new BufferedReader((Reader) localOb).readLine();
            System.out.println("scr: " + sd_scr);
        } catch (Exception e1) {
            System.out.println(e1.getMessage());
        }

        try {
            localOb = new FileReader(str1 + "serial"); // 串号/序列号
            String sd_serial = new BufferedReader((Reader) localOb).readLine();
            System.out.println("serial: " + sd_serial);
        } catch (Exception e1) {
            System.out.println(e1.getMessage());
        }

        try {
            localOb = new FileReader(str1 + "date"); // 生产日期
            String sd_date = new BufferedReader((Reader) localOb).readLine();
            System.out.println("date: " + sd_date);
        } catch (Exception e1) {
            System.out.println(e1.getMessage());
        }
    }
}
