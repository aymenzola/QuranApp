package com.app.dz.quranapp.Services;


import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class DownloadTask extends AsyncTask<String, Integer, Integer> {
    private boolean isPaused = false;
    private boolean isCancelled = false;
    private int downloadedSize = 0;
    private int totalSize = -1;
    private String url;
    private File outputFile;
    private DownloadListeners listener;
    private int lastProgress = -1;
    private String fileName;

    public DownloadTask(String url, File outputFile, DownloadListeners listener) {
        this.url = url;
        this.outputFile = outputFile;
        this.listener = listener;
    }

    @Override
    protected Integer doInBackground(String... params) {
        InputStream input = null;
        OutputStream output = null;
        HttpURLConnection connection = null;

        try {
            //File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),AppfolderName+"/"+fileName);

            Log.e("Download", "we are here starting download");
            URL url = new URL(this.url);
            connection = (HttpURLConnection) url.openConnection();
            connection.connect();

            // Get the total file size
            totalSize = connection.getContentLength();
            Log.e("Download", "we are here starting download totalSize : "+totalSize);
            // Create input and output streams
            input = new BufferedInputStream(url.openStream());
            output = new FileOutputStream(outputFile);

            byte data[] = new byte[1024];
            int count;

            while ((count = input.read(data)) != -1) {
                if (isCancelled) {
                    output.flush();
                    output.close();
                    input.close();
                    if (outputFile.exists()) {
                        outputFile.delete();
                    }
                    return downloadedSize;
                }
                if (!isPaused) {
                    downloadedSize += count;
                    output.write(data, 0, count);
                    publishProgress(downloadedSize);
                }
            }
            Log.e("Download", "we are here starting exit download");

            return downloadedSize;
        } catch (IOException e) {
            Log.e("Download", "download exception "+e.getMessage());
            e.printStackTrace();
            return downloadedSize;
        } finally {
            try {
                if (output != null) {
                    output.close();
                }
                if (input != null) {
                    input.close();
                }
            } catch (IOException e) {
                Log.e("Download", "2 download exception "+e.getMessage());
                e.printStackTrace();
            }
            if (connection != null) {
                connection.disconnect();
            }
        }

    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        Log.d("Download", "onProgressUpdate "+values[0]);
        if (listener != null) {
            listener.onProgressUpdate(values[0], totalSize);
        }
    }

    @Override
    protected void onPostExecute(Integer downloadedSize) {
        Log.d("Download", "onProgressUpdate "+downloadedSize);
        if (listener != null) {
            Log.e("Download", "downloadedSize "+downloadedSize+" totalsize "+totalSize);
            if (downloadedSize == totalSize) {
                listener.onDownloadComplete(outputFile);
            } else {
                listener.onDownloadCacled("reasen");
            }
        }
    }

    public void pauseDownload() {
        isPaused = true;
    }

    public void resumeDownload() {
        isPaused = false;
    }

    public void cancelDownload() {
        isCancelled = true;
    }
}

