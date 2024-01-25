package com.app.dz.quranapp.Util;


import static com.app.dz.quranapp.Services.ForegroundDownloadAudioService.AppfolderName;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.app.dz.quranapp.MushafParte.multipleRiwayatParte.ReaderAudio;
import com.app.dz.quranapp.R;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.text.DecimalFormat;
import java.util.List;

public class PublicMethods {
    private static PublicMethods instance;
    DecimalFormat decimalFormat = new DecimalFormat("000");

    private PublicMethods() {
        // Private constructor to prevent external instantiation
    }

    public static synchronized PublicMethods getInstance() {
        if (instance == null) {
            instance = new PublicMethods();
        }
        return instance;
    }

    public ReaderAudio getReaderFromList(int readerId,List<ReaderAudio> readerAudioList) {
        for (ReaderAudio readerAudio:readerAudioList) {
            if (readerAudio.getId()==readerId) return readerAudio;
        }
        return new ReaderAudio();
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

    public String getReaderTag(String readerName) {
        switch (readerName) {
            case "Alafasy":
                return "Alafasy";
            case "Shuraym":
                return "Shuraym";
            case "Sudais":
                return "Sudais";
            case "Mohammad_al_Tablaway_128kbps":
                return "MohammadTab";
            default:
                return "AbdulBaset";
        }
    }

    public boolean isReaderSelectionAvailable(String selectedRe) {
        switch (selectedRe) {
            case "Alafasy":
                return true;
            case "Shuraym":
                return true;
            case "Sudais":
                return true;
            case "Mohammad_al_Tablaway_128kbps":
                return false;
            case "AbdulBaset/Murattal":
                return true;
            default:
                return false;
        }
    }

    public File getSuraFile(String selectedReader, int suraId) {
        return getFile(getLocalFileName(selectedReader, suraId), selectedReader);
    }

    public String getCorrectUrlOrPath(String readerName, int suraIndex, boolean isFromLocal) {

        String suraFormatNumber = decimalFormat.format(suraIndex);
        if (isFromLocal)
            return getFile(getLocalFileName(readerName, suraIndex), readerName).getPath();
        else {
            switch (readerName) {
                case "Alafasy":
                    return "https://download.quranicaudio.com/qdc/mishari_al_afasy/murattal/" + suraIndex + ".mp3";
                case "Shuraym":
                    return "https://download.quranicaudio.com/qdc/saud_ash-shuraym/murattal/" + suraFormatNumber + ".mp3";
                case "Sudais":
                    return "https://download.quranicaudio.com/qdc/abdurrahmaan_as_sudais/murattal/" + suraIndex + ".mp3";
                case "Mohammad_al_Tablaway_128kbps":
                    return "https://download.quranicaudio.com/qdc/khalil_al_husary/murattal/" + suraIndex + ".mp3";
                default:
                    return "https://download.quranicaudio.com/qdc/abdul_baset/murattal/" + suraIndex + ".mp3";
            }
        }
    }


    public String getCorrectUrlAya(String readerName, int suraIndex, int ayaIndex) {
        String number = decimalFormat.format(suraIndex) + "" + decimalFormat.format(ayaIndex);
        switch (readerName) {
            case "Mohammad_al_Tablaway_128kbps":
                return "https://mirrors.quranicaudio.com/everyayah/Mohammad_al_Tablaway_128kbps/" + number + ".mp3";
            default:
                return "https://verses.quran.com/" + readerName + "/mp3/" + number + ".mp3";
        }

    }

    public File getFile(String filename, String readerName) {
        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
         */
        return new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath() + "/" + AppfolderName + "/" + getReaderTag(readerName) + "/" + filename);
        /*} else {
            return new File(Environment.getExternalStorageDirectory().getPath() + "/" + AppfolderName + "/" + filename);
        }*/
    }


    public String getLocalFileName(String readerName, int suraIndex) {
        return getReaderTag(readerName) + "_" + suraIndex + ".mp3";
    }

    public boolean checkNetworkConnection(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            if (activeNetworkInfo != null && activeNetworkInfo.isConnected()) {
                return true;
            }
        }
        return false;
    }

    public void showNoInternetDialog(Context context, String message, String message2) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context, R.style.AlertDialogRounded);
        builder.setTitle("تنبيه");
        builder.setMessage(message);
        builder.setPositiveButton("حسنا", (dialog, which) -> {
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void showNoInternetSnackbar(View view, String message) {
        Snackbar snackbar = Snackbar.make(view, message, Snackbar.LENGTH_INDEFINITE)
                .setAction("Hide", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // do nothing
                    }
                });
        snackbar.show();
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


}