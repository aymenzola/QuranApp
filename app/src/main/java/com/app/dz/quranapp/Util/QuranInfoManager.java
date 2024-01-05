package com.app.dz.quranapp.Util;

public class QuranInfoManager {
    private static QuranInfoManager instance;

    private static final int[] SURA_AYAT_COUNTS = {
            7, 286, 200, 176, 120, 165, 206, 75, 129, 109,
            123, 111, 43, 52, 99, 128, 111, 110, 98, 135,
            112, 78, 118, 64, 77, 227, 93, 88, 69, 60,
            34, 30, 73, 54, 45, 83, 182, 88, 75, 85,
            54, 53, 89, 59, 37, 35, 38, 29, 18, 45,
            60, 49, 62, 55, 78, 96, 29, 22, 24, 13,
            14, 11, 11, 18, 12, 12, 30, 52, 52, 44,
            28, 28, 20, 56, 40, 31, 50, 40, 46, 42,
            29, 19, 36, 25, 22, 17, 19, 26, 30, 20,
            15, 21, 11, 8, 8, 19, 5, 8, 8, 11,
            11, 8, 3, 9, 5, 4, 7, 3, 6, 3,
            5, 4, 5, 6
    };

    private static final String[] READER_NAMES = {
            "Alafasy",
            "Shuraym",
            "Sudais",
            "Mohammad_al_Tablaway_128kbps",
            "AbdulBaset/Murattal",
    };

    private static final String[] READER_NAMES_TAG = {
            "Alafasy",
            "Shuraym",
            "Sudais",
            "MohammadTab",
            "AbdulBaset",
    };

    private String getReaderTag(String readerName) {
        switch (readerName) {
            case "Alafasy":
                return READER_NAMES_TAG[0];
            case "Shuraym":
                return READER_NAMES_TAG[1];
            case "Sudais":
                return READER_NAMES_TAG[2];
            case "Mohammad_al_Tablaway_128kbps":
                return READER_NAMES_TAG[3];
            default:
                return READER_NAMES_TAG[4];
        }
    }

    private static final String[] SURA_NAMES = {
            "الفاتحة", "البقرة", "آل عمران", "النساء", "المائدة", "الأنعام",
            "الأعراف", "الأنفال", "التوبة", "يونس", "هود", "يوسف", "الرعد",
            "إبراهيم", "الحجر", "النحل", "الإسراء", "الكهف", "مريم", "طه",
            "الأنبياء", "الحج", "المؤمنون", "النور", "الفرقان", "الشعراء",
            "النمل", "القصص", "العنكبوت", "الرّوم", "لقمان", "السجدة",
            "الأحزاب", "سبإ", "فاطر", "يس", "الصافات", "ص", "الزمر", "غافر",
            "فصّلت", "الشورى", "الزخرف", "الدخان", "الجاثية", "الأحقاف",
            "محمد", "الفتح", "الحجرات", "ق", "الذاريات", "الطور", "النجم",
            "القمر", "الرحمن", "الواقعة", "الحديد", "المجادلة", "الحشر",
            "الممتحنة", "الصف", "الجمعة", "المنافقون", "التغابن", "الطلاق",
            "التحريم", "الملك", "القلم", "الحاقة", "المعارج", "نوح",
            "الجن", "المزّمّل", "المدّثر", "القيامة", "الإنسان", "المرسلات",
            "النبأ", "النازعات", "عبس", "التكوير", "الانفطار", "المطفّفين",
            "الانشقاق", "البروج", "الطارق", "الأعلى", "الغاشية", "الفجر",
            "البلد", "الشمس", "الليل", "الضحى", "الشرح", "التين", "العلق",
            "القدر", "البينة", "الزلزلة", "العاديات", "القارعة", "التكاثر",
            "العصر", "الهمزة", "الفيل", "قريش", "الماعون"
            , "الكوثر"
            , "الكافرون"
            , "النصر"
            , "المسد"
            , "الإخلاص"
            , "الفلق"
            , "الناس"
    };


