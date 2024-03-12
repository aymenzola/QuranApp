package com.app.dz.quranapp.ui.activities.AdkarParte;


import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.app.dz.quranapp.R;
import com.app.dz.quranapp.databinding.DialogAdkarOnBinding;
import com.app.dz.quranapp.databinding.FragmentAdkarBinding;
import com.app.dz.quranapp.ui.activities.MainActivityPartes.HomeFragment.AdkarViewModel;
import com.app.dz.quranapp.ui.activities.subha.AdkarSubhaUtils;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;


public class AdkarFragment extends Fragment {

    private boolean isFirstLaunch = true;

    public final static String TAG = "FragmentQuranList";
    private FragmentAdkarBinding binding;
    private int adkarCouner = 0;
    //private List<AdkarModel> globalAdkarList = new ArrayList<>();
    private AdkarViewModel viewModel;
    AdkarCountsHelper AdkarHelper;
    private AdkarAdapterCategory adkarAdapterCategory;
    private NavController navController;

    public AdkarFragment() {
        // Required empty public constructor
    }


    public static AdkarFragment newInstance() {
        AdkarFragment fragment = new AdkarFragment();
        Log.e("lifecycle", "create new instance FragmentPlayLists");
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentAdkarBinding.inflate(getLayoutInflater(), container, false);
        Log.e("lifecycle", "onCreateView FragmentPlayLists");
        return binding.getRoot();
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e("lifecycle", "onCreate FragmentPlayLists");
        navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_activity_main3);
        viewModel = new ViewModelProvider(this).get(AdkarViewModel.class);
        AdkarHelper = AdkarCountsHelper.getInstance(requireActivity());

    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.e("lifecycle", "onViewCreated FragmentPlayLists");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requireActivity().getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            requireActivity().getWindow().setStatusBarColor(requireActivity().getColor(R.color.blan));
        }

        setObservers();
        if (savedInstanceState == null) setListeners();

        //viewModel.setFastDick();
        initializeAdkarState();
        viewModel.setAdkarList();


    }

    private void showAdkarCategories(List<AdkarModel> list) {
        adkarAdapterCategory = new AdkarAdapterCategory(list, adkarModel -> {
            NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_activity_main3);
            Bundle bundle = new Bundle();
            bundle.putSerializable("adkarModel", adkarModel);
            navController.navigate(R.id.action_adkarFragment_to_adkarDetailsFragment, bundle);
        }, requireActivity());
        GridLayoutManager linearLayoutManager = new GridLayoutManager(requireActivity(), 2, LinearLayoutManager.VERTICAL, false);
        binding.includeCategoryAdkarCard.recyclerViewFastAdkar.setLayoutManager(linearLayoutManager);
        binding.includeCategoryAdkarCard.recyclerViewFastAdkar.setAdapter(adkarAdapterCategory);
    }

    private void initializeAdkarState() {
        boolean adkarState = AdkarHelper.getAdkarState();
        binding.includeActivateAdkarCard.switchAdkar.setChecked(adkarState);
        changeEnableState(adkarState);
        if (adkarState) selectedDefaultButton();
    }


    private void setListeners() {

        String dikr = AdkarSubhaUtils.getLastDikr(requireActivity());
        binding.includeFastAdkarCard.tvFastAdkarText.setText(dikr);

        binding.includeFastAdkarCard.tvFastAdkarTitle.setSelected(true);
        binding.imgBack.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_activity_main3);
            navController.navigate(R.id.action_adkarFragment_to_fragment_home);
        });

        binding.includeFastAdkarCard.btnFastAdkar.setOnClickListener(v -> handleFastDikrClick());

        binding.includeActivateAdkarCard.switchAdkar.setOnCheckedChangeListener((buttonView, isChecked) -> adkarSwitchStateChanged(isChecked));

        binding.includeActivateAdkarCard.tvLevel1.setOnClickListener(this::handleLevelsClick);
        binding.includeActivateAdkarCard.tvLevel2.setOnClickListener(this::handleLevelsClick);
        binding.includeActivateAdkarCard.tvLevel3.setOnClickListener(this::handleLevelsClick);
        binding.includeActivateAdkarCard.tvLevel4.setOnClickListener(this::handleLevelsClick);
    }


    private void adkarSwitchStateChanged(boolean isChecked) {
        AdkarHelper.saveAdkarState(isChecked);
        if (isChecked) {
            selectedDefaultButton();
            changeEnableState(true);
            scheduleAdkar();
            if (!isFirstLaunch) {
                dialogAdkarInfo();
            }
        } else {
            disableALLButtons();
            Toast.makeText(requireActivity(), "Previous Work Cancelled", Toast.LENGTH_SHORT).show();
            AdkarHelper.resetAllAdkarData(requireActivity());
        }

        isFirstLaunch = false;
    }

    private void disableALLButtons() {
        unselecteAllButtons();

        resetAllButtonTextColor();
        changeEnableState(false);
    }


    private void handleLevelsClick(View v) {
        if (!binding.includeActivateAdkarCard.switchAdkar.isChecked()) return;
        if (AdkarHelper.getAdkarLevel() == Integer.parseInt(v.getTag().toString())) return;


        unselecteAllButtons();

        resetAllButtonTextColor();

        AdkarHelper.saveAdkarLevel(Integer.parseInt(v.getTag().toString()));

        dialogAdkarInfo();

        v.setBackgroundResource(R.drawable.shape_button_selected);
        TextView tv = (TextView) v;
        tv.setTextColor(getResources().getColor(R.color.white));
        scheduleAdkar();
        String dikr = AdkarSubhaUtils.getLastDikr(requireActivity());
        binding.includeFastAdkarCard.tvFastAdkarText.setText(dikr);
    }

    private String getMessage() {
        if (AdkarHelper.getAdkarLevel() == 1) {
            return "بتفعيلك خاصية الاشعارات في الوضع العالي ستصلك 7 إشعارات في اليوم!\uD83D\uDE01";
        } else if (AdkarHelper.getAdkarLevel() == 2) {
            return "بتفعيلك خاصية الاشعارات في الوضع العادي ستصلك 5 إشعارات في اليوم!\uD83D\uDE01";
        } else if (AdkarHelper.getAdkarLevel() == 3) {
            return "بتفعيلك خاصية الاشعارات في الوضع المتوسط ستصلك 3 إشعارات في اليوم!\uD83D\uDE01";
        } else {
            return "بتفعيلك خاصية الاشعارات في الوضع المنخفض سيصلك إشعاران في اليوم!\uD83D\uDE01";
        }
    }

    @SuppressLint("SetTextI18n")
    private void handleFastDikrClick() {
        adkarCouner = adkarCouner + 1;
        binding.includeFastAdkarCard.btnFastAdkar.setText(String.valueOf(adkarCouner));
    }

    public void setObservers() {

        viewModel.getFastDikr().observe(getViewLifecycleOwner(), adkarModelList -> {
            if (adkarModelList != null && adkarModelList.size() > 0) {
                //globalAdkarList = adkarModelList;
                //adkarCouner = 1;
                //binding.includeFastAdkarCard.tvFastAdkarText.setText(adkarModelList.get(0).getDikr());
            }
        });


        viewModel.getAdkarList().observe(getViewLifecycleOwner(), adkarModelList -> {
            if (adkarModelList != null && adkarModelList.size() > 0) {
                showAdkarCategories(adkarModelList);
            }
        });
    }

    private void scheduleAdkar() {
        AdkarHelper.resetAllAdkarData(requireActivity());
        Calendar now = Calendar.getInstance();
        // Get the current hour and minute
        int currentHour = now.get(Calendar.HOUR_OF_DAY);

        if (currentHour >= 8) {

            //using work manager to schedule the next dikr notification
            AdkarHelper.setIsFirstNotification(true);
            PeriodicWorkRequest adkarNotificationRequest = new PeriodicWorkRequest.Builder(AdkarNotificationWorker.class, AdkarHelper.getAdkarLevel(), TimeUnit.HOURS).build();
            AdkarHelper.saveAdkarWorkId(adkarNotificationRequest.getId().toString());
            AdkarHelper.saveScheduledNotificationCount((int) AdkarHelper.getNumberOfNotifications());
            WorkManager.getInstance(requireActivity()).enqueue(adkarNotificationRequest);
            //using alarm manager to schedule the next dikr notification
            AdkarHelper.scheduleDikrAlarm(requireActivity());
        } else {
            WeAreIntheNight();
        }


    }

    private void WeAreIntheNight() {

        Calendar now = Calendar.getInstance();
        // Get the current hour and minute
        int currentHour = now.get(Calendar.HOUR_OF_DAY);
        int currentMinute = now.get(Calendar.MINUTE);


        // Calculate the remaining minutes today
        int remainingMinutesToday = (8 * 60) - (currentHour * 60 + currentMinute);

        // Convert the total minutes to hours
        long interval = remainingMinutesToday / 60;

        //schedule the next day notification
        OneTimeWorkRequest adkarNotificationRequest = new OneTimeWorkRequest.Builder(AdkarNotificationWorkerTomorrow.class)
                .setInitialDelay(interval, TimeUnit.HOURS).build();
        AdkarHelper.saveAdkarWorkId(adkarNotificationRequest.getId().toString());

        WorkManager.getInstance(requireActivity()).enqueue(adkarNotificationRequest);
    }


    private void resetAllButtonTextColor() {
        binding.includeActivateAdkarCard.tvLevel1.setTextColor(getResources().getColor(R.color.black));
        binding.includeActivateAdkarCard.tvLevel2.setTextColor(getResources().getColor(R.color.black));
        binding.includeActivateAdkarCard.tvLevel3.setTextColor(getResources().getColor(R.color.black));
        binding.includeActivateAdkarCard.tvLevel4.setTextColor(getResources().getColor(R.color.black));
    }

    private void unselecteAllButtons() {
        binding.includeActivateAdkarCard.tvLevel1.setBackgroundResource(R.drawable.shape_button_unselected);
        binding.includeActivateAdkarCard.tvLevel2.setBackgroundResource(R.drawable.shape_button_unselected);
        binding.includeActivateAdkarCard.tvLevel3.setBackgroundResource(R.drawable.shape_button_unselected);
        binding.includeActivateAdkarCard.tvLevel4.setBackgroundResource(R.drawable.shape_button_unselected);
    }

    public void changeEnableState(boolean state) {
        binding.includeActivateAdkarCard.tvLevel1.setEnabled(state);
        binding.includeActivateAdkarCard.tvLevel2.setEnabled(state);
        binding.includeActivateAdkarCard.tvLevel3.setEnabled(state);
        binding.includeActivateAdkarCard.tvLevel4.setEnabled(state);
    }

    private void selectedDefaultButton() {
        int adkarLevel = AdkarHelper.getAdkarLevel();
        switch (adkarLevel) {
            case 1 -> {
                binding.includeActivateAdkarCard.tvLevel1.setBackgroundResource(R.drawable.shape_button_selected);
                binding.includeActivateAdkarCard.tvLevel1.setTextColor(getResources().getColor(R.color.white));
            }
            case 2 -> {
                binding.includeActivateAdkarCard.tvLevel2.setBackgroundResource(R.drawable.shape_button_selected);
                binding.includeActivateAdkarCard.tvLevel2.setTextColor(getResources().getColor(R.color.white));
            }
            case 3 -> {
                binding.includeActivateAdkarCard.tvLevel3.setBackgroundResource(R.drawable.shape_button_selected);
                binding.includeActivateAdkarCard.tvLevel3.setTextColor(getResources().getColor(R.color.white));
            }
            case 4 -> {
                binding.includeActivateAdkarCard.tvLevel4.setBackgroundResource(R.drawable.shape_button_selected);
                binding.includeActivateAdkarCard.tvLevel4.setTextColor(getResources().getColor(R.color.white));
            }
        }
    }

    public void dialogAdkarInfo() {

        MaterialAlertDialogBuilder dialogBuilder = new MaterialAlertDialogBuilder(requireActivity());

        DialogAdkarOnBinding binding = DataBindingUtil.inflate(LayoutInflater.from(requireActivity()),
                R.layout.dialog_adkar_on, null, false);
        dialogBuilder.setView(binding.getRoot());
        AlertDialog dialog = dialogBuilder.create();

        if (dialog.getWindow() != null)
            dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

        dialog.setCancelable(true);
        dialog.show();

        binding.tvMessage.setText(getMessage());

        binding.btnDone.setOnClickListener(v -> {
            dialog.dismiss();
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        isFirstLaunch = true;
        Log.e("lifecycle", "onPause FragmentPlayLists");
    }

}




