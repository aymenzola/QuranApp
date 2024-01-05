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

import java.util.ArrayList;
import java.util.List;


public class QuranFragmentWithPagination extends Fragment {

    public final static String QURAN_TAG = "quran_tag";
    public final static String TAG = "FragmentQuranList";

    private OnListenerInterface listener;
    private FragmentQuranListBinding binding;
    private SuraViewModel viewModel;
    private int lastPage = 1;
    private boolean islastData = false;
    private int position = 0;
    private Integer lastid = 0;
    private SuraAdapter adapter;
    private Integer pageCount = 38;
    private AyaDao dao;
    private boolean isGettingData = false;

    private JuzaAdapter adapterJuza;


    public QuranFragmentWithPagination() {
        // Required empty public constructor
    }


    public static QuranFragmentWithPagination newInstance() {
        QuranFragmentWithPagination fragment = new QuranFragmentWithPagination();
        Log.e("lifecycle", "create new instance FragmentPlayLists");
        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnListenerInterface) {
            listener = (OnListenerInterface) context;
        } else {
            Log.e("log", "activity dont implimaents Onclicklistnersenttoactivity");
        }

    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
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

        initializeArticlesAdapter();
        if (lastid == 0)
            getList(lastid,pageCount);

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

    public void getList(int startId, int pageSize) {
        Log.e(TAG, "we call get Suralist start id " + startId);
        isGettingData = true;
        new Thread(() -> viewModel.setSuraList(startId,pageSize)).start();
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

    public void initializeArticlesAdapter() {


        adapterJuza = new JuzaAdapter(getActivity(), this::moveToMushaf);
        binding.recyclerviewJuz.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false));
        binding.recyclerviewJuz.setHasFixedSize(true);
        binding.recyclerviewJuz.setAdapter(adapterJuza);

        adapter = new SuraAdapter(new ArrayList<>(),getActivity(), this::moveToAyatFragment);
        binding.recyclerview.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false));
        binding.recyclerview.setHasFixedSize(true);
        binding.recyclerview.setAdapter(adapter);

        binding.recyclerview.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);


                boolean canScrollDown = recyclerView.canScrollVertically(1); // 1 for checking if can scroll down, -1 for up
                if (!canScrollDown) {
                    if (!isGettingData && !islastData) {
                        isGettingData = true;
                        Log.e(TAG, "loda more data ");
                        // Load the next page of data
                        getList(lastid, pageCount);
                        return;
                    }
                    Log.e(TAG, "user cant scroll down");
                    // The user can't scroll down any further
                }

                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();

                int lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition();

                int i = adapter.getItemCount() - 3;
                if (lastVisibleItemPosition == i && !isGettingData && !islastData) {
                    isGettingData = true;
                    Log.e(TAG, "loda more data ");
                    // Load the next page of data
                    getList(lastid, pageCount);
                }
            }
        });


    }

    private void displayData(List<Sura> items) {
        if (adapter.getItemCount()>=114) return;
        if (adapter.getItemCount() == 0 && viewModel.getAll_suraList().size() > 0) {
            items = viewModel.getAll_suraList();
            Log.e(TAG, "we  use all list with size " + viewModel.getAll_suraList().size());
            lastid = items.get(items.size() - 1).getId();
            if (lastid == 114) islastData = true;
            //if (adapter.getItemCount()>0) return;
            isGettingData = false;
        } else {
            viewModel.setAll_suraList(items);
            Log.e(TAG, "we receive list from observer all sura list " + viewModel.getAll_suraList().size());
            lastid = items.get(items.size() - 1).getId();
            if (lastid == 114) islastData = true;
            //if (adapter.getItemCount()>0) return;
            isGettingData = false;
        }
    }

    private void moveToMushaf(Juz juz) {
        new Thread(() -> {
            Aya aya = dao.getJuzaStartAya(juz.getSura(), juz.getSuraAya());
            OpenMushaf(aya.getPage());
        }).start();


    }

}



