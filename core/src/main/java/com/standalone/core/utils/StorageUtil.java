package com.standalone.core.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.storage.StorageManager;
import android.os.storage.StorageVolume;
import android.provider.Settings;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Requirement:
 * <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
 **/
public class StorageUtil {
    private final static String TAG = "EXTERNAL_STORAGE";
    private final static int PERMISSION_REQUEST_CODE = 101;

    public static File getDefaultStorage(Context context) {
        String appName = getAppName(context);
        File file = new File(Environment.getExternalStorageDirectory(), appName);
        if (!file.exists()) {
            if (!file.mkdir()) {
                Log.e(TAG, "Cannot create a new folder");
            }
        }

        return file;
    }

    @RequiresApi(api = Build.VERSION_CODES.R)
    public static File getRemovableStorage(Context context) {
        String appName = getAppName(context);
        StorageManager storageManager = (StorageManager) context.getSystemService(Context.STORAGE_SERVICE);
        List<StorageVolume> storageVolumeList = storageManager.getStorageVolumes();
        StorageVolume storageVolume = storageVolumeList.get(1);
        File file = new File(storageVolume.getDirectory(), appName);
        if (!file.exists()) {
            if (!file.mkdir()) {
                Log.e(TAG, "Cannot create a new folder");
            }
        }

        return file;
    }

    public static File getExtStorage(Context context) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            return getRemovableStorage(context);
        }

        return getDefaultStorage(context);
    }

    public static boolean hasSDCard() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state) || Environment.MEDIA_MOUNTED_READ_ONLY.equals(state);
    }

    public static void requirePermission(Activity activity) {
        if (checkPermission(activity)) return;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            try {
                Intent intent = new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                intent.addCategory("android.intent.category.DEFAULT");
                intent.setData(Uri.parse(String.format("packages:%s", activity.getPackageName())));
                activity.startActivityIfNeeded(intent, PERMISSION_REQUEST_CODE);
            } catch (Exception e) {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                activity.startActivityIfNeeded(intent, PERMISSION_REQUEST_CODE);
            }
        } else {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        }
    }


    private static boolean checkPermission(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            return Environment.isExternalStorageManager();
        }
        int resultCode = ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        return resultCode == PackageManager.PERMISSION_GRANTED;
    }

    private static String getAppName(Context context) {
//        String appName = context.getApplicationInfo().loadLabel(context.getPackageManager()).toString();
        String[] pkgNameList = context.getPackageName().split("\\.");
        int len = pkgNameList.length;
        Log.e(TAG, "AppName: " + pkgNameList[len - 1]);
        return pkgNameList[len - 1];
    }
}
