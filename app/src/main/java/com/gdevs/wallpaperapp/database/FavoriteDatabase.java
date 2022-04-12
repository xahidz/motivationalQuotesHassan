package com.gdevs.wallpaperapp.database;


import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.gdevs.wallpaperapp.Models.Wallpaper;


@Database(entities={Wallpaper.class},version = 1)
public abstract class FavoriteDatabase extends RoomDatabase {

    public abstract FavoriteDao favoriteDao();


}
