package com.gdevs.wallpaperapp.Activity;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.gdevs.wallpaperapp.Config;
import com.gdevs.wallpaperapp.Fragment.CategoryFragment;
import com.gdevs.wallpaperapp.Fragment.GifsFragment;
import com.gdevs.wallpaperapp.Fragment.LatestFragment;
import com.gdevs.wallpaperapp.Fragment.PopularFragment;
import com.gdevs.wallpaperapp.Fragment.RandomFragment;
import com.gdevs.wallpaperapp.R;
import com.gdevs.wallpaperapp.Utils.PrefManager;
import com.gdevs.wallpaperapp.Utils.StatusBarView;
import com.gdevs.wallpaperapp.Utils.Utils;
import com.gdevs.wallpaperapp.database.FavoriteDatabase;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.onesignal.OneSignal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentTransaction;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.room.Room;
import androidx.viewpager.widget.ViewPager;

import java.util.ArrayList;
import java.util.List;

public class
MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    DrawerLayout drawerLayout;
    Toolbar toolbar;
    NavigationView navigationView;
    ActionBarDrawerToggle actionBarDrawerToggle;
    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    public static FavoriteDatabase favoriteDatabase;
    PrefManager prf;
    StatusBarView statusBarView;
    RelativeLayout headerBackground;
    private InterstitialAd mInterstitialAd;


    private static final String ONESIGNAL_APP_ID = "d84871d1-6132-40dd-9236-1356b2762756";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        prf = new PrefManager(this);

        // OneSignal Initialization
        OneSignal.initWithContext(this);
        OneSignal.setAppId(ONESIGNAL_APP_ID);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(getResources().getColor(R.color.white));
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR); //For setting material color into black of the navigation bar
        }

        loadBannerAds();
        loadInterstitialAds();

        //statusBarView = findViewById(R.id.statusBar);
        headerBackground = findViewById(R.id.headerBackground);

        favoriteDatabase= Room.databaseBuilder(getApplicationContext(), FavoriteDatabase.class,"myfavdb").allowMainThreadQueries().build();

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setItemIconTintList(null);

        View headerView = navigationView.getHeaderView(0);
        headerBackground = headerView.findViewById(R.id.headerBackground);

        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.setDrawerIndicatorEnabled(true);
        actionBarDrawerToggle.syncState();

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        this.viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(this.viewPager);
        tabLayout.setupWithViewPager(this.viewPager);

        tabLayout.getTabAt(0).setIcon((int) R.drawable.ic_baseline_explore);
        tabLayout.getTabAt(1).setIcon((int) R.drawable.ic_baseline_videocam);
        tabLayout.getTabAt(2).setIcon((int) R.drawable.ic_baseline_random);
        tabLayout.getTabAt(3).setIcon((int) R.drawable.ic_baseline_popular);
        tabLayout.getTabAt(4).setIcon((int) R.drawable.ic_baseline_category);

        tabLayout.getTabAt(0).getIcon().setColorFilter(Color.parseColor("#FFFFFF"), PorterDuff.Mode.SRC_IN);
        tabLayout.getTabAt(1).getIcon().setColorFilter(Color.parseColor("#86FFFFFF"), PorterDuff.Mode.SRC_IN);
        tabLayout.getTabAt(2).getIcon().setColorFilter(Color.parseColor("#86FFFFFF"), PorterDuff.Mode.SRC_IN);
        tabLayout.getTabAt(3).getIcon().setColorFilter(Color.parseColor("#86FFFFFF"), PorterDuff.Mode.SRC_IN);
        tabLayout.getTabAt(4).getIcon().setColorFilter(Color.parseColor("#86FFFFFF"), PorterDuff.Mode.SRC_IN);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
                tab.getIcon().setColorFilter(Color.parseColor("#FFFFFF"), PorterDuff.Mode.SRC_IN);
            }

            public void onTabUnselected(TabLayout.Tab tab) {
                tab.getIcon().setColorFilter(Color.parseColor("#86FFFFFF"), PorterDuff.Mode.SRC_IN);
            }

            public void onTabReselected(TabLayout.Tab tab) {
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
            mInterstitialAd.show(MainActivity.this);
        } else {
            Log.d("TAG", "The interstitial ad wasn't ready yet.");
        }
    }

    private void setupViewPager(ViewPager viewPager2) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new LatestFragment(), "Latest");
        adapter.addFragment(new GifsFragment(), "Gifs");
        adapter.addFragment(new RandomFragment(), "Random");
        adapter.addFragment(new PopularFragment(), "Popular");
        adapter.addFragment(new CategoryFragment(), "Category");
        viewPager2.setOffscreenPageLimit(2);
        viewPager2.setAdapter(adapter);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList();
        private final List<String> mFragmentTitleList = new ArrayList();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        public Fragment getItem(int position) {
            return this.mFragmentList.get(position);
        }

        public int getCount() {
            return this.mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            this.mFragmentList.add(fragment);
            this.mFragmentTitleList.add(title);
        }

        public CharSequence getPageTitle(int position) {
            return this.mFragmentTitleList.get(position);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        drawerLayout.closeDrawer(GravityCompat.START);
        if (menuItem.getItemId() == R.id.nav_category){
            //startActivity(new Intent(MainActivity.this,CategoryActivity.class));
        }
        if (menuItem.getItemId() == R.id.nav_favorite){
            startActivity(new Intent(MainActivity.this,FavoriteActivity.class));
            showInterstitialAds();
        }
        if (menuItem.getItemId() == R.id.nav_twitter){
            Intent browserIntent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://www.twitter.com"));
            startActivity(browserIntent);
        }
        if (menuItem.getItemId() == R.id.nav_facebook){
            Intent browserIntent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://www.facebook.com"));
            startActivity(browserIntent);
        }
        if (menuItem.getItemId() == R.id.nav_instagram){
            Intent browserIntent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://www.instagram.com"));
            startActivity(browserIntent);
        }
        if (menuItem.getItemId() == R.id.nav_share){
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            String shareBodyText = "https://play.google.com/store/apps/details?id="+getPackageName();
            intent.putExtra(Intent.EXTRA_SUBJECT,getString(R.string.app_name));
            intent.putExtra(Intent.EXTRA_TEXT,shareBodyText);
            startActivity(Intent.createChooser(intent,"share via"));

        }
        if (menuItem.getItemId() == R.id.nav_rate){
            try {

                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id="+getPackageName())));
            }catch (ActivityNotFoundException ex){
                startActivity(new Intent(Intent.ACTION_VIEW,Uri.parse("https://play.google.com/store/apps/details?id="+getPackageName())));
            }
        }
        if (menuItem.getItemId() == R.id.nav_about){
            aboutDialog();
        }
        if (menuItem.getItemId() == R.id.nav_setting){
            startActivity(new Intent(MainActivity.this,SettingActivity.class));
            showInterstitialAds();
        }
        return false;
    }

    @SuppressLint("SetTextI18n")
    public void aboutDialog() {
        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(MainActivity.this);
        View view = layoutInflaterAndroid.inflate(R.layout.dialog_about, null);

        TextView txt_app_version = view.findViewById(R.id.txt_app_version);
        txt_app_version.setText(getString(R.string.msg_about_version) + " " + "1");

        final AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
        alert.setView(view);
        alert.setPositiveButton(R.string.dialog_option_ok, (dialog, which) -> dialog.dismiss());
        alert.show();
    }

    public void onResume() {
        super.onResume();
        PrefManager prefManager = this.prf;
        if (prefManager != null) {
            Utils.changBGColode(prefManager.getInt(Config.HEADER), headerBackground,tabLayout,toolbar);
        }
        initcheck();
    }

    private void initcheck() {
        Window window = getWindow();
        // clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        if (!Config.DEVELOPERS_NAME.equals(Utils.decodeString(Config.NAME))){
            finish();
        }

        switch (prf.getInt(Config.HEADER)) {
            case 1:
                window.setStatusBarColor(getResources().getColor(R.color.purpleDark));
                break;
            case 2:
                window.setStatusBarColor(getResources().getColor(R.color.greenDark));
                break;
            case 3:
                window.setStatusBarColor(getResources().getColor(R.color.blueDark));
                break;
            case 4:
                window.setStatusBarColor(getResources().getColor(R.color.redDark));
                break;
            case 5:
                window.setStatusBarColor(getResources().getColor(R.color.pinkDark));
                break;

        }
    }

}