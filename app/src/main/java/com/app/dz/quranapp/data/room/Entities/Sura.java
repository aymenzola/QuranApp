package com.app.dz.quranapp.data.room.Entities;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;
import java.util.Objects;

@Entity
public class Sura implements Serializable {

    @PrimaryKey
    private int id;

    @NonNull
    private String name;

    @NonNull
    private String tname;

    @NonNull
    private String ename;

    @NonNull
    private String type;

    private int order;

    private int ayas;


    public Sura(int id, @NonNull String name, @NonNull String tname, @NonNull String ename
            , @NonNull String type, int order, int ayas) {
        this.id = id;
        this.name = name;
        this.tname = tname;
        this.ename = ename;
        this.type = type;
        this.order = order;
        this.ayas = ayas;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @NonNull
    public String getName() {
        return name;
    }

    public void setName(@NonNull String name) {
        this.name = name;
    }

    @NonNull
    public String getTname() {
        return tname;
    }

    public void setTname(@NonNull String tname) {
        this.tname = tname;
    }

    @NonNull
    public String getEname() {
        return ename;
    }

    public void setEname(@NonNull String ename) {
        this.ename = ename;
    }

    @NonNull
    public String getType() {
        return type;
    }

    public void setType(@NonNull String type) {
        this.type = type;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public int getAyas() {
        return ayas;
    }

    public void setAyas(int ayas) {
        this.ayas = ayas;
    }

    @NonNull
    @Override
    public String toString() {
        return "Sura{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", tname='" + tname + '\'' +
                ", ename='" + ename + '\'' +
                ", type='" + type + '\'' +
                ", order=" + order +
                ", ayas=" + ayas +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Sura sura = (Sura) o;
        return id == sura.id && order == sura.order && ayas == sura.ayas && name.equals(sura.name) && tname.equals(sura.tname) && ename.equals(sura.ename) && type.equals(sura.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, tname, ename, type, order, ayas);
    }
}
