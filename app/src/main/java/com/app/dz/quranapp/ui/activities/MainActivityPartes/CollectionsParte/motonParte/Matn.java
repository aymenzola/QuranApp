package com.app.dz.quranapp.ui.activities.MainActivityPartes.CollectionsParte.motonParte;

import com.app.dz.quranapp.Communs.Constants;

import java.util.List;

public class Matn implements java.io.Serializable {

    public int matnId;
    public Integer parentId = -1;
    public String matnTitle;
    public String fileName;
    public String matnDescription;
    private String fileUrl;
    public int pagesCount;
    public String matnImage;
    //should define file size
    public int fileKbSize;
    public boolean isDownloaded = false;

    public List<Matn> children;


    public Matn(String matnTitle, String matnDescription, String fileUrl, int pagesCount, String matnImage, int fileKbSize, String fileName) {
        this.matnTitle = matnTitle;
        this.matnDescription = matnDescription;
        this.fileUrl = fileUrl;
        this.pagesCount = pagesCount;
        this.matnImage = matnImage;
        this.fileKbSize = fileKbSize;
        this.fileName = fileName;
    }

    public Matn() {
    }

    public boolean isParent() {
        return parentId == -1;
    }

    public String getFileUrl() {
        return Constants.matn_file_base_link + fileUrl;
    }


    @Override
    public String toString() {
        return "Matn{" +
                "matnId=" + matnId +
                ", parentId=" + parentId +
                ", matnTitle='" + matnTitle + '\'' +
                ", fileName='" + fileName + '\'' +
                ", matnDescription='" + matnDescription + '\'' +
                ", fileUrl='" + fileUrl + '\'' +
                ", pagesCount=" + pagesCount +
                ", matnImage='" + matnImage + '\'' +
                ", fileKbSize=" + fileKbSize +
                ", isDownloaded=" + isDownloaded +
                ", children=" + children +
                '}';
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }
}
