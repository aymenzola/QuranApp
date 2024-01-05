package com.app.dz.quranapp.PlayerAudioNotification;

public class Statics {

    public static final int NOTIFICATION_ID_FOREGROUND_SERVICE = 8466503;
    public static final int NOTIFICATION_ID_FOREGROUND_SERVICE_2 = 8466502;
    public static final long DELAY_SHUTDOWN_FOREGROUND_SERVICE = 50000;
    public static final long DELAY_UPDATE_NOTIFICATION_FOREGROUND_SERVICE = 10000;

    public static final int countItemsCount_bukhari = 4787;
    public static final int countItemsCount_sahih_muslim = 3966;
    public static final int countItemsCount_hisn_muslim = 99;
    public static final int countItemsCount_abudawaed = 2890;
    public static final int countItemsCount_ibn_majah = 2561;
    public static final int countItemsCount_nisai = 3480;

    public static class ACTION {

        public static final String MAIN_ACTION = "music.action.main";
        public static final String PAUSE_ACTION = "music.action.pause";
        public static final String PLAY_ACTION = "music.action.play";
        public static final String START_ACTION = "music.action.start";
        public static final String STOP_ACTION = "music.action.stop";

        public static final String BACK_AYA_ACTION = "music.action.back";
        public static final String NEXT_AYA_ACTION = "music.action.next";

        public static final String BACK_SURA_ACTION = "music.action.sura_back";
        public static final String NEXT_SURA_ACTION = "music.action.sura_next";


    }

    public static class BROADCAST_DOWNLOAD_ACTION {
        public static final String NORMAL_ACTION = "normal";
        public static final String PROGRESS_ACTION = "progress_update";
        public static final String DOWNLOAD_CANCEL_ACTION = "download_calncel";
        public static final String DOWNLOAD_COMPLETE_ACTION = "download_complete";
        public static final String DOWNLOAD_ERROR_ACTION = "download_error";
        public static final String DOWNLOAD_FAILED_ACTION = "download_failed";
        public static final String DOWNLOAD_PREPAREING_FILES_ACTION = "preparing_files";
    }

    public static class BROADCAST_AUDIO_ACTION {
        public static final String AUDIO_FINISHED_ACTION = "audio_finished";
        public static final String AUDIO_RESUME_ACTION = "audio_resume";
        public static final String AUDIO_PROGRESS_ACTION = "audio_progress";
        public static final String AUDIO_PAUSE_ACTION = "audio_pause";
        public static final String AUDIO_START_ACTION = "audio_start";
        public static final String AUDIO_SELECT_AYA_ACTION = "audio_selcet";
        public static final String AUDIO_STOP_ACTION = "audio_stop";
    }

    public static class STATE_SERVICE {

        public static final int PREPARE = 30;
        public static final int PLAY = 20;
        public static final int PAUSE = 10;
        public static final int NOT_INIT = 0;


    }
}
