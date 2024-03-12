package com.app.dz.quranapp.ui.activities.AdkarParte;


import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.app.dz.quranapp.R;
import com.app.dz.quranapp.databinding.FragmentAdkarDetailsBinding;
import com.app.dz.quranapp.ui.activities.MainActivityPartes.HomeFragment.AdkarViewModel;

import java.util.List;


public class AdkarDetailsFragment extends Fragment {

    public final static String TAG = "FragmentQuranList";
    private FragmentAdkarDetailsBinding binding;
    private AdkarViewModel viewModel;
    AdkarCountsHelper AdkarHelper;
    private AdkarAdapter adkarAdapter;

    public AdkarDetailsFragment() {
        // Required empty public constructor
    }


    public static AdkarDetailsFragment newInstance() {
        AdkarDetailsFragment fragment = new AdkarDetailsFragment();
        Log.e("lifecycle", "create new instance FragmentPlayLists");
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentAdkarDetailsBinding.inflate(getLayoutInflater(), container, false);
        return binding.getRoot();
    }


    @SuppressLint("SetTextI18n")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(AdkarViewModel.class);
        AdkarHelper = AdkarCountsHelper.getInstance(getActivity());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getActivity().getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            getActivity().getWindow().setStatusBarColor(getActivity().getColor(R.color.background_color));
        }

        Bundle arguments = getArguments();
        if (arguments != null) {
            AdkarModel adkarModel = (AdkarModel) arguments.getSerializable("adkarModel");
            if (adkarModel != null) {
                binding.includeCategoryAdkarCard.tvFastAdkarTitle.setText(adkarModel.getCategory());
                getAdkarByCategory(adkarModel.getCategoryId());
            }

        }

        setListeners();
        setObservers();
    }

    private void setObservers() {
        viewModel.getAdkarListByCategory().observe(getViewLifecycleOwner(),adkarModelList -> {
            if (adkarModelList != null && adkarModelList.size() > 0) {
                if (adkarAdapter!=null) return;
                showAdkarCategories(adkarModelList);
            }
        });

        viewModel.getDikrUpdateResult().observe(getViewLifecycleOwner(),isUpdated -> {
            if (isUpdated != null) {
                Log.e("checkdata","isUpdated "+isUpdated);
            } else {
                Log.e("checkdata","setObservers: isUpdated is null");
            }
        });
    }

    private void getAdkarByCategory(Integer categoryId) {
        viewModel.setAdkarListByCategory(categoryId);
    }

    private void showAdkarCategories(List<AdkarModel> adkarModelList) {
        adkarAdapter = new AdkarAdapter(adkarModelList, (adkarModel,isSaved) ->
                viewModel.updateDikrSaveState(adkarModel.getId(),isSaved));
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,false);
        binding.includeCategoryAdkarCard.recyclerViewFastAdkar.setLayoutManager(linearLayoutManager);
        binding.includeCategoryAdkarCard.recyclerViewFastAdkar.setAdapter(adkarAdapter);
    }


    private void setListeners() {
        NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment_activity_main3);
        binding.imgBack.setOnClickListener(v -> navController.navigateUp());
    }


}



