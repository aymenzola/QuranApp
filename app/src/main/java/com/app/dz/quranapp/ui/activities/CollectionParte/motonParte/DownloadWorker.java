package com.app.dz.quranapp.ui.activities.CollectionParte.motonParte;

import static com.app.dz.quranapp.Communs.Statics.BROADCAST_DOWNLOAD_ACTION.DOWNLOAD_ERROR_ACTION;
import static com.app.dz.quranapp.Communs.Statics.BROADCAST_DOWNLOAD_ACTION.PROGRESS_ACTION;
import static com.app.dz.quranapp.Services.QuranServices.ForegroundDownloadAudioService.AppfolderName;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.app.dz.quranapp.Communs.Statics;
import com.app.dz.quranapp.Services.DownloadListeners;
import com.app.dz.quranapp.Services.DownloadTask;
import com.app.dz.quranapp.Services.QuranServices.ForegroundDownloadAudioService;
import com.app.dz.quranapp.Util.PublicMethods;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class DownloadWorker extends Worker {

    private static final String TAG = "DownloadWorker";

    public DownloadWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        String fileUrl = getInputData().getString("fileUrl");
        String fileName = getInputData().getString("fileName");

        if (fileUrl == null) return Result.failure();

        try {
            final CountDownLatch latch = new CountDownLatch(1);
            final AtomicReference<Result> result = new AtomicReference<>(Result.failure());

            downloadFile(fileUrl, fileName, new DownloadListeners() {
                private int lastProgress = -1;
                @Override
                public void onProgressUpdate(int downloadedSize, int totalSize) {
                    if (downloadedSize == 0) return;
                    int progress = Math.round(((float) downloadedSize / totalSize) * 100);
                    if (progress != lastProgress) {
                        lastProgress = progress;
                        // Report progress.
                        sendProgress(progress);
                    }
                }

                @Override
                public void onDownloadComplete(File outputFile) {
                    sendProgress(100);
                    result.set(Result.success());
                    latch.countDown();
                }

                @Override
                public void onDownloadCacled(String reason) {
                    Log.e(TAG, "download canceled " + reason);
                    result.set(Result.failure());
                    latch.countDown();
                }
            });

            latch.await();  // Wait here until the latch is counted down in onPostExecute()

            return result.get();
        } catch (Exception e) {
            return Result.failure();
        }
    }

    /*
    @NonNull
    @Override
    public Result doWork() {
        String fileUrl = getInputData().getString("fileUrl");
        String fileName = getInputData().getString("fileName");

        try {
            if (fileUrl==null) return Result.failure();

            /*
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder().url(fileUrl).build();
            Response response = client.newCall(request).execute();

            if (response.isSuccessful()) {
                InputStream inputStream = response.body().byteStream();
                OutputStream outputStream = new FileOutputStream(getApplicationContext().getFilesDir() + "/" + fileName);
                BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);

                byte[] data = new byte[1024];
                long total = 0;
                int count;
                while ((count = bufferedInputStream.read(data)) != -1) {
                    total += count;
                    outputStream.write(data, 0, count);

                    // Calculate progress as a percentage.
                    int progress = (int) ((total * 100) / response.body().contentLength());

                    // Report progress.
                    sendProgress(progress);
                }

                outputStream.flush();
                outputStream.close();
                bufferedInputStream.close();

                return Result.success();
            } else {
                return Result.failure();
            }*/

    /*        downloadFile(fileUrl,fileName);


        } catch (Exception e) {
            return Result.failure();
        }
    }
*/


    public void downloadFile(String url,String fileName,DownloadListeners listener) {
        Log.d(TAG, "the download file name is "+fileName);

        // Permission is granted, create the folder
        File folderFile2 = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),AppfolderName);
        if (!folderFile2.exists()) {
            if (folderFile2.mkdirs()) {
                Log.d(TAG, "Folder created successfully");
            } else {
                Log.e(TAG, "Failed to create folder");
            }
        } else {
            Log.d(TAG, "Folder already exists");
        }

        PublicMethods publicMethods = PublicMethods.getInstance();
        File file = publicMethods.getFile(fileName);
        DownloadTask downloadTask = new DownloadTask(url,file, new DownloadListeners() {
            private int lastProgress = -1;

            @Override
            public void onProgressUpdate(int downloadedSize, int totalSize) {
                listener.onProgressUpdate(downloadedSize, totalSize);
            }

            @Override
            public void onDownloadComplete(File outputFile) {
              listener.onDownloadComplete(outputFile);
            }

            @Override
            public void onDownloadCacled(String reason) {
                listener.onDownloadCacled(reason);
                /*if (isCanceled) return;
                toldTheActivty(DOWNLOAD_ERROR_ACTION, 0);
                stopDownload();
                */
            }
        });
        downloadTask.execute();
    }

    private void sendProgress(int progress) {
        Data progressData = new Data.Builder()
                .putInt("progress", progress)
                .build();
        setProgressAsync(progressData);
    }

}