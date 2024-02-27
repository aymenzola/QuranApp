package com.app.dz.quranapp.data.room;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "articles")
class ArticleModel {
    @PrimaryKey()
    @NonNull
    private String id;
    @NonNull
    private String adresse;
    @NonNull
    private String username;


    public ArticleModel(@NonNull String id, @NonNull String adresse, @NonNull String username) {
        this.id = id;
        this.adresse = adresse;
        this.username = username;
    }

    @NonNull
    public String getId() {
        return id;
    }

    @NonNull
    public String getUsername() {
        return username;
    }

    public void setUsername(@NonNull String username) {
        this.username = username;
    }

    public void setId(@NonNull String id) {
        this.id = id;
    }

    @NonNull
    public String getAdresse() {
        return adresse;
    }

    public void setAdresse(@NonNull String adresse) {
        this.adresse = adresse;
    }
}
