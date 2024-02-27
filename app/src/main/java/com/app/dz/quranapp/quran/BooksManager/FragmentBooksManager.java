package com.app.dz.quranapp.quran.BooksManager;


import static com.app.dz.quranapp.Services.QuranServices.ForegroundDownloadAudioService.AppfolderName;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
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

import com.app.dz.quranapp.data.room.Entities.Book;
import com.app.dz.quranapp.data.room.Entities.Hadith;
import com.app.dz.quranapp.databinding.FragmentBookListBinding;
import com.app.dz.quranapp.ui.activities.CollectionParte.BooksParte.BooksViewModel;
import com.app.dz.quranapp.quran.quranParte.ArabicNormalizer;
import com.app.dz.quranapp.data.room.AppDatabase;
import com.app.dz.quranapp.data.room.Daos.BookDao;
import com.app.dz.quranapp.data.room.DatabaseClient;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;


public class FragmentBooksManager extends Fragment {


    private HadithStringAdapter adapter;
    private BooksViewModel viewModel;
    private OnListenerInterface listener;
    private FragmentBookListBinding binding;
    private Integer lastid = null;
    private String collectionName = "";
    private List<Hadith> HadithsGloablList = new ArrayList<>();
    private List<Book> bookList = new ArrayList<>();
    private List<Book> bookListAhadith = new ArrayList<>();
    String[] arrayNames = {
             "bukhari90"
            , "bukhari91"
            , "bukhari92"
            , "bukhari93"
            , "bukhari94"
            , "bukhari95"
            , "bukhari97"
    };


    //CSV
    private int Count = 0;
    private String CurrantFileName = "nasai";
    private int max = 52;


    public FragmentBooksManager() {
        // Required empty public constructor
    }


    public static FragmentBooksManager newInstance(String collectionName) {
        FragmentBooksManager fragment = new FragmentBooksManager();
        Bundle bundle = new Bundle();
        bundle.putString("collectionName", collectionName);
        fragment.setArguments(bundle);
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
        binding = FragmentBookListBinding.inflate(getLayoutInflater(), container, false);
        return binding.getRoot();
    }


    @SuppressLint("SetTextI18n")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(BooksViewModel.class);

        collectionName = CurrantFileName;

        initializeArticlesAdapter();
        Log.e("lifecycle", "B onViewCreated");
        getBooks(collectionName);
        setObservers();

        binding.tvSize.setOnClickListener(v -> {
            new Thread(() -> {
                try {
                    Log.e("books","we start reading");
                    manageHadithObject();
                } catch (IOException e) {
                    Log.e("books","error "+e.getMessage());
                    e.printStackTrace();
                }
            }).start();
            //viewModel.setHadith(CurrantFileName, bookList.get(Count).bookNumber);
        });

        binding.btnCreate.setOnClickListener(v -> {
            createJsonFileHadiths();
            //writeHadithsCsvFromList(HadithsGloablList);
        });


