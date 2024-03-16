package com.app.dz.quranapp.quran.hafs_parte;

import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.app.dz.quranapp.databinding.BottomSheetLayoutBinding;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import org.jetbrains.annotations.NotNull;

public class FilterButtomSheetclass extends BottomSheetDialogFragment {

    Bottomsheetlistener listener;
    Context context;
    private SharedPreferences prefs;
    private int PageType;
    private int FirstType;

    public FilterButtomSheetclass(Bottomsheetlistener bottomsheetlistener,Context context) {
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

            int textSize = binding.seekBar.getProgress() + 16;

            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt("textSize", textSize);
            editor.apply();


            if (listener != null) listener.onTextSizeChanged(textSize);

            dismiss();
        });

        return binding.getRoot();
    }



    public interface Bottomsheetlistener {
        void onTextSizeChanged(int textsize);
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


}
