package com.gdevs.wallpaperapp.Models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName="favoritelist")
public class Wallpaper implements Serializable {

    @PrimaryKey
    int id;
    @ColumnInfo(name = "image")
    String wallpaper;
    @ColumnInfo(name = "prname")
    String name;
    String category;
    String colors;
    String type;
    int views;
    int download;
    int sets;
    String keyname;

    public Wallpaper () {
    }

    public Wallpaper(int id, String wallpaper, String name, String category, String colors, String type, int views, int download, int sets, String keyname) {
        this.id = id;
        this.wallpaper = wallpaper;
        this.name = name;
        this.category = category;
        this.colors = colors;
        this.type = type;
        this.views = views;
        this.download = download;
        this.sets = sets;
        this.keyname = keyname;
    }

    public int getId () {
        return id;
    }

    public void setId (int id) {
        this.id = id;
    }

    public String getWallpaper () {
        return wallpaper;
    }

    public void setWallpaper (String wallpaper) {
        this.wallpaper = wallpaper;
    }

    public String getName () {
        return name;
    }

    public void setName (String name) {
        this.name = name;
    }

    public String getCategory () {
        return category;
    }

    public void setCategory (String category) {
        this.category = category;
    }

    public String getColors () {
        return colors;
    }

    public void setColors (String colors) {
        this.colors = colors;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getViews () {
        return views;
    }

    public void setViews (int views) {
        this.views = views;
    }

    public int getDownload () {
        return download;
    }

    public void setDownload (int download) {
        this.download = download;
    }

    public int getSets () {
        return sets;
    }

    public void setSets (int sets) {
        this.sets = sets;
    }

    public String getKeyname () {
        return keyname;
    }

    public void setKeyname (String keyname) {
        this.keyname = keyname;
    }
}
