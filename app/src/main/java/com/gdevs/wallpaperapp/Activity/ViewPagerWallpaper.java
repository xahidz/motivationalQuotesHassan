package com.gdevs.wallpaperapp.Activity;

import android.Manifest;
import android.app.Dialog;
import android.app.WallpaperManager;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;

import com.gdevs.wallpaperapp.BuildConfig;
import com.gdevs.wallpaperapp.Config;
import com.gdevs.wallpaperapp.Models.Wallpaper;
import com.gdevs.wallpaperapp.R;
import com.gdevs.wallpaperapp.Utils.Methods;
import com.gdevs.wallpaperapp.Utils.PrefManager;
import com.gdevs.wallpaperapp.Utils.StatusBarView;
import com.gdevs.wallpaperapp.Utils.Utils;
import com.gdevs.wallpaperapp.common.common;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ViewPagerWallpaper extends AppCompatActivity {


    TextView txtmaintext;
    ImageView imageView;
    private int currentPosition = 0;
    private static int TOTAL_TEXT;
    private Dialog loadingDialog;
    ViewPager viewPager;
    CustomPagerAdapter customPagerAdapter;
    ArrayList<Wallpaper> arrayList;
    LinearLayout fabDownload, fabSet , fabShare, fabFavorite;
    static final int PERMISION_REQUEST_CODE = 1000;
    RelativeLayout rootLayout;
    static final int NUM_OF_THREADS = 4;
    ExecutorService executorService = Executors.newFixedThreadPool(NUM_OF_THREADS);
    ImageView img_favorite;
    TextView tvHD;
    PrefManager prf;
    StatusBarView statusBarView;
    private InterstitialAd mInterstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_wallpaper);

        prf = new PrefManager(this);
        prf.setStatusColor(getWindow());
        statusBarView = findViewById(R.id.statusBar);

        if (Config.VIEW_WALLPAPER_BANNER_ADS){
            loadBannerAds();
        }
        loadInterstitialAds();

        //loading dialog
        loadingDialog = new Dialog(this);
        loadingDialog.setContentView(R.layout.loading);
        loadingDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.side_nav_bar));
        loadingDialog.getWindow().setLayout(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        loadingDialog.setCancelable(false);

        arrayList = new ArrayList<>();

        tvHD = findViewById(R.id.tvHD);

        rootLayout = findViewById(R.id.root_layout);
        viewPager = findViewById(R.id.viewPager);
        customPagerAdapter = new CustomPagerAdapter(ViewPagerWallpaper.this);

        Intent i = getIntent();
        currentPosition = i.getIntExtra("POSITION", 0);
        arrayList = (ArrayList<Wallpaper>) i.getSerializableExtra("array");

        TOTAL_TEXT = (arrayList.size() - 1);
        viewPager.setAdapter(customPagerAdapter);
        viewPager.setCurrentItem(currentPosition, true);
        viewPager.getAdapter().notifyDataSetChanged();
        viewPager.setOffscreenPageLimit(0);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                currentPosition = position;
                changePreviewText(position);
                if (MainActivity.favoriteDatabase.favoriteDao().isFavorite(arrayList.get(currentPosition).getId()) == 1)
                    img_favorite.setImageResource(R.drawable.ic_baseline_favorite);
                else
                    img_favorite.setImageResource(R.drawable.ic_baseline_favorite_border);

            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {
                Log.e("scroll", "PageScrollStateChanged");
            }
        });

        //download Wallpaper
        fabDownload = findViewById(R.id.btnDownload);
        fabDownload.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                if (ActivityCompat.checkSelfPermission(ViewPagerWallpaper.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED){

                    requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},PERMISION_REQUEST_CODE);

                }else{
                    if (arrayList.get(currentPosition).getType().equals("gif")){
                        downloadBitmap();

                    }else {
                        downloadBitmap();

                    }

                }
            }
        });

        //set Wallpaper
        fabSet = findViewById(R.id.btnSetWallpaper);
        fabSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //setWallpaperToScreen();
                if (arrayList.get(currentPosition).getType().equals("gif")){

                    setGif(arrayList.get(currentPosition).getWallpaper());
                    showInterstitialAds();

                }else {
                    showSetWallaperOption();

                }

            }
        });

        //share Wallpaper
        fabShare = findViewById(R.id.btnInfo);
        fabShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (arrayList.get(currentPosition).getType().equals("gif")){
                    shareGifWallpaper();

                }else {
                    shareWallpaper();

                }

            }
        });

        //add Wallpaper to favorite
        fabFavorite = findViewById(R.id.btnFavorite);
        img_favorite = findViewById(R.id.img_favorite);
        fabFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Wallpaper wallpaper = new Wallpaper();
                int id = arrayList.get(currentPosition).getId();
                String name = arrayList.get(currentPosition).getName();
                String image = arrayList.get(currentPosition).getWallpaper();
                wallpaper.setId(id);
                wallpaper.setName(name);
                wallpaper.setWallpaper(image);

                if (MainActivity.favoriteDatabase.favoriteDao().isFavorite(id) != 1) {
                    img_favorite.setImageResource(R.drawable.ic_baseline_favorite);
                    Toast.makeText(ViewPagerWallpaper.this, R.string.add_fav, Toast.LENGTH_SHORT).show();
                    MainActivity.favoriteDatabase.favoriteDao().addData(wallpaper);

                } else {
                    img_favorite.setImageResource(R.drawable.ic_baseline_favorite_border);
                    Toast.makeText(ViewPagerWallpaper.this, R.string.remove_fav, Toast.LENGTH_SHORT).show();
                    MainActivity.favoriteDatabase.favoriteDao().delete(wallpaper);
                }
                showInterstitialAds();
            }
        });

    }

    private void downloadBitmap() {

        loadingDialog.show();

        Glide.with(this)
                .asBitmap()
                .load(arrayList.get(currentPosition).getWallpaper())
                .override(common.WIDTH,common.HEIGHT)
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        saveBitmap(resource);
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {

                    }
                });
    }

    //set gif as wallpaper
    public void setGif(final String str) {

        Glide.with(this)
                .download(str.replace(" ", "%20"))
                .listener(new RequestListener<File>() {
                    @Override
                    public boolean onLoadFailed(GlideException glideException, Object obj, Target<File> target, boolean z) {
                        return false;
                    }

                    public boolean onResourceReady(File file, Object obj, Target<File> target, DataSource dataSource, boolean z) {
                        try {

                            Methods.setGifWallpaper(ViewPagerWallpaper.this, Methods.getBytesFromFile(file), Methods.createName(str), "image/gif");

                            return true;
                        } catch (IOException e) {
                            e.printStackTrace();
                            return true;
                        }
                    }
                }).submit();
    }

    private void saveBitmap(Bitmap bitmap) {
        OutputStream fos;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {


            ContentResolver resolver = getContentResolver();
            ContentValues contentValues = new ContentValues();
            contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, System.currentTimeMillis() + ".jpg");
            contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg");
            contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES);
            Uri imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);

            Toast.makeText(ViewPagerWallpaper.this, "File Saved Successfully", Toast.LENGTH_SHORT).show();
            Snackbar.make(rootLayout, "Downlaod Successfull", Snackbar.LENGTH_LONG).show();
            try {
                fos = resolver.openOutputStream(Objects.requireNonNull(imageUri));
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);

                fos.flush();
                fos.close();


            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
        String fileName  = UUID.randomUUID()+".jpg";
        String path = Environment.getExternalStorageDirectory().toString();
        File folder = new File(path+"/"+ getString(R.string.app_name));
        folder.mkdir();

        File file = new File(folder,fileName);
        if (file.exists())
            file.delete();

        try {

            FileOutputStream outputStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG,100,outputStream);
            outputStream.flush();
            outputStream.close();

            //send pictures to gallery
            sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)));

            Snackbar.make(rootLayout,"Downlaod Successfull", Snackbar.LENGTH_LONG).show();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }}

        loadingDialog.dismiss();
    }


    //set as wallpaper
    private void setWallpaperToScreen() {
        loadingDialog.show();

        Glide.with(this)
                .asBitmap()
                .load(arrayList.get(currentPosition).getWallpaper())
                .override(common.WIDTH,common.HEIGHT)
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {

                        WallpaperManager wallpaperManager = WallpaperManager.getInstance(ViewPagerWallpaper.this);
                        try {
                            wallpaperManager.setBitmap(resource);

                            Snackbar.make(rootLayout,"Wallpaper was set", Snackbar.LENGTH_LONG).show();

                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        loadingDialog.dismiss();

                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {

                    }
                });
        showInterstitialAds();
    }

    //set wallpaper on Home Screen
    private void setWallpaperToHomeScreen() {
        loadingDialog.show();

        Glide.with(this)
                .asBitmap()
                .load(arrayList.get(currentPosition).getWallpaper())
                .override(common.WIDTH,common.HEIGHT)
                .into(new CustomTarget<Bitmap>() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {

                        WallpaperManager wallpaperManager = WallpaperManager.getInstance(ViewPagerWallpaper.this);
                        try {
                            wallpaperManager.setBitmap(resource,null, true, WallpaperManager.FLAG_SYSTEM);

                            Snackbar.make(rootLayout,"Wallpaper was set", Snackbar.LENGTH_LONG).show();

                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        loadingDialog.dismiss();

                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {

                    }
                });
        showInterstitialAds();
    }

    //set wallpaper on Lock Screen
    private void setWallpaperToLockScreen() {
        loadingDialog.show();

        Glide.with(this)
                .asBitmap()
                .load(arrayList.get(currentPosition).getWallpaper())
                .override(common.WIDTH,common.HEIGHT)
                .into(new CustomTarget<Bitmap>() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {

                        WallpaperManager wallpaperManager = WallpaperManager.getInstance(ViewPagerWallpaper.this);
                        try {
                            wallpaperManager.setBitmap(resource,null, true, WallpaperManager.FLAG_LOCK);

                            Snackbar.make(rootLayout,"Wallpaper was set", Snackbar.LENGTH_LONG).show();

                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        loadingDialog.dismiss();

                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {

                    }
                });
    }

    //set crop wallpaper
    private void setCropWallpaper() {
        Intent intent = new Intent(getApplicationContext(), ActivitySetAsWallpaper.class);
        intent.putExtra("WALLPAPER_IMAGE_URL", arrayList.get(currentPosition).getWallpaper());
        startActivity(intent);
    }

    //share wallpaper code
    private void shareWallpaper() {

        Glide.with(this)
                .asBitmap()
                .load(arrayList.get(currentPosition).getWallpaper())
                .into(new SimpleTarget<Bitmap>() {
                          @Override
                          public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {

                              Intent intent = new Intent(Intent.ACTION_SEND);
                              intent.setType("image/*");
                              String shareBodyText = "https://play.google.com/store/apps/details?id="+getPackageName();
                              intent.putExtra(Intent.EXTRA_STREAM, getLocalBitmapUri(resource));
                              intent.putExtra(Intent.EXTRA_SUBJECT,getString(R.string.app_name));
                              intent.putExtra(Intent.EXTRA_TEXT,shareBodyText);

                              startActivity(Intent.createChooser(intent, "Wallpapers Hub"));
                          }
                      }
                );
    }



    //share wallpaper code
    private void shareGifWallpaper() {

        Glide.with(this)
                .asBitmap()
                .load(arrayList.get(currentPosition).getWallpaper())
                .into(new SimpleTarget<Bitmap>() {
                          @Override
                          public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {

                              Intent intent = new Intent(Intent.ACTION_SEND);
                              intent.setType("image/gif");
                              String shareBodyText = "https://play.google.com/store/apps/details?id="+getPackageName();
                              intent.putExtra(Intent.EXTRA_STREAM, getLocalBitmapUri(resource));
                              intent.putExtra(Intent.EXTRA_SUBJECT,getString(R.string.app_name));
                              intent.putExtra(Intent.EXTRA_TEXT,shareBodyText);

                              startActivity(Intent.createChooser(intent, "Wallpapers"));
                          }
                      }
                );
    }

    //help to share as image
    private Uri getLocalBitmapUri(Bitmap bmp) {
        Uri bmpUri = null;
        try {
            File file = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                    "wallpaper" + System.currentTimeMillis() + ".png");
            FileOutputStream out = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.close();
            bmpUri = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".provider", file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bmpUri;
    }


    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        try {
            return super.onTouchEvent(ev);
        } catch (IllegalArgumentException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    class CustomPagerAdapter extends PagerAdapter {

        Context mContext;
        LayoutInflater mLayoutInflater;

        CustomPagerAdapter(Context context) {
            mContext = context;
            mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return arrayList.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            View itemView = mLayoutInflater.inflate(R.layout.layout_viewpager, container, false);

            LinearLayout ll = itemView.findViewById(R.id.ll_viewpager);
            imageView = itemView.findViewById(R.id.iv_full);


            Glide.with(ViewPagerWallpaper.this)
                    .load(arrayList.get(position).getWallpaper())
                    .placeholder(R.drawable.placeholder)
                    .into(imageView);

            container.addView(ll, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);

            return ll;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }

    public void changePreviewText(int position) {
        currentPosition = position;
        Log.d("Main", "Current position: " + position);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showSetWallaperOption() {
        final Dialog dialog = new Dialog(ViewPagerWallpaper.this, R.style.DialogCustomTheme);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        dialog.setContentView(R.layout.layout_option);

        LinearLayout llSetWallpaper = dialog.findViewById(R.id.llSetWallpaper);
        LinearLayout llCropWallpaper = dialog.findViewById(R.id.llCropWallpaper);
        llSetWallpaper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showScreenOption();
                dialog.dismiss();
            }
        });
        llCropWallpaper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setCropWallpaper();
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void showScreenOption() {
        final Dialog dialog = new Dialog(ViewPagerWallpaper.this, R.style.DialogCustomTheme);
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
            }
        });
        llLock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setWallpaperToLockScreen();
                dialog.dismiss();
            }
        });
        llBoth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setWallpaperToScreen();
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public void onResume() {
        super.onResume();
        PrefManager prefManager = this.prf;
        if (prefManager != null) {
            Utils.changBGColode(prefManager.getInt(Config.HEADER),statusBarView);
        }
        if (!Config.DEVELOPERS_NAME.equals(Utils.decodeString(Config.NAME))){
            finish();
        }
    }

    private void loadBannerAds() {
        AdView adView = new AdView(this);
        adView.setAdUnitId(Config.ADMOB_BANNER);
        adView.setAdSize(AdSize.BANNER);
        LinearLayout layout = (LinearLayout) findViewById(R.id.adView);
        layout.addView(adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
    }

    private void loadInterstitialAds() {
        AdRequest adRequest = new AdRequest.Builder().build();

        InterstitialAd.load(this,Config.ADMOB_INTER, adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        // The mInterstitialAd reference will be null until
                        // an ad is loaded.
                        mInterstitialAd = interstitialAd;
                        Log.i("TAG", "onAdLoaded");
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error
                        Log.i("TAG", loadAdError.getMessage());
                        mInterstitialAd = null;
                    }
                });
    }

    private void showInterstitialAds() {
        if (mInterstitialAd != null) {
            mInterstitialAd.show(ViewPagerWallpaper.this);
        } else {
            Log.d("TAG", "The interstitial ad wasn't ready yet.");
        }
    }
} 