package com.app.dz.quranapp.Util;


import static com.app.dz.quranapp.Services.QuranServices.ForegroundDownloadAudioService.AppfolderName;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.app.dz.quranapp.R;
import com.app.dz.quranapp.data.room.Entities.Riwaya;
import com.app.dz.quranapp.quran.models.ReaderAudio;
import com.app.dz.quranapp.quran.models.RiwayaType;
import com.app.dz.quranapp.ui.activities.CollectionParte.motonParte.Matn;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.shockwave.pdfium.PdfDocument;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pub.devrel.easypermissions.EasyPermissions;

public class PublicMethods {
    private static PublicMethods instance;
    DecimalFormat decimalFormat = new DecimalFormat("000");
    private Map<String, List<Integer>> readerAvialableSuraNumbers;


    private PublicMethods() {
        // Private constructor to prevent external instantiation
        // Private constructor to prevent external instantiation
        readerAvialableSuraNumbers = new HashMap<>();
        readerAvialableSuraNumbers.put("abdAzizSahim", Arrays.asList(1, 18, 31, 50, 56, 57, 67, 72, 75, 112));
        readerAvialableSuraNumbers.put("rasheed_ifrad_warch", Arrays.asList(1, 18, 25, 26, 27, 28, 29, 31, 33, 35, 37, 38, 41, 42, 71));

    }

    public static synchronized PublicMethods getInstance() {
        if (instance == null) {
            instance = new PublicMethods();
        }
        return instance;
    }

    public ReaderAudio getReaderFromList(int readerId, List<ReaderAudio> readerAudioList) {
        for (ReaderAudio readerAudio : readerAudioList) {
            if (readerAudio.getId() == readerId) return readerAudio;
        }
        return new ReaderAudio();
    }


    public static List<ReaderAudio> getReadersAudiosListList(Context context) {
        List<ReaderAudio> list = CsvReader.readReaderAudioListFromCsv(context, "audio.csv");
        List<ReaderAudio> filterdList = new ArrayList<>();
        for (ReaderAudio readerAudio : list)
            if (readerAudio.getAudioType() != 0) filterdList.add(readerAudio);
        return filterdList;
    }

    public static List<ReaderAudio> getReadersAudiosListWithRiwaya(Context context, String riwaya) {
        List<ReaderAudio> list = CsvReader.readReaderAudioListFromCsv(context, "audio.csv");

        List<ReaderAudio> filterdList = new ArrayList<>();

        if (riwaya.contains(RiwayaType.WARCH.name())) {
            for (ReaderAudio readerAudio : list)
                if (readerAudio.getAudioType() != 0 && readerAudio.getRiwaya().contains(RiwayaType.WARCH.name()))
                    filterdList.add(readerAudio);
        } else {
            //default readers
            for (ReaderAudio readerAudio : list)
                if (readerAudio.getAudioType() != 0 && readerAudio.getRiwaya().contains(RiwayaType.HAFS.name()))
                    filterdList.add(readerAudio);
        }
        return filterdList;
    }

    public static ReaderAudio getReaderAudioWithId(int readerId, Context context) {
        List<ReaderAudio> list = CsvReader.readReaderAudioListFromCsv(context, "audio.csv");
        for (ReaderAudio readerAudio : list)
            if (readerAudio.getId() == readerId) return readerAudio;
        return new ReaderAudio();
    }

    public static List<ReaderAudio> getReadersAudiosListList(Context context, Riwaya riwaya) {
        List<ReaderAudio> list = CsvReader.readReaderAudioListFromCsv(context, "audio.csv");
        List<ReaderAudio> filterdList = new ArrayList<>();
        for (ReaderAudio readerAudio : list)
            if (readerAudio.getAudioType() != 0) filterdList.add(readerAudio);
        return filterdList;
    }

    public String getSreamLink(ReaderAudio readerAudio, int suraIndex) {
        /** check type
         // if 3 --> the link will be :  link/1.mp3
         // else 1 --> the link will be : link/001.mp3
         // else 0 --> the link will be with aya number : link/001001.mp3 **/

        if (readerAudio.getAudioType() == 3) {
            return readerAudio.getUrl() + suraIndex + ".mp3";
        } else {
            String suraFormatNumber = decimalFormat.format(suraIndex);
            return readerAudio.getUrl() + suraFormatNumber + ".mp3";
        }
    }

