package com.siiri.uniplugin_calendar.utils;


import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;


public class Utils {

    /**
     * 获取 app name
     *
     * @param context 上下文
     * @return app name
     */
    public static String getAppName(Context context) {
        return context.getApplicationInfo().loadLabel(context.getPackageManager()).toString();
    }

    public static boolean arePermissionsGranted(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            boolean writeGranted = context.checkSelfPermission(Manifest.permission.WRITE_CALENDAR) == PackageManager.PERMISSION_GRANTED;
            boolean readGranted = context.checkSelfPermission(Manifest.permission.READ_CALENDAR) == PackageManager.PERMISSION_GRANTED;
            return writeGranted && readGranted;
        }
        return true;
    }

}
