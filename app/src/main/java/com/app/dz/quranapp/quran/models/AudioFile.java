package com.app.dz.quranapp.quran.models;

import java.io.File;

public class AudioFile {
    public File file;
    public String fileName;
    public String fileSize;

    public AudioFile(File file,String fileName, String fileSize) {
        this.file = file;
        this.fileName = fileName;
        this.fileSize = fileSize;
    }
}
