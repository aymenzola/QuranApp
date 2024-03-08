package com.app.dz.quranapp.quran.hafs_parte;

import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.dz.quranapp.data.room.Entities.Aya;
import com.app.dz.quranapp.data.room.Entities.AyaString;
import com.app.dz.quranapp.quran.adapters.MushafPageAdapter;
import com.app.dz.quranapp.quran.listeners.OnFragmentListeners;
import com.app.dz.quranapp.quran.models.AyaTextLimits;
import com.app.dz.quranapp.quran.viewmodels.AyatPageViewModel;
import com.app.dz.quranapp.quran.viewmodels.MyViewModel;
import com.app.dz.quranapp.quran.models.AyaPosition;
import com.app.dz.quranapp.Util.QuranInfoManager;
import com.app.dz.quranapp.databinding.QuranPageFragmentBinding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class QuranPageFragment extends Fragment {

    private static final String ARG_PAGE_NUMBER = "page_number";

    private final Map<Integer, AyaPosition> stringAyaPostionHashMap = new HashMap<>();
    private final static String TAG = QuranPageFragment.class.getSimpleName();
    private OnFragmentListeners listener;
    private int pageNumber = 1;
    private QuranPageFragmentBinding binding;
    private AyatPageViewModel viewModel;
    private MushafPageAdapter adapter;
    private Aya lastAyaInPrivouisPage;
    private boolean isThereSelectedAya = false;
    private boolean isTvClicked = true;
    private int lastSelectedItem = -1; //default -1
    private boolean isTitlereated = false;

    private Aya CurrantAya; //default the first aya in sura
    private Aya DefaulAya; //default the first aya in sura
    private MyViewModel StateViewModel;
    private Boolean isfullModeActiveGlobal = false;
    private List<Aya> globalItems;


    public QuranPageFragment() {
        // Required empty public constructor
    }


    public static QuranPageFragment newInstance(int pageNumber) {
        QuranPageFragment fragment = new QuranPageFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE_NUMBER, pageNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentListeners) {
            listener = (OnFragmentListeners) context;
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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            pageNumber = getArguments().getInt(ARG_PAGE_NUMBER);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = QuranPageFragmentBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }


    @SuppressLint("SetTextI18n")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(AyatPageViewModel.class);
        StateViewModel = ViewModelProviders.of(getActivity()).get(MyViewModel.class);

        initializeArticlesAdapter();
        setObservers();
        if (pageNumber == 1) viewModel.setAyatList(pageNumber);
        else getPageAyat(pageNumber);



    }


    private void setObservers() {
        StateViewModel.getData().observe(getViewLifecycleOwner(), isfullModeActive -> {
            isfullModeActiveGlobal = isfullModeActive;
        });
        viewModel.getPevAya().observe(getViewLifecycleOwner(), prevAya -> {
            lastAyaInPrivouisPage = prevAya;
            viewModel.setAyatList(pageNumber);
        });
        viewModel.getAllAyat().observe(getViewLifecycleOwner(), ayatList -> {
            if (ayatList != null && ayatList.size() > 0) displayData(ayatList);
        });
    }

    public void initializeArticlesAdapter() {
        SharedPreferences prefs = getActivity().getSharedPreferences("MyPrefs", MODE_PRIVATE);
        int textSize = prefs.getInt("textSize", 25);
        adapter = new MushafPageAdapter(getActivity(), textSize, new MushafPageAdapter.OnAdapterClickListener() {
            @Override
            public void onClick(Aya aya) {

            }

            @Override
            public void onItemClick(AyaString ayaString, int position) {
                if (isfullModeActiveGlobal) {
                    Log.e("logtag","onItemClick");
                    listener.onScreenClick();
                    return;
                }
                handleClick();
            }

            @Override
            public void onItemTouch(AyaString ayaString, int position, View view, MotionEvent event) {
                if (isfullModeActiveGlobal) {
                    Log.e("logtag","onItemTouch");
                    return;
                }
                handleTouch(ayaString, position, view, event);
            }
        });
        binding.recyclerview.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false));
        binding.recyclerview.setHasFixedSize(true);
        binding.recyclerview.setAdapter(adapter);
    }


    public void getPageAyat(int id) {
        viewModel.setlastAya(id);
    }

    public Aya getCurrantSura() {
        return CurrantAya;
    }

    public int getCurrantPage() {
        return pageNumber;
    }

    public void selectThisAya(Aya aya) {
        Log.e(TAG, "we recieve order to selcet this reader aya " + aya.getText());
        AyaPosition ayaPostion = stringAyaPostionHashMap.get(aya.getId());
        if (ayaPostion != null) {
            Log.e(TAG, "aya position info : " + ayaPostion);

            AyaString ayaString = adapter.getItem(ayaPostion.adapterPosition);
            SpannableStringBuilder ssb = new SpannableStringBuilder(ayaString.getStringBuilder().toString());
            int color = Color.parseColor("#FDD48A");
            BackgroundColorSpan blue = new BackgroundColorSpan(color);
            ssb.setSpan(blue, ayaPostion.start, ayaPostion.end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);


            adapter.selectView(ayaPostion.adapterPosition, ssb);


        } else {
            Log.e(TAG, "aya position is null");
        }
    }

    @SuppressLint("SetTextI18n")
    private void displayData(List<Aya> items) {
        globalItems = items;
        //binding.tvSura.setText("سورة ");
        //binding.tvJuza.setText("" + QuranInfoManager.getInstance().getJuzaName(items.get(0).getJuz()));

        List<AyaTextLimits> ayaLimitsCurrantItems = new ArrayList<>();
        List<AyaPosition> ayaPostionsList = new ArrayList<>();
        List<Aya> ayaList = new ArrayList<>();
        List<AyaString> ayaStringList = new ArrayList<>();
        SpannableStringBuilder stringBuilder = new SpannableStringBuilder();

        CurrantAya = items.get(0);
        DefaulAya = items.get(0);

        int currantPosition = 0;
        if (lastAyaInPrivouisPage != null) {
            if (items.get(0).getSura() != lastAyaInPrivouisPage.getSura()) {
                ayaStringList.add(new AyaString(new SpannableStringBuilder(), true, getSuraTitle(items.get(0).getSura())));
                currantPosition = 1;
            }
        } else {
            //surat fatiha
            ayaStringList.add(new AyaString(new SpannableStringBuilder(), true, getSuraTitle(1)));
            currantPosition = 1;
        }

        if (items.size() == 1) {
            // one aya
            ayaList.add(items.get(0));
            stringBuilder.append(items.get(0).getText()).append(" ﴿ ")
                    .append(String.valueOf(items.get(0).getSuraAya())).append(" ﴾ ");

            AyaPosition ayaPostion = new AyaPosition(items.get(0).getId(), 0, items.get(0).getText().length());
            ayaPostion.adapterPosition = currantPosition;
            stringAyaPostionHashMap.put(ayaPostion.ayaId, ayaPostion);

            ayaLimitsCurrantItems.add(new AyaTextLimits(items.get(0).getId(), 0, items.get(0).getText().length(), items.get(0)));

            AyaString ayaString = new AyaString(stringBuilder, false, "");
            ayaString.setAyaList(ayaList);
            ayaString.setAyaLimitsList(ayaLimitsCurrantItems);

            Log.e("logcheck2", "one aya we add it");

            ayaStringList.add(ayaString);

        } else if (items.size() > 1) {

            for (int i = 0; i < items.size(); i++) {
                if (i + 1 < items.size()) {

                    if (items.get(i).getSura() == items.get(i + 1).getSura()) {

                        int start = stringBuilder.length();
                        stringBuilder.append(items.get(i).getText());

                        int end = stringBuilder.length();
                        stringBuilder.append(" ﴿ ").append(String.valueOf(items.get(i).getSuraAya())).append(" ﴾ ");
                        ayaList.add(items.get(i));
                        ayaLimitsCurrantItems.add(new AyaTextLimits(items.get(i).getId(), start, end, items.get(i)));

                        ayaPostionsList.add(new AyaPosition(items.get(i).getId(), start, end));


                    } else {

                        //add currant last aya in this sura
                        int start = stringBuilder.length();
                        stringBuilder.append(items.get(i).getText());

                        int end = stringBuilder.length();
                        stringBuilder.append(" ﴿ ").append(String.valueOf(items.get(i).getSuraAya())).append(" ﴾ ");
                        ayaList.add(items.get(i));
                        ayaLimitsCurrantItems.add(new AyaTextLimits(items.get(i).getId(), start, end, items.get(i)));

                        ayaPostionsList.add(new AyaPosition(items.get(i).getId(), start, end));


                        //add previous parte

                        AyaString ayaString = new AyaString(stringBuilder, false, "");
                        ayaString.setAyaList(ayaList);
                        ayaString.setAyaLimitsList(ayaLimitsCurrantItems);
                        ayaStringList.add(ayaString);

                        for (AyaPosition ayaPostion : ayaPostionsList) {
                            ayaPostion.adapterPosition = ayaStringList.size() - 1;
                            stringAyaPostionHashMap.put(ayaPostion.ayaId, ayaPostion);
                        }

                        //add this for new sura
                        ayaStringList.add(new AyaString(new SpannableStringBuilder(), true, getSuraTitle(items.get(i + 1).getSura())));

                        //prepare variables
                        stringBuilder = new SpannableStringBuilder();
                        ayaList.clear();
                        ayaLimitsCurrantItems.clear();
                        ayaPostionsList.clear();
                    }

                } else {
                    //we are in the last item
                    if (items.get(i).getSura() == items.get(i - 1).getSura()) {
                        //the last item equal the previous item
                        int start = stringBuilder.length();
                        stringBuilder.append(items.get(i).getText());
                        int end = stringBuilder.length();
                        stringBuilder.append(" ﴿ ").append(String.valueOf(items.get(i).getSuraAya())).append(" ﴾ ");
                        ayaList.add(items.get(i));
                        ayaLimitsCurrantItems.add(new AyaTextLimits(items.get(i).getId(), start, end, items.get(i)));
                        ayaPostionsList.add(new AyaPosition(items.get(i).getId(), start, end));

                        //add previous parte
                        AyaString ayaString = new AyaString(stringBuilder, false, "");
                        ayaString.setAyaList(ayaList);
                        ayaString.setAyaLimitsList(ayaLimitsCurrantItems);
                        ayaStringList.add(ayaString);

                        for (AyaPosition ayaPostion : ayaPostionsList) {
                            ayaPostion.adapterPosition = ayaStringList.size() - 1;
                            stringAyaPostionHashMap.put(ayaPostion.ayaId, ayaPostion);
                        }


                    } else {
                        //the last item does not equal the previous item

                        //add previous parte
                        AyaString ayaString = new AyaString(stringBuilder, false, "");
                        ayaString.setAyaList(ayaList);
                        ayaString.setAyaLimitsList(ayaLimitsCurrantItems);
                        ayaStringList.add(ayaString);

                        for (AyaPosition ayaPostion : ayaPostionsList) {
                            ayaPostion.adapterPosition = ayaStringList.size() - 1;
                            stringAyaPostionHashMap.put(ayaPostion.ayaId, ayaPostion);
                        }

                        int lastItemPostion = items.size() - 1;
                        //add this for new sura
                        ayaStringList.add(new AyaString(new SpannableStringBuilder(), true, getSuraTitle(items.get(lastItemPostion).getSura())));

                    }
                }

            }

        }
        Log.e(TAG, "stringAyaPostionHashMap size : " + stringAyaPostionHashMap.size());
        /*if (!isTitlereated)
            binding.tvSura.setText("سورة " + QuranInfoManager.getInstance().getSuraName(items.get(0).getSura() - 1));

        */
        adapter.additems(ayaStringList);
    }

    private void handleClick() {
        isTvClicked = true;
    }


    @SuppressLint("SetTextI18n")
    private void handleTouch(AyaString model, int position, View view, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            Log.e("logcheck2", "Touch istvcliked" + isTvClicked + " isthereTestSelected " + isThereSelectedAya);
            if (isTvClicked) {
                TextView tv = (TextView) view;
                if (isThereSelectedAya) {
                    listener.onHideAyaInfo();
                    isThereSelectedAya = false;
                    tv.setText(colorNumbersInText(new SpannableStringBuilder(model.getStringBuilder().toString())));
                    CurrantAya = DefaulAya;
                    return;
                }
                int x = (int) event.getX();
                int y = (int) event.getY();
                Layout layout = ((TextView) view).getLayout();
                int line = layout.getLineForVertical(y);
                int characterIndex = layout.getOffsetForHorizontal(line, x);
                isTvClicked = false;
                isThereSelectedAya = true;
                getAyaStartEnd_AndTell_AdapterToSelectIt(characterIndex, position, model);
            }
        }
    }

    private void getAyaStartEnd_AndTell_AdapterToSelectIt(int characterIndex, int position, AyaString model) {
        List<AyaTextLimits> limits = new ArrayList<>(model.getAyaLimitsList());
        for (AyaTextLimits ayaTextLimits : limits) {
            Log.e("logcheck2", "sura " + ayaTextLimits.aya.getSura() + " sura aya : " + ayaTextLimits.aya.getSuraAya() + " start : "
                    + ayaTextLimits.starindexInSura + " end : " + ayaTextLimits.endindexInSura);
        }
        boolean isExist = false;
        for (AyaTextLimits ayaLimits : limits) {
            if (characterIndex >= ayaLimits.starindexInSura && characterIndex < ayaLimits.endindexInSura) {
                isExist = true;
                int start = ayaLimits.starindexInSura;
                int end = ayaLimits.endindexInSura;

                SpannableStringBuilder ssb = new SpannableStringBuilder(model.getStringBuilder().toString());
                int color = Color.parseColor("#FDD48A");
                BackgroundColorSpan blue = new BackgroundColorSpan(color);
                ssb.setSpan(blue, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

                adapter.selectView(position, ssb);
                lastSelectedItem = position;
                listener.onAyaClick(ayaLimits.aya);
                CurrantAya = ayaLimits.aya;
                break;
            }
        }
        if (!isExist) isThereSelectedAya = false;

    }

    public SpannableStringBuilder colorNumbersInText(SpannableStringBuilder builder) {
        // Find all the number patterns in the text
        Pattern pattern = Pattern.compile("\\d+");
        Matcher matcher = pattern.matcher(builder);

        while (matcher.find()) {
            // Get the start and end indices of the matched number
            int start = matcher.start();
            int end = matcher.end();

            // Apply the color to the matched number
            builder.setSpan(new ForegroundColorSpan(Color.parseColor("#B07A1A")), start - 2, end + 2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        }
        return builder;
    }

    public void getFileDuration() {

    }

    @SuppressLint("SetTextI18n")
    public String getSuraTitle(int sura) {
        isTitlereated = true;
        QuranInfoManager quranSuraNames = QuranInfoManager.getInstance();
        String suraName = "سورة " + quranSuraNames.getSuraName(sura - 1);

        /*
        String value = binding.tvSura.getText().toString();
        String suraName2 = "" + quranSuraNames.getSuraName(sura - 1);
        String newValue;
        if (value.equals("سورة ")) {
            newValue = value + suraName2;
        } else {
            newValue = value + " و " + suraName2;
        }
        binding.tvSura.setText(newValue);
        */

        return suraName;
    }

    public String getJuzaName() {
        if (globalItems==null) return "";
        return QuranInfoManager.getInstance().getJuzaNameNumber(globalItems.get(0).getJuz());
    }

    public String getSuraName() {
        if (globalItems==null) return "";
        return  QuranInfoManager.getInstance().getSuraName(globalItems.get(0).getSura() - 1);
    }


}

