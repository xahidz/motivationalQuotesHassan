package com.gdevs.wallpaperapp.Activity;


import android.app.Dialog;
import android.app.WallpaperManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.canhub.cropper.CropImageView;
import com.gdevs.wallpaperapp.R;
import com.google.android.material.snackbar.Snackbar;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;


import com.canhub.cropper.CropImage;
import com.canhub.cropper.CropImageActivity;

import java.io.IOException;

public class ActivitySetAsWallpaper extends AppCompatActivity {

    //private CropImageView mCropImageView;
    String str_image;
    Toolbar toolbar;
    Bitmap bitmap = null;
    RelativeLayout rootLayout;
    private Dialog loadingDialog;
    CropImageView mCropImageView;

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_as_wallpaper);

        Intent i = getIntent();
        str_image = i.getStringExtra("WALLPAPER_IMAGE_URL");

        toolbar = findViewById(R.id.toolbar);
        setTitle("");
        setSupportActionBar(toolbar);

        rootLayout = findViewById(R.id.root_layout);

        //loading dialog
        loadingDialog = new Dialog(this);
        loadingDialog.setContentView(R.layout.loading);
        loadingDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.side_nav_bar));
        loadingDialog.getWindow().setLayout(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        loadingDialog.setCancelable(false);

        mCropImageView = (CropImageView) findViewById(R.id.CropImageView);

        ImageLoader.getInstance().init(ImageLoaderConfiguration.createDefault(getApplicationContext()));
        ImageLoader.getInstance().loadImage(str_image, new ImageLoadingListener() {

            @Override
            public void onLoadingStarted(String arg0, View arg1) {
            }

            @Override
            public void onLoadingFailed(String arg0, View arg1, FailReason arg2) {
            }

            @Override
            public void onLoadingComplete(String arg0, View arg1, Bitmap arg2) {
                mCropImageView.setImageBitmap(arg2);
            }

            @Override
            public void onLoadingCancelled(String arg0, View arg1) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_detail, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;

            case R.id.menu_set:
                showScreenOption();
                break;

            default:
                return super.onOptionsItemSelected(menuItem);
        }
        return true;
    }

    private void showScreenOption() {
        final Dialog dialog = new Dialog(ActivitySetAsWallpaper.this, R.style.DialogCustomTheme);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        dialog.setContentView(R.layout.layout_set);

        LinearLayout llHome = dialog.findViewById(R.id.llHome);
        LinearLayout llLock = dialog.findViewById(R.id.llLock);
        LinearLayout llBoth = dialog.findViewById(R.id.llBoth);
        llHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setWallpaperToHomeScreen();
                dialog.dismiss();
                closeApp();
            }
        });
        llLock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setWallpaperToLockScreen();
                dialog.dismiss();
                closeApp();
            }
        });
        llBoth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setWallpaperToScreen();
                dialog.dismiss();
                closeApp();
            }
        });
        dialog.show();
    }

    //set as wallpaper
    private void setWallpaperToScreen() {
        loadingDialog.show();

        bitmap = mCropImageView.getCroppedImage();


        WallpaperManager wallpaperManager = WallpaperManager.getInstance(ActivitySetAsWallpaper.this);
        try {
            wallpaperManager.setBitmap(bitmap);

            Snackbar.make(rootLayout,"Wallpaper was set", Snackbar.LENGTH_LONG).show();

        } catch (IOException e) {
            e.printStackTrace();
        }

        loadingDialog.dismiss();
    }

    //set wallpaper on Home Screen
    private void setWallpaperToHomeScreen() {
        loadingDialog.show();

      bitmap = mCropImageView.getCroppedImage();


        WallpaperManager wallpaperManager = WallpaperManager.getInstance(ActivitySetAsWallpaper.this);
        try {
            wallpaperManager.setBitmap(bitmap);

            Snackbar.make(rootLayout,"Wallpaper was set", Snackbar.LENGTH_LONG).show();

        } catch (IOException e) {
            e.printStackTrace();
        }

        loadingDialog.dismiss();
    }

    //set wallpaper on Lock Screen
    private void setWallpaperToLockScreen() {
        loadingDialog.show();

       bitmap = mCropImageView.getCroppedImage();


        WallpaperManager wallpaperManager = WallpaperManager.getInstance(ActivitySetAsWallpaper.this);
        try {
            wallpaperManager.setBitmap(bitmap);

            Snackbar.make(rootLayout,"Wallpaper was set", Snackbar.LENGTH_LONG).show();

        } catch (IOException e) {
            e.printStackTrace();
        }

        loadingDialog.dismiss();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {

            Uri result = CropImage.getPickImageResultUriContent(ActivitySetAsWallpaper.this, data);
            if (resultCode == RESULT_OK) {
               //ri resultUri = result.getUriContent();
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
              //Exception error = result.getError();
            }
        }
    }

    private void closeApp() {
        Intent intent = new Intent(ActivitySetAsWallpaper.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        Toast.makeText(ActivitySetAsWallpaper.this, "Wallpaper Set Successfully", Toast.LENGTH_SHORT).show();
    }
}