    public String getCorrectUrlOrPath(int readerId, int suraIndex, boolean isFromLocal, Context context) {
        String suraFormatNumber = decimalFormat.format(suraIndex);

        //get the selected reader
        List<ReaderAudio> list = CsvReader.readReaderAudioListFromCsv(context, "audio.csv");
        ReaderAudio selectedReader = new ReaderAudio();
        for (ReaderAudio readerAudio : list)
            if (readerAudio.getId() == readerId) {
                selectedReader = readerAudio;
                break;
            }


        if (isFromLocal)
            return getFile(context, selectedReader.getReaderTag(), suraIndex).getPath();
        else {
            if (selectedReader.getAudioType() == 3) {
                return selectedReader.getUrl() + suraIndex + ".mp3";
            } else {
                return selectedReader.getUrl() + suraFormatNumber + ".mp3";
            }
        }
    }

    public String getCorrectUrlOrPath(ReaderAudio selectedReader, int suraIndex, boolean isFromLocal, Context context) {

        if (isFromLocal)
            return getFile(context, selectedReader.getReaderTag(), suraIndex).getPath();
        else {
            if (selectedReader.getAudioType() == 3) {
                return selectedReader.getUrl() + suraIndex + ".mp3";
            } else {
                return selectedReader.getUrl() + decimalFormat.format(suraIndex) + ".mp3";
            }
        }
    }

    public File getFile(Context context, String readerTag, int suraIndex) {
        createAppFolderIfNotExist(context);
        String filename = readerTag + "_" + suraIndex + ".mp3";
        /*if (Build.VERSION_CODES.P >= Build.VERSION.SDK_INT) {
            return new File(context.getFilesDir(), AppfolderName + File.separator+ readerTag +File.separator+ filename);
        } else {
            return new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath() + "/" + AppfolderName + "/" + readerTag + "/" + filename);
        }*/
        return new File(context.getFilesDir(), AppfolderName + File.separator+ readerTag +File.separator+ filename);
    }

    public File getFile(String filename, Context context) {
        createAppFolderIfNotExist(context);
     /*   if (Build.VERSION_CODES.P >= Build.VERSION.SDK_INT) {
            return new File(context.getFilesDir(), AppfolderName + File.separator + filename);
        } else {
            return new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath() + "/" + AppfolderName + "/" + filename);
        }
    */
        return new File(context.getFilesDir(), AppfolderName + File.separator + filename);
    }


    public boolean checkIfThisExist(String filename,Context context) {
        return getFile(filename,context).exists();
    }


    public String getUserFriendlyErrorMessage(String errorMessage) {
        if (errorMessage.contains("MalformedURLException")) {
            return "تنسيق URL للملف غير صحيح.";
        } else if (errorMessage.contains("IOException")) {
            return "حدثت مشكلة أثناء القراءة أو الكتابة في الملف.";
        } else if (errorMessage.contains("ProtocolException")) {
            return "حدث خطأ في بروتوكول HTTP أثناء تنزيل الملف.";
        } else {
            return "حدث خطأ أثناء تنزيل الملف. الرجاء المحاولة مرة أخرى.";
        }
    }

    /*public File getFile(Context context, String readerTag, int suraIndex) {
        String filename = readerTag + "_" + suraIndex + ".mp3";
        File downloadsDir = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
        if (downloadsDir != null) {
            return new File(downloadsDir.getPath() + "/" + AppfolderName + "/" + readerTag + "/" + filename);
        } else {
            return null;
        }
    }*/