    private static final String[] SURA_PAGES = {
            "1", "البقرة", "آل عمران", "النساء", "المائدة", "الأنعام",
            "الأعراف", "الأنفال", "التوبة", "يونس", "هود", "يوسف", "الرعد",
            "إبراهيم", "الحجر", "النحل", "الإسراء", "الكهف", "مريم", "طه",
            "الأنبياء", "الحج", "المؤمنون", "النور", "الفرقان", "الشعراء",
            "النمل", "القصص", "العنكبوت", "الرّوم", "لقمان", "السجدة",
            "الأحزاب", "سبإ", "فاطر", "يس", "الصافات", "ص", "الزمر", "غافر",
            "فصّلت", "الشورى", "الزخرف", "الدخان", "الجاثية", "الأحقاف",
            "محمد", "الفتح", "الحجرات", "ق", "الذاريات", "الطور", "النجم",
            "القمر", "الرحمن", "الواقعة", "الحديد", "المجادلة", "الحشر",
            "الممتحنة", "الصف", "الجمعة", "المنافقون", "التغابن", "الطلاق",
            "التحريم", "الملك", "القلم", "الحاقة", "المعارج", "نوح",
            "الجن", "المزّمّل", "المدّثر", "القيامة", "الإنسان", "المرسلات",
            "النبأ", "النازعات", "عبس", "التكوير", "الانفطار", "المطفّفين",
            "الانشقاق", "البروج", "الطارق", "الأعلى", "الغاشية", "الفجر",
            "البلد", "الشمس", "الليل", "الضحى", "الشرح", "التين", "العلق",
            "القدر", "البينة", "الزلزلة", "العاديات", "القارعة", "التكاثر",
            "العصر", "الهمزة", "الفيل", "قريش", "الماعون"
            , "الكوثر"
            , "الكافرون"
            , "النصر"
            , "المسد"
            , "الإخلاص"
            , "الفلق"
            , "الناس"
    };

    private QuranInfoManager() {
    }

    public static QuranInfoManager getInstance() {
        if (instance == null) {
            instance = new QuranInfoManager();
        }
        return instance;
    }

    public String getSuraName(int index) {
        if (index >= 0 && index < SURA_NAMES.length) {
            return SURA_NAMES[index];
        } else {
            return "";
        }
    }

    public String getReaderName(int index) {
        if (index >= 0 && index < READER_NAMES.length) {
            return READER_NAMES[index];
        } else {
            return "";
        }
    }

    public double getReadPercentage(int suraIndex, int ayaIndex) {
        if (suraIndex >= 0 && suraIndex < SURA_NAMES.length) {
            int suraAyatCount = SURA_AYAT_COUNTS[suraIndex];
            if (ayaIndex >= 0 && ayaIndex < suraAyatCount) {
                int totalAyatCount = 0;
                for (int i = 0; i < SURA_AYAT_COUNTS.length; i++) {
                    totalAyatCount += SURA_AYAT_COUNTS[i];
                }
                int currentAyatCount = 0;
                for (int i = 0; i < suraIndex; i++) {
                    currentAyatCount += SURA_AYAT_COUNTS[i];
                }
                currentAyatCount += ayaIndex;
                double percentage = (double) currentAyatCount / totalAyatCount * 100.0;
                return percentage;
            } else {
                throw new IndexOutOfBoundsException("Invalid aya index");
            }
        } else {
            throw new IndexOutOfBoundsException("Invalid sura index");
        }
    }

    public String getJuzaName(int JuzaInt) {
        String prefex = "الجزء ";
        switch (JuzaInt) {
            case 1:
                return prefex + " الأول ";
            case 2:
                return prefex + " الثاني ";
            case 3:
                return prefex + " الثالث ";
            case 4:
                return prefex + " الرابع ";
            case 5:
                return prefex + " الخامس ";
            case 6:
                return prefex + " السادس ";
            case 7:
                return prefex + " السابع ";
            case 8:
                return prefex + " الثامن ";
            case 9:
                return prefex + " التاسع ";
            case 10:
                return prefex + " العاشر ";
            case 11:
                return prefex + " الحادي عشر ";
            case 12:
                return prefex + " الثاني عشر ";
            case 13:
                return prefex + " الثالث عشر ";
            case 14:
                return prefex + " الرابع عشر ";
            case 15:
                return prefex + " الخامس عشر ";
            case 16:
                return prefex + " السادس عشر ";
            case 17:
                return prefex + " السابع عشر ";
            case 18:
                return prefex + " الثامن عشر ";
            case 19:
                return prefex + " التاسع عشر ";
            case 20:
                return prefex + " العشرون ";
            case 21:
                return prefex + " الواحد و العشرون ";
            case 22:
                return prefex + " الثاني و العشرون ";
            case 23:
                return prefex + " الثالث و العشرون";
            case 24:
                return prefex + " الرابع و العشرون";
            case 25:
                return prefex + " الخامس و العشرون";
            case 26:
                return prefex + " السادس و العشرون";
            case 27:
                return prefex + " السابع و العشرون";
            case 28:
                return prefex + " الثامن و العشرون";
            case 29:
                return prefex + " التاسع و العشرون";
            default:
                return prefex + " الثلاثون ";
        }
    }


}


