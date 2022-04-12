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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.gdevs.wallpaperapp.Adapters.CategoryAdapter;
import com.gdevs.wallpaperapp.Models.Category;
import com.gdevs.wallpaperapp.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class CategoryFragment extends Fragment {

    RecyclerView rvCategory;
    DatabaseReference categoryReference;
    List<Category> categoryList;
    CategoryAdapter categoriesAdapter;
    SwipeRefreshLayout swipeRefreshLayout;

    public CategoryFragment() {
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

        loadCategory(view);

        return view;
    }

    private void loadCategory(View view) {

        categoryList = new ArrayList<>();
        rvCategory = view.findViewById(R.id.recyclerView);
        rvCategory.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL,false);
        rvCategory.setLayoutManager(linearLayoutManager);

        categoriesAdapter = new CategoryAdapter(getContext(),categoryList);
        rvCategory.setAdapter(categoriesAdapter);

        categoryReference = FirebaseDatabase.getInstance().getReference("categories");

        fetchCategory();
        setHasOptionsMenu(true);

    }

    private void fetchCategory() {
        categoryReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                showRefresh(false);
                if (dataSnapshot.exists()) {
                    for (DataSnapshot wallpaperSnapshot : dataSnapshot.getChildren()) {

                        String name = wallpaperSnapshot.getKey();
                        String desc = wallpaperSnapshot.child("desc").getValue(String.class);
                        String thumb = wallpaperSnapshot.child("thumbnail").getValue(String.class);

                        Category category = new Category(name, desc, thumb);

                        categoryList.add(0,category);
                    }
                    categoriesAdapter.notifyDataSetChanged();
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void refreshData(final View view) {

        //lyt_no_item.setVisibility(View.GONE);
        categoryList.clear();
        categoriesAdapter.notifyDataSetChanged();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                loadCategory(view);
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
            categoriesAdapter.getFilter().filter(s);
            return false;
        }
    };
}