    /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
              return new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath() + "/" + AppfolderName + "/" + readerTag + "/" + filename);
          } else {
              return new File(Environment.getExternalStorageDirectory().getPath() + "/" + AppfolderName + "/" + filename);
          }*/


    public String getLocalFileName(String readerTag, int suraIndex) {
        return readerTag + "_" + suraIndex + ".mp3";
    }

    public boolean checkNetworkConnection(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            return activeNetworkInfo != null && activeNetworkInfo.isConnected();
        }
        return false;
    }


    @SuppressLint("SetTextI18n")
    public void showNoInternetDialog(Context context, String message) {
        MaterialAlertDialogBuilder dialogBuilder = new MaterialAlertDialogBuilder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View layoutView = inflater.inflate(R.layout.dialog_internet, null);

        TextView btn_ok = layoutView.findViewById(R.id.btn_ok);
        TextView tv_message = layoutView.findViewById(R.id.tv_message);

        dialogBuilder.setView(layoutView);
        AlertDialog dialog = dialogBuilder.create();

        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

        dialog.setCancelable(false);
        dialog.show();

        tv_message.setText("" + message);

        btn_ok.setOnClickListener(v -> dialog.dismiss());

    }

    public static boolean isDeviceInSilentMode(Context context) {
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        if (audioManager != null) {
            return audioManager.getRingerMode() == AudioManager.RINGER_MODE_SILENT;
        }
        return false;
    }

    public static boolean isInternetAvailable(Context pContext) {
        if (pContext == null) {
            return false;
        }
        ConnectivityManager cm =
                (ConnectivityManager) pContext.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        return activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
    }

    public List<Matn> checkBooksExistence(List<Matn> books, Context context) {
        PublicMethods publicMethods = PublicMethods.getInstance();
        for (Matn book : books) {
            File file = publicMethods.getFile(book.fileName,context);
            if (file.exists()) {
                book.isDownloaded = true;
            }
        }
        return books;
    }

    public PdfDocument.Bookmark findBookmarkByPage(List<PdfDocument.Bookmark> bookmarks, int pageNumber) {
        for (PdfDocument.Bookmark bookmark : bookmarks) {
            if (bookmark.getPageIdx() == pageNumber) {
                return bookmark;
            }

            if (bookmark.hasChildren()) {
                PdfDocument.Bookmark found = findBookmarkByPage(bookmark.getChildren(), pageNumber);
                if (found != null) {
                    return found;
                }
            }
        }
        return null;
    }

    public boolean isAvailableFile(int suraNumber, String readerTag) {
        List<Integer> availableSuraNumbers = readerAvialableSuraNumbers.get(readerTag);
        if (availableSuraNumbers != null) {
            return availableSuraNumbers.contains(suraNumber);
        } else {
            return true;
        }
    }

    public int getForignSuraPage(String riwayaTag, int id) {
        if (riwayaTag.equals(RiwayaType.ENGLISH_QURAN.name())) {
            //return page english pdf
            return 40;
        } else {
            //return page french pdf
            return 50;
        }
    }


    public String getCollectionArabicName(String collectionName) {
        return switch (collectionName) {
            case "bukhari" -> "صحيح البخاري";
            case "muslim" -> "صحيح مسلم";
            case "nasai" -> "سنن النسائي";
            case "ibnmajah" -> "سنن ابن ماجة";
            case "hisn" -> "حصن المسلم";
            default -> "سنن أبي داود";
        };
    }

    public String getErrorMessage(int what) {
        return switch (what) {
            case MediaPlayer.MEDIA_ERROR_UNKNOWN -> "خطأ غير معروف في تشغيل الوسائط";
            case MediaPlayer.MEDIA_ERROR_SERVER_DIED -> "توقف خادم الوسائط";
            case MediaPlayer.MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK ->
                    "الوسائط غير صالحة للتشغيل التدريجي";
            case MediaPlayer.MEDIA_ERROR_IO -> "خطأ IO في الوسائط";
            case MediaPlayer.MEDIA_ERROR_MALFORMED -> "وسائط غير صحيحة البنية";
            case MediaPlayer.MEDIA_ERROR_UNSUPPORTED -> "وسائط غير مدعومة";
            case MediaPlayer.MEDIA_ERROR_TIMED_OUT -> "انتهت مدة الوسائط";
            default -> "هذا القارئ يمكن تشغيله في هذه السورة";
        };
    }

    public void createAppFolderIfNotExist(Context context) {
    /*
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            File folder = new File(context.getFilesDir(), AppfolderName);
            if (!folder.exists()) {
                boolean result = folder.mkdirs();
                Log.e("checkStorageTag", result ? "Folder created" : "Folder not created");
            } else {
                Log.e("checkStorageTag", "Folder already exists");
            }

        } else {

            File folderFile2 = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), AppfolderName);
            if (!folderFile2.exists()) {
                if (folderFile2.mkdirs()) {
                    Log.e("checkStorageTag", "Folder created successfully");
                } else {
                    Log.e("checkStorageTag", "Failed to create folder");
                }
            } else {
                Log.d("checkStorageTag", "Folder already exists");
            }
        }
    */
        File folder = new File(context.getFilesDir(), AppfolderName);
        if (!folder.exists()) {
            boolean result = folder.mkdirs();
            Log.e("checkStorageTag", result ? "Folder created" : "Folder not created");
        } else {
            Log.e("checkStorageTag", "Folder already exists");
        }
    }

    public boolean haveStoragePermissions(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
            //we have permission
            return EasyPermissions.hasPermissions(context,Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        return true;
    }

}