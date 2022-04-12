package com.gdevs.wallpaperapp.Fragment;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;


import com.gdevs.wallpaperapp.Adapters.WallpaperAdapter;
import com.gdevs.wallpaperapp.Models.Wallpaper;
import com.gdevs.wallpaperapp.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class PopularFragment extends Fragment {


    RecyclerView rvWallpaper;
    DatabaseReference wallpaperReference;
    List<Wallpaper> wallpaperList, favList;
    WallpaperAdapter wallpaperAdapter;
    SwipeRefreshLayout swipeRefreshLayout;
    private int nativeAdPos = 3;

    public PopularFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_home, container, false);

        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        showRefresh(true);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshData(view);
            }
        });

        loadPictures(view);

        return view;
    }

    private void loadPictures(View view) {

        favList = new ArrayList<>();
        wallpaperList = new ArrayList<>();
        rvWallpaper = view.findViewById(R.id.recyclerView);
        rvWallpaper.setHasFixedSize(true);
        final GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(),2);
        rvWallpaper.setLayoutManager(gridLayoutManager);

        wallpaperAdapter = new WallpaperAdapter(getContext(),wallpaperList);
        rvWallpaper.setAdapter(wallpaperAdapter);

        wallpaperReference = FirebaseDatabase.getInstance().getReference("HDWallpaper");

        fetchWallpapers();
        setHasOptionsMenu(true);

    }

    private void fetchWallpapers() {
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
                        if (favList.get(i).getType().equals("image")) {
                            wallpaperList.add(favList.get(i));
                            Collections.shuffle(wallpaperList);
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

    private void refreshData(final View view) {

        wallpaperList.clear();

        wallpaperAdapter.notifyDataSetChanged();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                loadPictures(view);
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
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.main, menu);

        final MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(queryTextListener);
    }

    private SearchView.OnQueryTextListener queryTextListener = new SearchView.OnQueryTextListener() {
        @Override
        public boolean onQueryTextSubmit(String s) {

            return true;
        }

        @Override
        public boolean onQueryTextChange(String s) {
            wallpaperAdapter.getFilter().filter(s);
            return false;
        }
    };
}