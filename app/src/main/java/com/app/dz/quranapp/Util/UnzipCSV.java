package com.app.dz.quranapp.Util;

import android.content.Context;
import android.util.Log;

import java.io.*;
import java.util.zip.*;

public class UnzipCSV {
    /**
     * Size of the buffer to read/write data
     */
    private static final int BUFFER_SIZE = 4096;

    /**
     * Extracts a zip file specified by the zipFilePath to a directory specified by
     * destDirectory (will be created if does not exists)
     * @param context - the Context of the app
     * @param zipFileName - the name of the zip file in the assets folder
     * @param destDirectory - the destination directory where the zip will be extracted
     * @throws IOException
     */

    public void unzipCSV(Context context, String zipFileName, String destDirectory) throws IOException {
        Log.e("zip", "zip start");
        InputStream is = context.getAssets().open(zipFileName);
        ZipInputStream zipIn = new ZipInputStream(is);
        File destDir = new File(destDirectory);
        if (!destDir.exists()) {
            destDir.mkdir();
        }
        ZipEntry entry = zipIn.getNextEntry();
        // iterates over entries in the zip file
        while (entry != null) {
            Log.e("zip", "looping ");
            String filePath = destDirectory + File.separator + entry.getName();
            if (!entry.isDirectory()) {
                Log.e("zip", "the entry is a file");
                // if the entry is a file, extracts it
                extractFile(zipIn, filePath);
            } else {
                Log.e("zip", "the entry is a directory");
                // if the entry is a directory, make the directory
                File dir = new File(filePath);
                dir.mkdir();
            }
            zipIn.closeEntry();
            entry = zipIn.getNextEntry();
        }
        zipIn.close();
    }

    /**
     * Extracts a zip entry (file entry)
     * @param zipIn
     * @param filePath
     * @throws IOException
     */
    private void extractFile(ZipInputStream zipIn, String filePath) throws IOException {
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(filePath));
        byte[] bytesIn = new byte[BUFFER_SIZE];
        int read = 0;
        while ((read = zipIn.read(bytesIn)) != -1) {
            Log.e("zip", "working ");
            bos.write(bytesIn, 0, read);
        }
        bos.close();
        Log.e("zip", "zip finished");
    }
}
