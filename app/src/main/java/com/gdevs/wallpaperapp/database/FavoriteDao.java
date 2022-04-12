package com.gdevs.wallpaperapp.database;


import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.gdevs.wallpaperapp.Models.Wallpaper;

import java.util.List;

@Dao
public interface FavoriteDao {
    @Insert
    public void addData (Wallpaper favoriteList);

    @Query("select * from favoritelist")
    public List<Wallpaper> getFavoriteData ();

    @Query("SELECT EXISTS (SELECT 1 FROM favoritelist WHERE id=:id)")
    public int isFavorite (int id);

    @Delete
    public void delete (Wallpaper favoriteList);


}
