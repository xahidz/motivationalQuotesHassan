package com.gdevs.wallpaperapp.Activity;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;


import com.gdevs.wallpaperapp.Config;
import com.gdevs.wallpaperapp.R;
import com.gdevs.wallpaperapp.Utils.PrefManager;
import com.gdevs.wallpaperapp.Utils.StatusBarView;
import com.gdevs.wallpaperapp.Utils.Utils;

public class SettingActivity extends AppCompatActivity {

    LinearLayout linearLayoutTheme;
    LinearLayout linearLayoutPolicyPrivacy;
    PrefManager prf;
    AlertDialog alertDialog1;
    CharSequence[] values = {" Purple "," Green "," Blue "," Red "," Pink "};
    Toolbar toolbar;
    StatusBarView statusBarView;
    Switch switchDarkMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        prf = new PrefManager(this);

        toolbar = findViewById(R.id.toolbar);
        setTitle("App Setting");
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        prf.setStatusColor(getWindow());


        statusBarView = findViewById(R.id.statusBar);
        linearLayoutTheme = findViewById(R.id.linearLayoutTheme);
        linearLayoutPolicyPrivacy = findViewById(R.id.linearLayoutPolicyPrivacy);
        switchDarkMode = findViewById(R.id.switchDarkMode);

        linearLayoutTheme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateAlertDialog();
            }
        });

        linearLayoutPolicyPrivacy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SettingActivity.this,PrivacyPolicyActivity.class));
            }
        });

        //Night Mode
        switchDarkMode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public final void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked){
                    prf.setNightModeState(true);
                    onResume();
                }else {
                    prf.setNightModeState(false);
                    onResume();
                }
            }
        });
    }


    public void CreateAlertDialog(){

        AlertDialog.Builder builder = new AlertDialog.Builder(SettingActivity.this);
        builder.setTitle("Display Wallpaper");
        builder.setSingleChoiceItems(values, -1, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                switch(item)
                {
                    case 0:
                        prf.setInt(Config.HEADER, 1);
                        onResume();
                        reloadCheck(0);
                        break;
                    case 1:
                        prf.setInt(Config.HEADER, 2);
                        onResume();
                        reloadCheck(0);
                        break;
                    case 2:
                        prf.setInt(Config.HEADER, 3);
                        onResume();
                        reloadCheck(0);
                        break;
                    case 3:
                        prf.setInt(Config.HEADER, 4);
                        onResume();
                        reloadCheck(0);
                        break;
                    case 4:
                        prf.setInt(Config.HEADER, 5);
                        onResume();
                        reloadCheck(0);
                        break;
                }
                alertDialog1.dismiss();
            }
        });
        alertDialog1 = builder.create();
        alertDialog1.show();
    }

    private void reloadCheck(int i) {
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
        initCheck();
    }

    private void initCheck() {
        if (prf.loadNightModeState()==true){
            switchDarkMode.setChecked(true);
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }else {
            switchDarkMode.setChecked(false);
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }
}