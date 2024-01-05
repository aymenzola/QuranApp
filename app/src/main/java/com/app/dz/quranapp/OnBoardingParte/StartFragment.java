package com.app.dz.quranapp.OnBoardingParte;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.app.dz.quranapp.R;
import com.app.dz.quranapp.databinding.OnboardingFragmentBinding;


public class StartFragment extends Fragment {

    private static final String ARG_POSTION = "position";

    public int[] images = {
            R.drawable.ic_open_box,
            R.drawable.ic_box,
            R.drawable.ic_key
    };

    public String[] titles = {
            "مفتاحك... لكنوز... من الحسنات",
            "مفتاحك... لكنوز...",
            "مفتاحك..."
    };


    public String[] descs = {"لنتعرض و إياكم لرحمات الرحمـٰن، فيدخلنا و إياكم الجنان.",
            "زاخر بالخير و القرآن، كتب، أذكار، مواقيت الصلاة ...",
            "تطبيق سهل و ميسر"
    };


    private int position;
    private OnboardingFragmentBinding binding;

    public StartFragment() {
        // Required empty public constructor
    }

    public static StartFragment newInstance(int position) {
        StartFragment fragment = new StartFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_POSTION, position);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            position = getArguments().getInt(ARG_POSTION);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = OnboardingFragmentBinding.inflate(getLayoutInflater(), container, false);
        binding.tvText.setText(descs[position]);
        binding.tvTitle.setText(titles[position]);

        return binding.getRoot();
    }


    @SuppressLint("SetTextI18n")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

         }


}

