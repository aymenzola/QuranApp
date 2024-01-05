package com.app.dz.quranapp.Services;

import java.io.File;

public interface DownloadListeners {
    void onProgressUpdate(int downloadedSize, int totalSize);

    void onDownloadComplete(File outputFile);

    void onDownloadCacled(String reasen);
}
