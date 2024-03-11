package com.app.dz.quranapp.ui.activities.MainActivityPartes.CollectionsParte.moreBooksParte;

import java.util.List;

public class Book implements java.io.Serializable {

    public int bookId;
    public Integer parentId = -1;
    public String bookTitle;
    public String fileName;
    public String bookDescription;
    private String fileUrl;
    public int pagesCount;
    public String bookImage;
    //should define file size
    public int fileKbSize;
    public boolean isDownloaded = false;

    public List<Book> children;


    public Book(String matnTitle, String matnDescription, String fileUrl, int pagesCount, String matnImage, int fileKbSize, String fileName) {
        this.bookTitle = matnTitle;
        this.bookDescription = matnDescription;
        this.fileUrl = fileUrl;
        this.pagesCount = pagesCount;
        this.bookImage = matnImage;
        this.fileKbSize = fileKbSize;
        this.fileName = fileName;
    }

    public Book() {
    }

    public boolean isParent() {
        return parentId == -1;
    }

    public String getFileUrl() {
        return  fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    @Override
    public String toString() {
        return "Book{" +
                "bookId=" + bookId +
                ", parentId=" + parentId +
                ", bookTitle='" + bookTitle + '\'' +
                ", fileName='" + fileName + '\'' +
                ", bookDescription='" + bookDescription + '\'' +
                ", fileUrl='" + fileUrl + '\'' +
                ", pagesCount=" + pagesCount +
                ", bookImage='" + bookImage + '\'' +
                ", fileKbSize=" + fileKbSize +
                ", isDownloaded=" + isDownloaded +
                ", children=" + children +
                '}';
    }
}
