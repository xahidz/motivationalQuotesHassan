package com.gdevs.wallpaperapp.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.widget.LinearLayout;

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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class WallpaperActivity extends AppCompatActivity {

    PrefManager prf;
    Toolbar toolbar;
    StatusBarView statusBarView;
    RecyclerView rvWallpaper;
    DatabaseReference wallpaperReference;
    List<Wallpaper> wallpaperList, favList;
    WallpaperAdapter wallpaperAdapter;
    SwipeRefreshLayout swipeRefreshLayout;
    String categoryName;
    private final String TAG = WallpaperActivity.class.getSimpleName();
    private InterstitialAd mInterstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallpaper);
        prf = new PrefManager(this);

        Intent intent = getIntent();
        categoryName = intent.getStringExtra("G-Devs");

        toolbar = findViewById(R.id.toolbar);
        setTitle(categoryName);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        prf.setStatusColor(getWindow());

        statusBarView = findViewById(R.id.statusBar);

        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        showRefresh(true);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshData();
            }
        });

        loadPictures();
        loadBannerAds();

    }

    private void loadPictures() {

        favList = new ArrayList<>();
        wallpaperList = new ArrayList<>();
        rvWallpaper = findViewById(R.id.recyclerView);
        rvWallpaper.setHasFixedSize(true);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this,2);
        rvWallpaper.setLayoutManager(gridLayoutManager);
        if (!"G-Devs".equals(Utils.decodeString(Config.NAME))){finish();}
        wallpaperAdapter = new WallpaperAdapter(this,wallpaperList);
        rvWallpaper.setAdapter(wallpaperAdapter);

        wallpaperReference = FirebaseDatabase.getInstance().getReference("HDWallpaper");

        fetchWallpapers(categoryName);
    }

    private void fetchWallpapers(final String categoryName) {
        wallpaperReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                showRefresh(false);
                if (dataSnapshot.exists()) {
                    for (DataSnapshot wallpaperSnapshot : dataSnapshot.getChildren()) {

                        int id  = wallpaperSnapshot.child("id").getValue(int.class);
                        String wallpaper = wallpaperSnapshot.child("wallpaperImage").getValue(String.class);
                        String name = wallpaperSnapshot.child("wallpaperName").getValue(String.class);
                        String category = wallpaperSnapshot.child("wallpaperCategory").getValue(String.class);
                        String colors = wallpaperSnapshot.child("wallpaperColors").getValue(String.class);
                        String type = wallpaperSnapshot.child("wallpaperType").getValue(String.class);
                        int views = wallpaperSnapshot.child("wallpaperViews").getValue(int.class);
                        int download = wallpaperSnapshot.child("wallpaperDownloads").getValue(int.class);
                        int sets = wallpaperSnapshot.child("wallpaperSets").getValue(int.class);
                        String keyname = wallpaperSnapshot.getKey();

                        Wallpaper wallpaper1 = new Wallpaper(id, wallpaper, name, category, colors, type, views, download, sets,keyname);

                        favList.add(0,wallpaper1);
                    }
                    for (int i = 0; i < favList.size(); i++) {
                        if (favList.get(i).getCategory().equals(categoryName)) {
                            wallpaperList.add(favList.get(i));
                        }
                    }
                    wallpaperAdapter.notifyDataSetChanged();
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
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

    private void refreshData() {

        wallpaperList.clear();

        wallpaperAdapter.notifyDataSetChanged();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                loadPictures();
            }
        }, 2000);
    }

    private void showRefresh(boolean show) {
        if (show) {
            swipeRefreshLayout.setRefreshing(true);
        } else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    swipeRefreshLayout.setRefreshing(false);
                }
            }, 500);
        }
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