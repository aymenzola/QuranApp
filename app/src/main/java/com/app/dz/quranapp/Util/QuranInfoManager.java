package com.app.dz.quranapp.Util;

import android.content.Context;

import com.app.dz.quranapp.data.room.Entities.Sura;

import java.util.ArrayList;
import java.util.List;

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

    public String getJuzaNameNumber(int JuzaInt) {
        return "الجزء " +JuzaInt;
    }


    public static String getPageSurasNames(int page){
//        List<Sura> suraList =   SuraRepository.getInstance(context).getSuraListInPage(page);
        List<Sura> suraList =   getSuraList(page);

        StringBuilder suraNames = new StringBuilder();
        for (Sura sura : suraList) {
            suraNames.append(sura.getName()).append(" ");
        }
        return suraNames.toString();
    }



    public static List<Sura> getSuraList(int currantPage) {
        List<Sura> suraList = new ArrayList<>();
        if (currantPage == 1) suraList.add(new Sura(1, "الفاتحة", "الفاتحة", "الفاتحة", "", 1, 7));

        if (currantPage >= 2 && currantPage <= 49)
            suraList.add(new Sura(2, "البقرة", "البقرة", "البقرة", "", 2, 286));

        if (currantPage >= 50 && currantPage <= 76)
            suraList.add(new Sura(3, "ال عمران", "", "", "", 2, 200));

        if (currantPage >= 77 && currantPage <= 105)
            suraList.add(new Sura(4, "النساء", "", "", "", 2, 176));

        if (currantPage == 106) {
            suraList.add(new Sura(4, "النساء", "", "", "", 2, 176));
            suraList.add(new Sura(5, "المائدة", "", "", "", 2, 120));
        }

        if (currantPage >= 107 && currantPage <= 127) {
            suraList.add(new Sura(5, "المائدة", "", "", "", 2, 120));
        }
        if (currantPage >= 128 && currantPage <= 150) {
            suraList.add(new Sura(6, "الانعام", "", "", "", 2, 165));
        }
        if (currantPage >= 151 && currantPage <= 177) {
            suraList.add(new Sura(7, "الأعراف", "", "", "", 2, 206));
        }
        if (currantPage >= 178 && currantPage <= 187) {
            suraList.add(new Sura(8, "الأنفال", "", "", "", 2, 75));
        }
        if (currantPage >= 188 && currantPage <= 208) {
            suraList.add(new Sura(9, "التوبة", "", "", "", 2, 129));
        }
        if (currantPage >= 209 && currantPage < 221) {
            suraList.add(new Sura(10, "يونس", "", "", "", 2, 109));
        }
        if (currantPage == 221) {
            suraList.add(new Sura(10, "يونس", "", "", "", 2, 109));
            suraList.add(new Sura(11, "هود", "", "", "", 2, 123));
        }

        if (currantPage >= 222 && currantPage < 235) {
            suraList.add(new Sura(11, "هود", "", "", "", 2, 123));
        }
        if (currantPage == 235) {
            suraList.add(new Sura(11, "هود", "", "", "", 2, 123));
            suraList.add(new Sura(12, "يوسف", "", "", "", 2, 111));
        }
        if (currantPage >= 236 && currantPage <= 248) {
            suraList.add(new Sura(12, "يوسف", "", "", "", 2, 111));
        }

        if (currantPage >= 249 && currantPage < 255) {
            suraList.add(new Sura(13, "الرعد", "", "", "", 2, 43));
        }

        if (currantPage == 255) {
            suraList.add(new Sura(13, "الرعد", "", "", "", 2, 43));
            suraList.add(new Sura(14, "إبراهيم", "", "", "", 2, 52));
        }

        if (currantPage >= 256 && currantPage <= 261) {
            suraList.add(new Sura(14, "إبراهيم", "", "", "", 2, 52));
        }
        if (currantPage >= 262 && currantPage < 267) {
            suraList.add(new Sura(15, "الحجر", "", "", "", 2, 99));
        }
        if (currantPage == 267) {
            suraList.add(new Sura(15, "الحجر", "", "", "", 2, 99));
            suraList.add(new Sura(16, "النحل", "", "", "", 2, 128));
        }

        if (currantPage >= 268 && currantPage <= 282) {
            suraList.add(new Sura(16, "النحل", "", "", "", 2, 128));
        }

        if (currantPage >= 283 && currantPage < 293) {
            suraList.add(new Sura(17, "الإسراء", "", "", "", 2, 111));
        }
        if (currantPage == 293) {
            suraList.add(new Sura(17, "الإسراء", "", "", "", 2, 111));
            suraList.add(new Sura(18, "الكهف", "", "", "", 2, 110));
        }
        if (currantPage >= 294 && currantPage <= 305) {
            suraList.add(new Sura(18, "الكهف", "", "", "", 2, 110));
        }
        if (currantPage >= 306 && currantPage < 312) {
            suraList.add(new Sura(19, "مريم", "", "", "", 2, 98));
        }
        if (currantPage == 312) {
            suraList.add(new Sura(19, "مريم", "", "", "", 2, 98));
            suraList.add(new Sura(20, "طه", "", "", "", 2, 135));
        }
        if (currantPage >= 313 && currantPage <= 321) {
            suraList.add(new Sura(20, "طه", "", "", "", 2, 135));
        }

        if (currantPage >= 322 && currantPage <= 331) {
            suraList.add(new Sura(21, "الأنبياء", "", "", "", 2, 112));
        }
        if (currantPage >= 332 && currantPage <= 341) {
            suraList.add(new Sura(22, "الحج", "", "", "", 2, 78));
        }
        if (currantPage >= 342 && currantPage <= 349) {
            suraList.add(new Sura(23, "المؤمنون", "", "", "", 2, 118));
        }
        if (currantPage >= 350 && currantPage < 359) {
            suraList.add(new Sura(24, "النور", "", "", "", 2, 64));
        }
        if (currantPage == 359) {
            suraList.add(new Sura(24, "النور", "", "", "", 2, 64));
            suraList.add(new Sura(25, "الفرقان", "", "", "", 2, 77));

        }
        if (currantPage >= 360 && currantPage <= 366) {
            suraList.add(new Sura(25, "الفرقان", "", "", "", 2, 77));
        }
        if (currantPage >= 367 && currantPage <= 376) {
            suraList.add(new Sura(26, "الشعراء", "", "", "", 2, 227));
        }
        if (currantPage >= 377 && currantPage < 385) {
            suraList.add(new Sura(27, "النمل", "", "", "", 2, 93));
        }
        if (currantPage == 385) {
            suraList.add(new Sura(27, "النمل", "", "", "", 2, 93));
            suraList.add(new Sura(28, "القصص", "", "", "", 2, 88));
        }
        if (currantPage >= 386 && currantPage < 396) {
            suraList.add(new Sura(28, "القصص", "", "", "", 2, 88));
        }
        if (currantPage == 396) {
            suraList.add(new Sura(28, "القصص", "", "", "", 2, 88));
            suraList.add(new Sura(29, "العنكبوت", "", "", "", 2, 69));

        }
        if (currantPage >= 397 && currantPage < 404) {
            suraList.add(new Sura(29, "العنكبوت", "", "", "", 2, 69));
        }
        if (currantPage == 404) {
            suraList.add(new Sura(29, "العنكبوت", "", "", "", 2, 69));
            suraList.add(new Sura(30, "الرّوم", "", "", "", 2, 60));
        }
        if (currantPage >= 405 && currantPage <= 410) {
            suraList.add(new Sura(30, "الرّوم", "", "", "", 2, 60));
        }
        if (currantPage >= 411 && currantPage <= 414) {
            suraList.add(new Sura(31, "لقمان", "", "", "", 2, 34));
        }
        if (currantPage >= 415 && currantPage <= 417) {
            suraList.add(new Sura(32, "السجدة", "", "", "", 2, 30));
        }
        if (currantPage >= 418 && currantPage <= 427) {
            suraList.add(new Sura(33, "الأحزاب", "", "", "", 2, 73));
        }
        if (currantPage >= 426 && currantPage < 434) {
            suraList.add(new Sura(34, "سبإ", "", "", "", 2, 54));
        }
        if (currantPage == 434) {
            suraList.add(new Sura(34, "سبإ", "", "", "", 2, 54));
            suraList.add(new Sura(35, "فاطر", "", "", "", 2, 45));
        }
        if (currantPage >= 435 && currantPage < 440) {
            suraList.add(new Sura(35, "فاطر", "", "", "", 2, 45));
        }
        if (currantPage == 440) {
            suraList.add(new Sura(35, "فاطر", "", "", "", 2, 45));
            suraList.add(new Sura(36, "يس", "", "", "", 2, 83));
        }
        if (currantPage >= 441 && currantPage <= 445) {
            suraList.add(new Sura(36, "يس", "", "", "", 2, 83));
        }
        if (currantPage >= 446 && currantPage <= 452) {
            suraList.add(new Sura(37, "الصافات", "", "", "", 2, 182));
        }
        if (currantPage >= 453 && currantPage < 458) {
            suraList.add(new Sura(38, "ص", "", "", "", 2, 88));
        }
        if (currantPage == 458) {
            suraList.add(new Sura(38, "ص", "", "", "", 2, 88));
            suraList.add(new Sura(39, "الزمر", "", "", "", 2, 75));
        }
        if (currantPage >= 459 && currantPage < 467) {
            suraList.add(new Sura(39, "الزمر", "", "", "", 2, 75));
        }
        if (currantPage == 467) {
            suraList.add(new Sura(39, "الزمر", "", "", "", 2, 75));
            suraList.add(new Sura(40, "غافر", "", "", "", 2, 85));
        }
        if (currantPage >= 468 && currantPage <= 476) {
            suraList.add(new Sura(40, "غافر", "", "", "", 2, 85));
        }
        if (currantPage >= 477 && currantPage <= 482) {
            suraList.add(new Sura(41, "فصّلت", "", "", "", 2, 54));
        }
        if (currantPage >= 483 && currantPage < 489) {
            suraList.add(new Sura(42, "الشورى", "", "", "", 2, 53));
        }
        if (currantPage == 489) {
            suraList.add(new Sura(42, "الشورى", "", "", "", 2, 53));
            suraList.add(new Sura(43, "الزخرف", "", "", "", 2, 89));
        }
        if (currantPage >= 490 && currantPage <= 495) {
            suraList.add(new Sura(43, "الزخرف", "", "", "", 2, 89));
        }
        if (currantPage >= 496 && currantPage <= 498) {
            suraList.add(new Sura(44, "الدخان", "", "", "", 2, 59));
        }
        if (currantPage >= 499 && currantPage < 502) {
            suraList.add(new Sura(45, "الجاثية", "", "", "", 2, 37));
        }
        if (currantPage == 502) {
            suraList.add(new Sura(45, "الجاثية", "", "", "", 2, 37));
            suraList.add(new Sura(46, "الأحقاف", "", "", "", 2, 35));
        }
        if (currantPage >= 503 && currantPage <= 506) {
            suraList.add(new Sura(46, "الأحقاف", "", "", "", 2, 35));
        }
        if (currantPage >= 507 && currantPage <= 510) {
            suraList.add(new Sura(47, "محمد", "", "", "", 2, 38));
        }
        if (currantPage >= 511 && currantPage < 515) {
            suraList.add(new Sura(48, "الفتح", "", "", "", 2, 29));
        }
        if (currantPage == 515) {
            suraList.add(new Sura(48, "الفتح", "", "", "", 2, 29));
            suraList.add(new Sura(49, "الحجرات", "", "", "", 2, 18));
        }
        if (currantPage >= 516 && currantPage <= 517) {
            suraList.add(new Sura(49, "الحجرات", "", "", "", 2, 18));
        }
        if (currantPage >= 518 && currantPage < 520) {
            suraList.add(new Sura(50, "ق", "", "", "", 2, 45));
        }
        if (currantPage == 520) {
            suraList.add(new Sura(50, "ق", "", "", "", 2, 45));
            suraList.add(new Sura(51, "الذاريات", "", "", "", 2, 60));
        }
        if (currantPage >= 521 && currantPage < 523) {
            suraList.add(new Sura(51, "الذاريات", "", "", "", 2, 60));
        }
        if (currantPage == 523) {
            suraList.add(new Sura(51, "الذاريات", "", "", "", 2, 60));
            suraList.add(new Sura(52, "الطور", "", "", "", 2, 49));
        }
        if (currantPage >= 524 && currantPage <= 525) {
            suraList.add(new Sura(52, "الطور", "", "", "", 2, 49));
        }
        if (currantPage >= 526 && currantPage < 528) {
            suraList.add(new Sura(53, "النجم", "", "", "", 2, 62));
        }
        if (currantPage == 528) {
            suraList.add(new Sura(53, "النجم", "", "", "", 2, 62));
            suraList.add(new Sura(54, "القمر", "", "", "", 2, 55));
        }
        if (currantPage >= 529 && currantPage < 531) {
            suraList.add(new Sura(54, "القمر", "", "", "", 2, 55));
        }
        if (currantPage == 531) {
            suraList.add(new Sura(54, "القمر", "", "", "", 2, 55));
            suraList.add(new Sura(55, "الرحمن", "", "", "", 2, 78));
        }
        if (currantPage >= 532 && currantPage < 534) {
            suraList.add(new Sura(55, "الرحمن", "", "", "", 2, 78));
        }
        if (currantPage == 534) {
            suraList.add(new Sura(55, "الرحمن", "", "", "", 2, 78));
            suraList.add(new Sura(56, "الواقعة", "", "", "", 2, 96));
        }
        if (currantPage >= 535 && currantPage < 537) {
            suraList.add(new Sura(56, "الواقعة", "", "", "", 2, 96));
        }
        if (currantPage == 537) {
            suraList.add(new Sura(56, "الواقعة", "", "", "", 2, 96));
            suraList.add(new Sura(57, "الحديد", "", "", "", 2, 29));
        }
        if (currantPage >= 538 && currantPage <= 541) {
            suraList.add(new Sura(57, "الحديد", "", "", "", 2, 29));
        }
        if (currantPage >= 542 && currantPage < 545) {
            suraList.add(new Sura(58, "المجادلة", "", "", "", 2, 22));
        }
        if (currantPage == 545) {
            suraList.add(new Sura(58, "المجادلة", "", "", "", 2, 22));
            suraList.add(new Sura(59, "الحشر", "", "", "", 2, 24));
        }
        if (currantPage >= 546 && currantPage <= 548) {
            suraList.add(new Sura(59, "الحشر", "", "", "", 2, 24));
        }
        if (currantPage >= 549 && currantPage < 551) {
            suraList.add(new Sura(60, "الممتحنة", "", "", "", 2, 13));
        }
        if (currantPage == 551) {
            suraList.add(new Sura(60, "الممتحنة", "", "", "", 2, 13));
            suraList.add(new Sura(61, "الصف", "", "", "", 2, 14));
        }
        if (currantPage == 552) {
            suraList.add(new Sura(61, "الصف", "", "", "", 2, 14));
        }
        if (currantPage == 553) {
            suraList.add(new Sura(62, "الجمعة", "", "", "", 2, 11));
        }
        if (currantPage == 554) {
            suraList.add(new Sura(62, "الجمعة", "", "", "", 2, 11));
            suraList.add(new Sura(63, "المنافقون", "", "", "", 2, 11));
        }

        if (currantPage == 555) {
            suraList.add(new Sura(63, "المنافقون", "", "", "", 2, 11));
        }
        if (currantPage >= 556 && currantPage <= 557) {
            suraList.add(new Sura(64, "التغابن", "", "", "", 2, 18));
        }
        if (currantPage >= 558 && currantPage <= 559) {
            suraList.add(new Sura(65, "الطلاق", "", "", "", 2, 12));
        }

        if (currantPage >= 560 && currantPage <= 561) {
            suraList.add(new Sura(66, "التحريم", "", "", "", 2, 12));
        }
        if (currantPage >= 562 && currantPage < 564) {
            suraList.add(new Sura(67, "الملك", "", "", "", 2, 30));
        }
        if (currantPage == 564) {
            suraList.add(new Sura(67, "الملك", "", "", "", 2, 30));
            suraList.add(new Sura(68, "القلم", "", "", "", 2, 52));
        }

        if (currantPage == 565 ) {
            suraList.add(new Sura(68, "القلم", "", "", "", 2, 52));
        }
        if (currantPage == 566) {
            suraList.add(new Sura(68, "القلم", "", "", "", 2, 52));
            suraList.add(new Sura(69, "الحاقة", "", "", "", 2, 52));
        }
        if (currantPage == 567) {
            suraList.add(new Sura(69, "الحاقة", "", "", "", 2, 52));
        }
        if (currantPage == 568) {
            suraList.add(new Sura(69, "الحاقة", "", "", "", 2, 52));
            suraList.add(new Sura(70, "المعارج", "", "", "", 2, 44));
        }
        if (currantPage == 569) {
            suraList.add(new Sura(70, "المعارج", "", "", "", 2, 44));
        }
        if (currantPage == 570) {
            suraList.add(new Sura(70, "المعارج", "", "", "", 2, 44));
            suraList.add(new Sura(71, "نوح", "", "", "", 2, 28));
        }
        if (currantPage == 571) {
            suraList.add(new Sura(71, "نوح", "", "", "", 2, 28));
        }
        if (currantPage >= 572 && currantPage <= 573) {
            suraList.add(new Sura(72, "الجن", "", "", "", 2, 28));
        }
        if (currantPage == 574) {
            suraList.add(new Sura(73, "المزّمّل", "", "", "", 2, 20));
        }
        if (currantPage == 575) {
            suraList.add(new Sura(73, "المزّمّل", "", "", "", 2, 20));
            suraList.add(new Sura(74, "المدّثر", "", "", "", 2, 56));
        }
        if (currantPage == 576) {
            suraList.add(new Sura(74, "المدّثر", "", "", "", 2, 56));
        }
        if (currantPage == 577) {
            suraList.add(new Sura(74, "المدّثر", "", "", "", 2, 56));
            suraList.add(new Sura(75, "القيامة", "", "", "", 2, 40));
        }
        if (currantPage == 578) {
            suraList.add(new Sura(75, "القيامة", "", "", "", 2, 40));
            suraList.add(new Sura(76, "الإنسان", "", "", "", 2, 31));
        }
        if (currantPage == 579) {
            suraList.add(new Sura(76, "الإنسان", "", "", "", 2, 31));
        }
        if (currantPage == 580) {
            suraList.add(new Sura(76, "الإنسان", "", "", "", 2, 31));
            suraList.add(new Sura(77, "المرسلات", "", "", "", 2, 50));
        }
        if (currantPage == 581) {
            suraList.add(new Sura(77, "المرسلات", "", "", "", 2, 50));
        }
        if (currantPage == 582) {
            suraList.add(new Sura(78, "النبأ", "", "", "", 2, 40));
        }
        if (currantPage == 583) {
            suraList.add(new Sura(78, "النبأ", "", "", "", 2, 40));
            suraList.add(new Sura(79, "النازعات", "", "", "", 2, 46));
        }
        if (currantPage == 584) {
            suraList.add(new Sura(79, "النازعات", "", "", "", 2, 46));
        }
        if (currantPage == 585) {
            suraList.add(new Sura(80, "عبس", "", "", "", 2, 42));
        }
        if (currantPage == 586) {
            suraList.add(new Sura(80, "عبس", "", "", "", 2, 42));
            suraList.add(new Sura(81, "التكوير", "", "", "", 2, 29));
        }
        if (currantPage == 587) {
            suraList.add(new Sura(82, "الانفطار", "", "", "", 2, 19));
            suraList.add(new Sura(83, "المطفّفين", "", "", "", 2, 36));
        }
        if (currantPage == 588) {
            suraList.add(new Sura(83, "المطفّفين", "", "", "", 2, 36));
        }
        if (currantPage == 589) {
            suraList.add(new Sura(83, "المطفّفين", "", "", "", 2, 36));
            suraList.add(new Sura(84, "الانشقاق", "", "", "", 2, 25));
        }
        if (currantPage == 590) {
            suraList.add(new Sura(84, "الانشقاق", "", "", "", 2, 25));
            suraList.add(new Sura(85, "البروج", "", "", "", 2, 22));
        }
        if (currantPage == 591) {
            suraList.add(new Sura(86, "الطارق", "", "", "", 2, 17));
            suraList.add(new Sura(87, "الأعلى", "", "", "", 2, 19));
        }
        if (currantPage == 592) {
            suraList.add(new Sura(87, "الأعلى", "", "", "", 2, 19));
            suraList.add(new Sura(88, "الغاشية", "", "", "", 2, 26));
        }
        if (currantPage == 593) {
            suraList.add(new Sura(88, "الغاشية", "", "", "", 2, 26));
            suraList.add(new Sura(89, "الفجر", "", "", "", 2, 30));
        }
        if (currantPage == 594) {
            suraList.add(new Sura(89, "الفجر", "", "", "", 2, 30));
            suraList.add(new Sura(90, "البلد", "", "", "", 2, 20));
        }
        if (currantPage == 595) {
            suraList.add(new Sura(90, "البلد", "", "", "", 2, 20));
            suraList.add(new Sura(91, "الشمس", "", "", "", 2, 15));
            suraList.add(new Sura(92, "الليل", "", "", "", 2, 21));
        }
        if (currantPage == 596) {
            suraList.add(new Sura(92, "الليل", "", "", "", 2, 21));
            suraList.add(new Sura(93, "الضحى", "", "", "", 2, 11));
            suraList.add(new Sura(94, "الشرح", "", "", "", 2, 8));
        }
        if (currantPage == 597) {
            suraList.add(new Sura(94, "الشرح", "", "", "", 2, 8));
            suraList.add(new Sura(95, "التين", "", "", "", 2, 8));
            suraList.add(new Sura(96, "العلق", "", "", "", 2, 19));
        }

        if (currantPage == 598) {
            suraList.add(new Sura(96, "العلق", "", "", "", 2, 19));
            suraList.add(new Sura(97, "القدر", "", "", "", 2, 5));
            suraList.add(new Sura(98, "البينة", "", "", "", 2, 8));
        }
        if (currantPage == 599) {
            suraList.add(new Sura(98, "البينة", "", "", "", 2, 8));
            suraList.add(new Sura(99, "الزلزلة", "", "", "", 2, 8));
            suraList.add(new Sura(100, "العاديات", "", "", "", 2, 11));
        }
        if (currantPage == 600) {
            suraList.add(new Sura(100, "العاديات", "", "", "", 2, 11));
            suraList.add(new Sura(101, "القارعة", "", "", "", 2, 11));
            suraList.add(new Sura(102, "التكاثر", "", "", "", 2, 8));
        }
        if (currantPage == 601) {
            suraList.add(new Sura(103, "العصر", "", "", "", 2, 3));
            suraList.add(new Sura(104, "الهمزة", "", "", "", 2, 9));
            suraList.add(new Sura(105, "الفيل", "", "", "", 2, 5));
        }




        if (currantPage == 602) {
            suraList.add(new Sura(106, "قريش", "", "", "", 2, 4));
            suraList.add(new Sura(107, "الماعون", "", "", "", 2, 7));
            suraList.add(new Sura(108, "الكوثر", "", "", "", 2, 3));
        }
        if (currantPage == 603) {
            suraList.add(new Sura(109, "الكافرون", "", "", "", 2, 6));
            suraList.add(new Sura(110, "النصر", "", "", "", 2, 3));
            suraList.add(new Sura(111, "المسد", "", "", "", 2, 5));

        }

        if (currantPage == 604) {
            suraList.add(new Sura(112, "الإخلاص", "", "", "", 2, 4));
            suraList.add(new Sura(113, "الفلق", "", "", "", 2, 5));
            suraList.add(new Sura(114, "الناس", "", "", "", 2, 6));

        }

        return suraList;
    }

    public static String convertToHijri(int month) {
        switch (month) {
            case 1:
                return "محرم";
            case 2:
                return "صفر";
            case 3:
                return "ربيع الأول";
            case 4:
                return "ربيع الثاني";
            case 5:
                return "جمادى الأولى";
            case 6:
                return "جمادى الثانية";
            case 7:
                return "رجب";
            case 8:
                return "شعبان";
            case 9:
                return "رمضان";
            case 10:
                return "شوال";
            case 11:
                return "ذو القعدة";
            case 12:
                return "ذو الحجة";
            default:
                return "";
        }
    }


}


