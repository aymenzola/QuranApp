package com.app.dz.quranapp;

import static android.content.Context.MODE_PRIVATE;

import static com.app.dz.quranapp.MushafParte.QuranActivity.QURAN_HAFS_TYPE;
import static com.app.dz.quranapp.MushafParte.QuranActivity.QURAN_WARSH_TYPE;
import static com.app.dz.quranapp.MushafParte.QuranActivity.TAFSIR_TYPE;
import static com.app.dz.quranapp.MushafParte.warsh_parte.QuranPageFragmentMultipleRiwayat.QuranWarchFolderName;
import static com.app.dz.quranapp.Services.ForegroundDownloadAudioService.AppfolderName;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.app.dz.quranapp.Entities.Riwaya;
import com.app.dz.quranapp.databinding.BottomSheetLayoutBinding;
import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.text.DecimalFormat;

public class FilterButtomSheetclass extends BottomSheetDialogFragment {

    Bottomsheetlistener listener;
    Context context;
    private SharedPreferences prefs;
    private int PageType;
    private int FirstType;

    public FilterButtomSheetclass(Bottomsheetlistener bottomsheetlistener, Context context) {
        this.listener = bottomsheetlistener;
        this.context = context;
    }


    @SuppressLint("SetTextI18n")
    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        BottomSheetLayoutBinding binding = BottomSheetLayoutBinding.inflate(inflater, container, false);


        prefs = context.getSharedPreferences("MyPrefs", MODE_PRIVATE);
        int defaultSize = prefs.getInt("textSize", 25);
        PageType = prefs.getInt("page_type", QURAN_HAFS_TYPE);
        FirstType = PageType;
        if (PageType == TAFSIR_TYPE)
            binding.radioTafsir.setChecked(true);
        if (PageType == QURAN_HAFS_TYPE)
            binding.radioQuran.setChecked(true);
        if (PageType == QURAN_WARSH_TYPE)
            binding.radioQuranWarsh.setChecked(true);


        boolean isAlreadyDownloaded = getImageDesitination(1).exists();
        if (isAlreadyDownloaded) {
            Glide.with(context).load(R.drawable.ic_baseline_check_24).into(binding.imageWarsh);
        } else {
            Glide.with(context).load(R.drawable.ic_baseline_arrow_circle_down_24).into(binding.imageWarsh);
        }

        binding.seekBar.setProgress(defaultSize - 15);
        binding.tvSizeDisplay.setTextSize(TypedValue.COMPLEX_UNIT_SP, defaultSize);
        binding.tvSizeValue.setText("" + defaultSize);
        binding.seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int textSize = progress + 15;
                binding.tvSizeDisplay.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
                binding.tvSizeValue.setText("" + textSize);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }

            // Implement onStartTrackingTouch and onStopTrackingTouch as in the previous example
            // ...

        });

        binding.tvSave.setOnClickListener(v -> {
            if (PageType == QURAN_WARSH_TYPE && !isAlreadyDownloaded) {
                Toast.makeText(context, "يجب تحميل هذه الرواية أولا", Toast.LENGTH_SHORT).show();
                return;
            }
            int textSize = binding.seekBar.getProgress() + 16;

            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt("textSize", textSize);
            editor.apply();

            if (FirstType != PageType) {
                editor.putInt("page_type", PageType);
                editor.apply();

                //todo listener.onTypeChanged(PageType);
                dismiss();
                return;
            }

            if (listener != null) listener.onTextSizeChanged(textSize);

            dismiss();
        });

        binding.radioQuran.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                PageType = QURAN_HAFS_TYPE;
                binding.radioQuranWarsh.setChecked(false);
                binding.radioTafsir.setChecked(false);
            }
        });
        binding.radioTafsir.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                PageType = TAFSIR_TYPE;
                binding.radioQuranWarsh.setChecked(false);
                binding.radioQuran.setChecked(false);
            }
        });
        binding.radioQuranWarsh.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                PageType = QURAN_WARSH_TYPE;
                binding.radioQuran.setChecked(false);
                binding.radioTafsir.setChecked(false);
            }
        });

        binding.imageWarsh.setOnClickListener(v -> {
            if (isAlreadyDownloaded) return;
            listener.onDownloadWarsh(QURAN_WARSH_TYPE);
            dismiss();
        });

        return binding.getRoot();
    }

    @Override
    public void onAttach(@NonNull @NotNull Context context) {
        super.onAttach(context);

        try {
            listener = (Bottomsheetlistener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + "must impliment Bottomsheetlistener");
        }

    }

    public interface Bottomsheetlistener {
        void onTextSizeChanged(int textsize);

        void onTypeChanged(Riwaya riwaya);

        void onDownloadWarsh(int type);
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //    setStyle(DialogFragment.STYLE_NO_TITLE, R.style.AppBottomSheetDialogTheme);
    }

    @Override
    public void onStart() {
        super.onStart();
        /*if (getDialog() != null) {
            getDialog().getWindow().setBackgroundDrawableResource(R.drawable.rounded_bottom_sheet);
        }*/
    }

    public File getImageDesitination(int pageNumber) {
        DecimalFormat decimalFormat = new DecimalFormat("000");
        String pageN = decimalFormat.format(pageNumber);
        return new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath() + "/" + AppfolderName + "/" + QuranWarchFolderName + "/p " + pageN + ".cst");
    }

}
