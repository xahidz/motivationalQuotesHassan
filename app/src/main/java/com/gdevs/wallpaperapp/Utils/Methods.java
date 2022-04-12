package com.gdevs.wallpaperapp.Utils;

import android.app.WallpaperManager;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.CharacterIterator;
import java.text.DecimalFormat;
import java.text.StringCharacterIterator;
import android.os.StrictMode.VmPolicy.Builder;

import com.gdevs.wallpaperapp.R;


public class Methods {

    private Context context;

    // constructor
    public Methods (Context context) {
        this.context = context;
    }

    public static String createName(String str) {
        return str.substring(str.lastIndexOf(47) + 1);
    }

    public static byte[] getBytesFromFile(File file) throws IOException {
        long length = file.length();
        if (length <= 2147483647L) {
            int i = (int) length;
            byte[] bArr = new byte[i];
            int i2 = 0;
            FileInputStream fileInputStream = new FileInputStream(file);
            while (i2 < i) {
                try {
                    int read = fileInputStream.read(bArr, i2, i - i2);
                    if (read < 0) {
                        break;
                    }
                    i2 += read;
                } catch (Throwable th) {
                    fileInputStream.close();
                    throw th;
                }
            }
            fileInputStream.close();
            if (i2 >= i) {
                return bArr;
            }
            throw new IOException("Could not completely read file " + file.getName());
        }
        throw new IOException("File is too large!");
    }


    public static void setGifWallpaper(Context context2, byte[] bArr, String str, String str2) {
        PrefManager sharedPref = new PrefManager(context2);
        try {
            File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), context2.getString(R.string.app_name));
            if (!file.exists()) {
                file.mkdirs();
            }
            if (file.exists()) {
                File file2 = new File(file, str);
                FileOutputStream fileOutputStream = new FileOutputStream(file2);
                fileOutputStream.write(bArr);
                fileOutputStream.flush();
                fileOutputStream.close();
                ContentValues contentValues = new ContentValues();
                //contentValues.put(OneSignalDbContract.NotificationTable.COLUMN_NAME_TITLE, file2.getName());
                contentValues.put("_display_name", file2.getName());
                contentValues.put("description", "");
                contentValues.put("mime_type", str2);
                contentValues.put("date_added", Long.valueOf(System.currentTimeMillis()));
                contentValues.put("datetaken", Long.valueOf(System.currentTimeMillis()));
                contentValues.put("_data", file2.getAbsolutePath());
                context2.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
                Constant.gifPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/" + context2.getString(R.string.app_name);
                Constant.gifName = file2.getName();
                try {
                    WallpaperManager.getInstance(context2).clear();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Intent intent = new Intent("android.service.wallpaper.CHANGE_LIVE_WALLPAPER");
                intent.putExtra("android.service.wallpaper.extra.LIVE_WALLPAPER_COMPONENT", new ComponentName(context2, SetGIFAsWallpaperService.class));
                context2.startActivity(intent);
                sharedPref.saveGif(Constant.gifPath, Constant.gifName);
                Log.d("GIF_PATH", Constant.gifPath);
                Log.d("GIF_NAME", Constant.gifName);
            }
        } catch (Exception e2) {
            e2.printStackTrace();
        }
    }
}
