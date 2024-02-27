package com.app.dz.quranapp.fix_new_futers.ai_commands;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class PermissionHelper {

    public static final int PERMISSION_PHONE_REQUEST_CODE = 100;

    public static boolean checkAndRequestPermissions(Activity activity, String... permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            for (String permission : permissions) {
                if (ContextCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(activity, permissions, PERMISSION_PHONE_REQUEST_CODE);
                    return false;
                }
            }
        }
        return true;
    }
}
