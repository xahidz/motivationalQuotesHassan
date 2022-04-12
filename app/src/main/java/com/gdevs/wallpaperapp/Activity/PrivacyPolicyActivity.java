package com.gdevs.wallpaperapp.Activity;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;


import com.gdevs.wallpaperapp.Config;
import com.gdevs.wallpaperapp.R;
import com.gdevs.wallpaperapp.Utils.PrefManager;
import com.gdevs.wallpaperapp.Utils.StatusBarView;
import com.gdevs.wallpaperapp.Utils.Utils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class PrivacyPolicyActivity extends AppCompatActivity {

    TextView privacyPolicy;
    private DatabaseReference dbCategories , quote;
    String privacy_Policy;
    PrefManager prf;
    Toolbar toolbar;
    StatusBarView statusBarView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy_policy);

        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.policy_privacy);
        setSupportActionBar(toolbar);
        toolbar.setTitle(R.string.policy_privacy);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        prf = new PrefManager(this);
        prf.setStatusColor(getWindow());


        privacyPolicy = findViewById(R.id.policy_text);
        statusBarView = findViewById(R.id.statusBar);


        quote = FirebaseDatabase.getInstance().getReference("policy");
        quote.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        String name = ds.getKey();

                        privacy_Policy = ds.child("desc").getValue(String.class);

                        privacyPolicy.setText(privacy_Policy);


                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
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
