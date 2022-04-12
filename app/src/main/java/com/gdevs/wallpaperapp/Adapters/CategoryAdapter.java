package com.gdevs.wallpaperapp.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.gdevs.wallpaperapp.Activity.WallpaperActivity;
import com.gdevs.wallpaperapp.Config;
import com.gdevs.wallpaperapp.Models.Category;
import com.gdevs.wallpaperapp.R;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;

import java.util.ArrayList;
import java.util.List;


public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> implements Filterable {

    private Context mCtx;
    private List<Category> categoryList;
    private List<Category> searchList;
    private InterstitialAd mInterstitialAd;

    public CategoryAdapter(Context mCtx, List<Category> categoryList) {
        this.mCtx = mCtx;
        this.categoryList = categoryList;
        this.searchList = categoryList;
        loadInterstitialAds();
    }

    @Override
    public CategoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mCtx).inflate(R.layout.item_category, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CategoryViewHolder holder, int position) {
        Category c = searchList.get(position);

        holder.textView.setText(c.name);

        Glide.with(mCtx)
                .load(c.thumb)
                .placeholder(R.drawable.placeholder)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.imageView);

    }

    @Override
    public int getItemCount() {
        return searchList.size();
    }

    class CategoryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView textView;
        ImageView imageView;

        public CategoryViewHolder(View itemView) {
            super(itemView);


            textView = itemView.findViewById(R.id.tvCategory);
            imageView = itemView.findViewById(R.id.ivCategory);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {

            int p = getAdapterPosition();
            Category c = categoryList.get(p);

            Intent intent = new Intent(mCtx, WallpaperActivity.class);
            intent.putExtra(Config.DEVELOPERS_NAME, c.name);

            mCtx.startActivity(intent);
            showInterstitialAds();
        }
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    searchList = categoryList;
                } else {
                    ArrayList<Category> filteredList = new ArrayList<>();
                    for (Category row : categoryList) {
                        if (row.getName().toLowerCase().contains(charString.toLowerCase())||row.getDesc().toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(row);
                        }
                    }
                    searchList = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = searchList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                searchList = (ArrayList<Category>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    private void loadInterstitialAds() {
        AdRequest adRequest = new AdRequest.Builder().build();

        InterstitialAd.load(mCtx, Config.ADMOB_INTER, adRequest,
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
            mInterstitialAd.show((Activity) mCtx);
        } else {
            Log.d("TAG", "The interstitial ad wasn't ready yet.");
        }
    }
}
