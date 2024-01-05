package com.app.dz.quranapp.MainFragmentsParte.HomeFragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.dz.quranapp.Entities.Aya;
import com.app.dz.quranapp.Entities.Juz;
import com.app.dz.quranapp.Entities.Sura;
import com.app.dz.quranapp.MushafParte.QuranActivity;
import com.app.dz.quranapp.MushafParte.ReadingPosition;
import com.app.dz.quranapp.R;
import com.app.dz.quranapp.Util.SharedPreferenceManager;
import com.app.dz.quranapp.databinding.FragmentQuranListBinding;
import com.app.dz.quranapp.quran.QuranSearchParte.ActivitySearchQuran;
import com.app.dz.quranapp.room.Daos.AyaDao;
import com.app.dz.quranapp.room.MushafDatabase;

import java.util.List;


public class QuranFragment extends Fragment {

    public final static String QURAN_TAG = "quran_tag";
    public final static String TAG = "FragmentQuranList";

    private FragmentQuranListBinding binding;
    private SuraViewModel viewModel;
    private int lastPage = 1;
    private int position = 0;
    private SuraAdapter adapter;
    private Integer pageCount = 38;
    private AyaDao dao;
    private boolean isGettingData = false;

    private JuzaAdapter adapterJuza;


    public QuranFragment() {
        // Required empty public constructor
    }


    public static QuranFragment newInstance() {
        QuranFragment fragment = new QuranFragment();
        Log.e("lifecycle", "create new instance FragmentPlayLists");
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentQuranListBinding.inflate(getLayoutInflater(), container, false);
        return binding.getRoot();
    }


    @SuppressLint("SetTextI18n")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(SuraViewModel.class);
        MushafDatabase database = MushafDatabase.getInstance(getActivity());
        dao = database.getAyaDao();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getActivity().getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            getActivity().getWindow().setStatusBarColor(getActivity().getColor(R.color.blan));
        }

        initializeJuzAdapter();
        getSuraList();

        setObservers();
        getJuzList();
        setListeners();

    }

    private void setListeners() {
        binding.included.imgSearch.setOnClickListener(view1 -> startActivity(new Intent(getActivity(), ActivitySearchQuran.class)));
        binding.included.tvTitle.setText("القران الكريم");
        binding.tvMove.setOnClickListener(view1 -> OpenMushaf(lastPage));
        binding.tvSura.setOnClickListener(V -> ChangePosition(1));
        binding.tvJuza.setOnClickListener(V -> ChangePosition(0));
    }

    private void ChangePosition(int i) {
//        if (i == position) return;
        if (i == 1) {
            Log.e(QURAN_TAG, "show sura ");
            position = 0;
            binding.recyclerview.setVisibility(View.VISIBLE);
            binding.recyclerviewJuz.setVisibility(View.GONE);


            binding.tvSura.setTextColor(getResources().getColor(R.color.white));
            binding.tvSura.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.round_left, null));


            binding.tvJuza.setBackgroundColor(getResources().getColor(R.color.white));
            binding.tvJuza.setTextColor(getResources().getColor(R.color.tv_gri_color));

        } else {
            position = 1;
            binding.recyclerview.setVisibility(View.GONE);
            binding.recyclerviewJuz.setVisibility(View.VISIBLE);

            Log.e(QURAN_TAG, "show juza visibility " + binding.recyclerviewJuz.getVisibility());

            binding.tvJuza.setTextColor(getResources().getColor(R.color.white));
            binding.tvJuza.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.round_right, null));


            binding.tvSura.setBackgroundColor(getResources().getColor(R.color.white));
            binding.tvSura.setTextColor(getResources().getColor(R.color.tv_gri_color));

        }
    }

    public interface OnListenerInterface {
        void onitemclick(int position);
    }


    @SuppressLint("SetTextI18n")
    @Override
    public void onResume() {
        super.onResume();
        Log.e(QURAN_TAG, "quran onResume");
        SharedPreferenceManager sharedPreferenceManager = SharedPreferenceManager.getInstance(getActivity());
        if (sharedPreferenceManager.iSThereAyaSaved()) {
            //we saved aya
            binding.tvLastAyaTitle.setVisibility(View.VISIBLE);
            ReadingPosition readingPosition = sharedPreferenceManager.getReadinPosition();
            lastPage = readingPosition.page;
            binding.tvLastAya.setText("" + readingPosition.ayaText);
            /*int progress = (int) quranSuraNames.getReadPercentage(readingPosition.sura - 1, readingPosition.aya - 1);
            binding.progressBar.setProgress(progress);
            binding.tvProgress.setText("" + progress + " % ");
            */
        } else {
            //default case
            binding.tvLastAyaTitle.setVisibility(View.GONE);
            binding.tvLastAya.setText("ستظهر هنا اخر اية قمت بحفظها");
        }
    }

    private void moveToAyatFragment(Sura sura) {

        new Thread(() -> {
            Aya aya = dao.getFirstAyaInSura(sura.getId());
            OpenMushaf(aya.getPage());
        }).start();


    }

    private void OpenMushaf(int startPage) {
        Intent intent = new Intent(getActivity(), QuranActivity.class);
        intent.putExtra("page", startPage);
        startActivity(intent);
    }

    public void getSuraList() {
        new Thread(() -> viewModel.setAllSuraList()).start();
    }

    private void setObservers() {
        viewModel.getAllSura().observe(getViewLifecycleOwner(), suraList -> {
            if (suraList != null && suraList.size() > 0) displayData(suraList);
        });

        viewModel.getAllJuza().observe(getViewLifecycleOwner(), juzList -> {

            if (juzList != null)
                Log.e(QURAN_TAG, "we recive observer size " + juzList.size());
            else
                Log.e(QURAN_TAG, "juz list is null ");

            if (juzList != null && juzList.size() > 0) displayDataJuza(juzList);
        });
    }

    public void getJuzList() {
        viewModel.setJuzaList();
    }

    private void displayDataJuza(List<Juz> items) {
        Log.e(QURAN_TAG, "juz list " + items.size());
        if (adapterJuza.getItemCount() > 0) return;
        adapterJuza.setItems(items);
    }

    public void initializeJuzAdapter() {
        adapterJuza = new JuzaAdapter(getActivity(), this::moveToMushaf);
        binding.recyclerviewJuz.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false));
        binding.recyclerviewJuz.setHasFixedSize(true);
        binding.recyclerviewJuz.setAdapter(adapterJuza);
    }

    private void displayData(List<Sura> items) {
        if (adapter!=null)
        if (adapter.getItemCount() == 114) return;
        adapter = new SuraAdapter(items,getActivity(), this::moveToAyatFragment);
        binding.recyclerview.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false));
        binding.recyclerview.setHasFixedSize(true);
        binding.recyclerview.setAdapter(adapter);
    }

    private void moveToMushaf(Juz juz) {
        new Thread(() -> {
            Aya aya = dao.getJuzaStartAya(juz.getSura(), juz.getSuraAya());
            OpenMushaf(aya.getPage());
        }).start();


    }

}