        binding.included.tvTitle.setText("الكتب");
        binding.included.imgBack.setOnClickListener(v -> {
            getActivity().onBackPressed();
        });
    }


    private void setObservers() {
        viewModel.getHadithObject().observe(getViewLifecycleOwner(), hadithObject -> {
            if (hadithObject != null) manageHadithObject(hadithObject);
        });

        viewModel.getDBookObject().observe(getViewLifecycleOwner(), booksObject -> {
            if (booksObject != null) manageBooksObject(booksObject);
        });
    }

    public void initializeArticlesAdapter() {

        adapter = new HadithStringAdapter(getActivity(), model -> moveToAyatFragment(model));
        binding.recyclerview.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false));
        binding.recyclerview.setHasFixedSize(true);
        binding.recyclerview.setAdapter(adapter);


        //TODO for pagination copy these linesb
        /*binding.nestscrollview.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener) (view, i, i1, i2, i3) -> {
            if (!islastData && !binding.nestscrollview.canScrollVertically(1)) {
                Log.e("article", "is last " + islastData);
                getVideos();
            }
        });*/

    }

    private void moveToAyatFragment(Book book) {
      /*  Bundle bundle = new Bundle();
        bundle.putString("collectionName", collectionName);
        bundle.putString("bookNumber", book.bookNumber);
        ActivityChapterList fragment1 = new ActivityChapterList();
        fragment1.setArguments(bundle);
        NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_activity_main3);
        navController.navigate(R.id.action_navigation_Books_to_navigation_Chapters, bundle);
    */
    }

    public void getBooks(String collectionName) {
        viewModel.setBooks(collectionName);

        //TODO for csv
        viewModel.setBooksObject(collectionName);
    }

    private void displayData(List<Book> items) {
        //lastid = items.get(items.size() - 1).getEtag();
        Log.e("article", "size " + items.size());
        adapter.setItems(items);

    }

    public interface OnListenerInterface {
        void onitemclick(int position);
    }

    private void manageBooksObject(Object booksObject) {
        Gson gson = new Gson();
        JsonObject jsonObject = gson.toJsonTree(booksObject).getAsJsonObject();
        JsonArray jsonBooks = jsonObject.getAsJsonArray("data");

        Log.e("books", "" + jsonBooks);


        for (int i = 0; i < jsonBooks.size(); i++) {
            JsonObject book = jsonBooks.get(i).getAsJsonObject();
            String bookNumber = book.get("bookNumber").getAsString();
            JsonArray jsonTitles = book.getAsJsonArray("book");
            String bookName = jsonTitles.get(1).getAsJsonObject().get("name").getAsString();

            bookList.add(new Book(bookNumber, bookName, CurrantFileName));

            Log.e("books", "" + bookNumber + " name " + bookName);

        }

        Log.e("booksn", "bookList.size() " + bookList.size());

        displayData(bookList);
        createJsonFileForBooks();

        //writeCsvFromList(bookList,"csvBooks.csv");
    }

    public void writeCsvFromList(List<Book> books, String fileName) {
        try {
            File csvFile = new File(Environment.getExternalStorageDirectory(), fileName);
            FileWriter writer = new FileWriter(csvFile);

            // Write header row
            writer.write("bookNumber,bookName,bookCollection");
            writer.write("\n");

            // Write each list item as a row in the CSV file
            for (Book book : books) {
                String rowString2 = book.bookNumber + "," + book.bookName + "," + book.bookCollection;
                writer.write(rowString2);
                writer.write("\n");
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @SuppressLint("SetTextI18n")
    private void manageHadithObject(Object chapterObject) {

        Count++;

        Gson gson = new Gson();
        JsonObject jsonObject = gson.toJsonTree(chapterObject).getAsJsonObject();
        JsonArray jsonChapter = jsonObject.getAsJsonArray("data");

        for (int i = 0; i < jsonChapter.size(); i++) {
            JsonObject chapter = jsonChapter.get(i).getAsJsonObject();
            String bookNumber = chapter.get("bookNumber").getAsString();
            String chapterId = chapter.get("chapterId").getAsString();
            String collection = chapter.get("collection").getAsString();
            String hadithNumber = chapter.get("hadithNumber").getAsString();

            JsonArray chapterArray = chapter.getAsJsonArray("hadith");


            String gradedBy = "";
            String grade = "";
            JsonArray gradesArrayEnglish = chapterArray.get(0).getAsJsonObject().getAsJsonArray("grades");
            JsonArray gradesArrayArabic = chapterArray.get(1).getAsJsonObject().getAsJsonArray("grades");

            if (gradesArrayArabic != null && gradesArrayArabic.size() > 0) {
                gradedBy = gradesArrayArabic.get(0).getAsJsonObject().get("graded_by").getAsString();
                grade = gradesArrayArabic.get(0).getAsJsonObject().get("grade").getAsString();
            } else {
                if (gradesArrayEnglish != null && gradesArrayEnglish.size() > 0) {
                    gradedBy = gradesArrayEnglish.get(0).getAsJsonObject().get("graded_by").getAsString();
                    grade = gradesArrayEnglish.get(0).getAsJsonObject().get("grade").getAsString();
                }
            }


            String chapterTitle = chapterArray.get(1).getAsJsonObject().get("chapterTitle").getAsString();
            String HtmlBody = chapterArray.get(1).getAsJsonObject().get("body").getAsString();
            String HtmlBody2 = HtmlBody.replaceAll("(?m)^[ \t]*\r?\n", "");

            bookListAhadith.add(new Book(bookNumber, HtmlBody2, chapterTitle));
            if (i == 0)
                Log.e("bookshadith", "" + chapter);

            HadithsGloablList.add(new Hadith(collection, bookNumber, chapterId, hadithNumber, chapterTitle, HtmlBody2, removeTachkil(HtmlBody2), removeTachkil(chapterTitle), gradedBy, grade));
        }

        if (Count == max) {
            createJsonFileHadiths();
            displayData(bookListAhadith);
            Log.e("books", "Count = " + max + " stop");
            return;
        }

        binding.tvSize.setText(" عدد الاحاديث حاليا " + HadithsGloablList.size());
        Log.e("books", "hadithList.size() = " + HadithsGloablList.size());

        Log.e("booksn", "getting data book number " + bookList.get(Count).bookNumber + " count " + Count);

        viewModel.setHadith(CurrantFileName, bookList.get(Count).bookNumber);

    }

    private void manageHadithObject() throws IOException {

        File file = getFile(getCollectionFileNameJson(arrayNames[Count]));
        InputStream inputStream = new FileInputStream(file);
        Gson gsonObject = new Gson();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        JsonFileModel object = gsonObject.fromJson(bufferedReader,JsonFileModel.class);
        inputStream.close();

        Gson gson = new Gson();
        JsonObject jsonObject = gson.toJsonTree(object).getAsJsonObject();
        JsonArray jsonChapter = jsonObject.getAsJsonArray("data");

        for (int i = 0; i < jsonChapter.size(); i++) {
            JsonObject chapter = jsonChapter.get(i).getAsJsonObject();
            String bookNumber = chapter.get("bookNumber").getAsString();
            String chapterId = chapter.get("chapterId").getAsString();
            String collection = chapter.get("collection").getAsString();
            String hadithNumber = chapter.get("hadithNumber").getAsString();

            JsonArray chapterArray = chapter.getAsJsonArray("hadith");


            String gradedBy = "";
            String grade = "";
            JsonArray gradesArrayEnglish = chapterArray.get(0).getAsJsonObject().getAsJsonArray("grades");
            JsonArray gradesArrayArabic = chapterArray.get(1).getAsJsonObject().getAsJsonArray("grades");

            if (gradesArrayArabic != null && gradesArrayArabic.size() > 0) {
                gradedBy = gradesArrayArabic.get(0).getAsJsonObject().get("graded_by").getAsString();
                grade = gradesArrayArabic.get(0).getAsJsonObject().get("grade").getAsString();
            } else {
                if (gradesArrayEnglish != null && gradesArrayEnglish.size() > 0) {
                    gradedBy = gradesArrayEnglish.get(0).getAsJsonObject().get("graded_by").getAsString();
                    grade = gradesArrayEnglish.get(0).getAsJsonObject().get("grade").getAsString();
                }
            }


            String chapterTitle = chapterArray.get(1).getAsJsonObject().get("chapterTitle").getAsString();
            String HtmlBody = chapterArray.get(1).getAsJsonObject().get("body").getAsString();
            String HtmlBody2 = HtmlBody.replaceAll("(?m)^[ \t]*\r?\n", "");

            bookListAhadith.add(new Book(bookNumber, HtmlBody2, chapterTitle));
            if (i == 0)
                Log.e("bookshadith", "" + chapter);

            HadithsGloablList.add(new Hadith(collection, bookNumber, chapterId, hadithNumber, chapterTitle, HtmlBody2, removeTachkil(HtmlBody2), removeTachkil(chapterTitle), gradedBy, grade));
        }

        if (Count == 6) {
            Log.e("books", "finished hadithList.size() = " + HadithsGloablList.size());
            return;
        }

        Count++;
        Log.e("books", "currant hadithList.size() = " + HadithsGloablList.size()+" count "+Count);
        manageHadithObject();

    }

    public void createJsonFileHadiths() {
        Gson gson = new GsonBuilder().disableHtmlEscaping().create();
        String json = gson.toJson(HadithsGloablList);
        Log.e("books", "creating json file");
        new Thread(() -> {
            try {
                File file = new File(Environment.getExternalStorageDirectory(), CurrantFileName + ".json");
//            File file = new File(context.getFilesDir(), "myFile.json");
                FileWriter writer = new FileWriter(file);
                writer.write(json);
                writer.close();
                Log.e("books", "json is created");
            } catch (IOException e) {
                Log.e("books", "json error " + e.getMessage());
                e.printStackTrace();
            }
        }).start();


    }

    public void createJsonFileForBooks() {
        Gson gson = new GsonBuilder().disableHtmlEscaping().create();
        String json = gson.toJson(bookList);
        Log.e("books", "creating json file");
        new Thread(() -> {
            try {
                File file = new File(Environment.getExternalStorageDirectory(), CurrantFileName + "_books.json");
//            File file = new File(context.getFilesDir(), "myFile.json");
                FileWriter writer = new FileWriter(file);
                writer.write(json);
                writer.close();
                Log.e("books", "json is created");
            } catch (IOException e) {
                Log.e("books", "json error " + e.getMessage());
                e.printStackTrace();
            }
        }).start();


    }

    public String removeTachkil(String hadith) {
        if (hadith == null) return "";
        return new ArabicNormalizer(hadith).getOutput();
    }

    public File getFile(String filename) {
        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
         */
        return new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath() + "/" + AppfolderName + "/" + filename);
        /*} else {
            return new File(Environment.getExternalStorageDirectory().getPath() + "/" + AppfolderName + "/" + filename);
        }*/
    }

    public void getReadJsonHadiths() {
        AppDatabase db = DatabaseClient.getInstance(getActivity()).getAppDatabase();
        BookDao dao = db.getBookDao();

        new Thread(new Runnable() {
            @Override
            public void run() {
                List<Hadith> hadithList;
                try {
                    File file = getFile(getCollectionFileNameJson(CurrantFileName));
                    InputStream inputStream = new FileInputStream(file);
                    Gson gson = new Gson();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                    hadithList = gson.fromJson(bufferedReader, new TypeToken<List<Hadith>>() {
                    }.getType());
                    inputStream.close();
                    dao.insertHadithList(hadithList);
                    //displayData(bookListAhadith);


                    for (Hadith hadith : hadithList) {
                        Log.e("bookstag", "" + hadith.body_no_tachkil);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public String getCollectionFileNameJson(String bookCollection) {
        return bookCollection + ".json";
    }

    class CollectionDownloadDevAsyncTask extends AsyncTask<String, Void, String> {

        List<Hadith> hadithList = new ArrayList<>();

        @Override
        protected String doInBackground(String... names) {
            // Perform background task here

            Log.e("booksn", "we start ");
            File file = getFile(getCollectionFileNameJson(arrayNames[Count]));
            if (!file.exists() || !file.canRead()) {
                return " " + arrayNames[Count] + "Collection cant read the file or does not exist :" + file.exists() + " canRead :" + file.canRead() + " path " + file.getPath();
            } else {

                try {
                    InputStream inputStream = new FileInputStream(file);
                    Gson gson = new Gson();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                    gson.fromJson(bufferedReader, new TypeToken<List<Object>>() {
                    }.getType());
                    inputStream.close();
                    manageHadithObject(gson);
                    //here we have the list

                    return "we save " + arrayNames[Count] + " the json data successfully";
                } catch (IOException e) {
                    e.printStackTrace();
                    return "book error " + arrayNames[Count];
                }
            }
        }

        @Override
        protected void onPostExecute(String result) {
            Log.e("booksn", "onPostExecute result " + result);
        }
    }

    public class Grade{
        public String graded_by;
        public String grade;
    }

    public class Hadithh{
        public String lang;
        public String chapterNumber;
        public String chapterTitle;
        public int urn;
        public String body;
        public ArrayList<Grade> grades;
    }

    public class HadithObject{
        public String collection;
        public String bookNumber;
        public String chapterId;
        public String hadithNumber;
        public ArrayList<Hadithh> hadith;
    }
    public class JsonFileModel{
        public String limit;
        public String total;
        public ArrayList<HadithObject> data;
    }


}
