package com.standalone.core.utils;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;

import androidx.core.content.ContextCompat;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class LogUtil {
    final static String FILENAME = "log.txt";

    public static void write(Context context, String text) {
        try {
            File dir = StorageUtil.getExtStorage(context);
            if(!dir.canWrite()) return;
            File fileLog = new File(dir, FILENAME);
            BufferedWriter buffer = new BufferedWriter(new FileWriter(fileLog, true));
            String today = DateTimeUtil.toString("yyyy-MM-dd HH:mm.ss", DateTimeUtil.now());
            buffer.append(String.format("[%s] %s", today, text));
            buffer.newLine();
            buffer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
