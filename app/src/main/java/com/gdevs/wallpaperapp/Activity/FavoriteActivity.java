package com.gdevs.wallpaperapp.Activity;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;


import com.gdevs.wallpaperapp.Adapters.WallpaperAdapter;
import com.gdevs.wallpaperapp.Config;
import com.gdevs.wallpaperapp.Models.Wallpaper;
import com.gdevs.wallpaperapp.R;
import com.gdevs.wallpaperapp.Utils.PrefManager;
import com.gdevs.wallpaperapp.Utils.StatusBarView;
import com.gdevs.wallpaperapp.Utils.Utils;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;


import java.util.ArrayList;
import java.util.List;

public class FavoriteActivity extends AppCompatActivity {
    private RecyclerView rv;

    private WallpaperAdapter adapter;
    RelativeLayout NoQuotes;
    private ArrayList<Wallpaper> imageArry = new ArrayList<Wallpaper>();
    List<Wallpaper> favoriteLists= MainActivity.favoriteDatabase.favoriteDao().getFavoriteData();
    SwipeRefreshLayout swipeRefreshLayout;
    Toolbar toolbar;
    private final String TAG = FavoriteActivity.class.getSimpleName();
    PrefManager prf;

    StatusBarView statusBarView;
    InterstitialAd mInterstitialAd;

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);

        prf = new PrefManager(this);

        toolbar = findViewById(R.id.toolbar);
        setTitle(R.string.menu_favorite);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        prf.setStatusColor(getWindow());

        statusBarView = findViewById(R.id.statusBar);

        loadPictures();
        loadBannerAds();
    }

    private void loadPictures() {

        NoQuotes = findViewById(R.id.noFavorite);

        rv = findViewById(R.id.recyclerView);
        rv.setHasFixedSize(true);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3);
        rv.setLayoutManager(gridLayoutManager);

        getFavData();

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



    private void getFavData() {

        for (Wallpaper cn : favoriteLists) {

            imageArry.add(cn);
        }

        if (imageArry.isEmpty()){

            NoQuotes.setVisibility(View.VISIBLE);

        }

        adapter=new WallpaperAdapter(this,favoriteLists);
        rv.setAdapter(adapter);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }

        return super.onOptionsItemSelected(item);
    }

    public void onResume() {
        super.onResume();
        PrefManager prefManager = this.prf;
        if (prefManager != null) {
            Utils.changBGColode(prefManager.getInt(Config.HEADER),statusBarView, toolbar);
        }
    }
}