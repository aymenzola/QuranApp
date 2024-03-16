package com.app.dz.quranapp.quran.TafsirParte;

import static android.content.Context.MODE_PRIVATE;
import static com.app.dz.quranapp.Util.QuranInfoManager.getPageSurasNames;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.dz.quranapp.Util.QuranInfoManager;
import com.app.dz.quranapp.data.room.Entities.Aya;
import com.app.dz.quranapp.data.room.Entities.AyaTafsir;
import com.app.dz.quranapp.databinding.QuranPageFragmentBinding;
import com.app.dz.quranapp.quran.adapters.TafsirAdapter;
import com.app.dz.quranapp.quran.models.AyaPosition;
import com.app.dz.quranapp.quran.viewmodels.AyatPageViewModel;
import com.app.dz.quranapp.quran.viewmodels.MyViewModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TafsirFragment extends Fragment {

    private static final String ARG_PAGE_NUMBER = "page_number";

    private Map<Integer, AyaPosition> stringAyaPostionHashMap = new HashMap<>();
    private final static String TAG = TafsirFragment.class.getSimpleName();
    private int pageNumber = 1;
    private QuranPageFragmentBinding binding;
    private AyatPageViewModel viewModel;
    private TafsirAdapter adapter;
    private Aya lastAyaInPrivouisPage;
    private boolean isThereSelectedAya = false;
    private boolean isTvClicked = true;
    private int lastSelectedItem = -1; //default -1

    private Aya CurrantAya; //default the first aya in sura
    private Aya DefaulAya; //default the first aya in sura
    private boolean isTitlereated = false;
    private MyViewModel StateViewModel;
    private Boolean isfullModeActiveGlobal = false;
    private List<Aya> globalItems;


    public TafsirFragment() {
        // Required empty public constructor
    }


    public static TafsirFragment newInstance(int pageNumber) {
        TafsirFragment fragment = new TafsirFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE_NUMBER, pageNumber);
        fragment.setArguments(args);
        return fragment;
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
        StateViewModel = new ViewModelProvider(requireActivity()).get(MyViewModel.class);



        initializeArticlesAdapter();
        setObservers();
        if (pageNumber == 1) viewModel.setAyatList(pageNumber);
        else getPageAyat(pageNumber);



    }


    private void displayPageInfo(int pageNumber) {
        binding.tvPageNumber.setText(String.valueOf(pageNumber));
        binding.tvJuzNumber.setText(getJuzaName());
        binding.tvSuraName.setText(getSuraName(pageNumber));
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
        adapter = new TafsirAdapter(getActivity(), textSize, aya -> {
            if (isfullModeActiveGlobal) {
                StateViewModel.setData(false);
                Log.e("logtag","onItemClick");
                return;
            }
            //aya Clicked
            if (isThereSelectedAya) {
                //hide and unselect
                isThereSelectedAya = false;
                 } else {
                //select and show layout info
                isThereSelectedAya = true;
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

    public String getJuzaName() {
        if (globalItems==null) return "";
        return "" + QuranInfoManager.getInstance().getJuzaNameNumber(globalItems.get(0).getJuz());
    }

    public String getSuraName() {
        if (globalItems==null) return "";
        return QuranInfoManager.getInstance().getSuraName(globalItems.get(0).getSura() - 1);
    }

    public String getSuraName(int pageNumber) {
        String suranName = getPageSurasNames(pageNumber);
        Log.e("juza_tag", "asking fro page ayat list " + pageNumber + " sura name " + suranName);
        return suranName;
    }

    @SuppressLint("SetTextI18n")
    private void displayData(List<Aya> items) {
        globalItems = items;
        displayPageInfo(pageNumber);

        //binding.tvSura.setText("سورة ");
        //binding.tvJuza.setText("" + QuranInfoManager.getInstance().getJuzaName(items.get(0).getJuz()));

        List<AyaTafsir> ayaTafsirsList = new ArrayList<>();

        CurrantAya = items.get(0);
        DefaulAya = items.get(0);

        if (lastAyaInPrivouisPage != null) {
            if (items.get(0).getSura() != lastAyaInPrivouisPage.getSura()) {
                ayaTafsirsList.add(new AyaTafsir(true, false, getSuraTitle(items.get(0).getSura())));
            }
        } else {
            //surat fatiha
            ayaTafsirsList.add(new AyaTafsir(true, false, getSuraTitle(1)));
        }

        if (items.size() == 1) {
            // one aya
            AyaTafsir ayaString = new AyaTafsir(false, false, "", items.get(0), items.get(0).getPage());
            Log.e("logcheck2", "one aya we add it");
            ayaTafsirsList.add(ayaString);

        } else if (items.size() > 1) {

            for (int i = 0; i < items.size(); i++) {
                if (i + 1 < items.size()) {

                    if (items.get(i).getSura() == items.get(i + 1).getSura()) {

                        AyaTafsir ayaString = new AyaTafsir(false, false, "", items.get(i), items.get(i).getPage());
                        ayaTafsirsList.add(ayaString);

                    } else {

                        //add currant last aya in this sura

                        AyaTafsir ayaString = new AyaTafsir(false, false, "", items.get(i), items.get(i).getPage());
                        ayaTafsirsList.add(ayaString);

                        ayaTafsirsList.add(new AyaTafsir(true, false, getSuraTitle(items.get(i + 1).getSura())));
                    }

                } else {
                    //we are in the last item
                    if (items.get(i).getSura() == items.get(i - 1).getSura()) {
                        //the last item equal the previous item
                        AyaTafsir ayaString = new AyaTafsir(false, false, "", items.get(i), items.get(i).getPage());
                        ayaTafsirsList.add(ayaString);


                    } else {
                        //the last item does not equal the previous item
                        //add this for new sura
                        int lastItemPostion = items.size() - 1;
                        ayaTafsirsList.add(new AyaTafsir(true, false, getSuraTitle(items.get(lastItemPostion).getSura())));

                        AyaTafsir ayaString = new AyaTafsir(false, false, "", items.get(i), items.get(i).getPage());
                        ayaTafsirsList.add(ayaString);
                    }
                }

            }

        }

        /*if (!isTitlereated)
            binding.tvSura.setText("سورة " + QuranInfoManager.getInstance().getSuraName(items.get(0).getSura() - 1));
        */

        adapter.additems(ayaTafsirsList);
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

    @SuppressLint("SetTextI18n")
    public String getSuraTitle(int sura) {
        isTitlereated = true;
        QuranInfoManager quranSuraNames = QuranInfoManager.getInstance();

        /*String value = binding.tvSura.getText().toString();
        String suraName2 = "" + quranSuraNames.getSuraName(sura - 1);
        String newValue;
        if (value.equals("سورة ")) {
            newValue = value + suraName2;
        } else {
            newValue = value + " و " + suraName2;
        }
        binding.tvSura.setText(newValue);

*/
        return "سورة " + quranSuraNames.getSuraName(sura - 1);
    }


}

