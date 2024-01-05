package com.app.dz.quranapp.Util;

import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ZipManager {
    private static int BUFFER_SIZE = 6 * 1024;
 
public static void unzip(String zipFile, String location) throws IOException {
        try {
            Log.e("zip", "zip file : "+zipFile);
            Log.e("zip", "location : "+location);

            File f = new File(location);
            if (!f.isDirectory()) {
                f.mkdirs();
            }
            ZipInputStream zin = new ZipInputStream(new FileInputStream(zipFile));
            try {
                Log.e("zip", "we are here ");
                ZipEntry ze = null;
                while ((ze = zin.getNextEntry()) != null) {
                    String path = location + File.separator + ze.getName();
                    Log.e("zip", "write path "+path);
                    if (ze.isDirectory()) {
                        File unzipFile = new File(path);
                        if (!unzipFile.isDirectory()) {
                            Log.e("zip", "write 45");
                            unzipFile.mkdirs();
                        }
                    } else {
                        FileOutputStream fout = new FileOutputStream(path, false);
                        Log.e("zip", "write 0.0");
                        try {
                            for (int c = zin.read(); c != -1; c = zin.read()) {
                                Log.e("zip", "write 0");
                                fout.write(c);
                            }
                            zin.closeEntry();
                        } finally {
                            Log.e("zip", "zip close 1 ");
                            fout.close();
                        }
                    }
                }
                Log.e("zip", "under while  ");
                zin.close();
            } catch (Exception e){
                zin.close();
                Log.e("zip", "zip close "+e.getMessage());
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("zip", "Unzip exception", e);
        }
    }
}