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
import com.gdevs.wallpaperapp.Activity.MainActivity;
import com.gdevs.wallpaperapp.Activity.ViewPagerWallpaper;
import com.gdevs.wallpaperapp.Config;
import com.gdevs.wallpaperapp.Interface.ItemOnClickListener;
import com.gdevs.wallpaperapp.Models.Wallpaper;
import com.gdevs.wallpaperapp.R;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class WallpaperAdapter extends RecyclerView.Adapter<WallpaperAdapter.CategoryViewHolder> implements Filterable {

    private Context mCtx;
    private List<Wallpaper> wallpaperList;
    private List<Wallpaper> searchList;

    private ItemOnClickListener listener;
    private InterstitialAd mInterstitialAd;

    public WallpaperAdapter(Context mCtx, List<Wallpaper> wallpaperList) {
        this.mCtx = mCtx;
        this.wallpaperList = wallpaperList;
        this.searchList = wallpaperList;
        loadInterstitialAds();
    }

    public void setListener(ItemOnClickListener listener){
        this.listener = listener;
    }

    @Override
    public CategoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mCtx).inflate(R.layout.item_wallpaper_big, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CategoryViewHolder holder, final int position) {

        Wallpaper wallpaper = searchList.get(position);

        Glide.with(mCtx)
                .load(wallpaper.getWallpaper())
                .placeholder(R.drawable.placeholder)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.imageView);

        holder.textView.setText(wallpaper.getName());

        setListener(new ItemOnClickListener() {
            @Override
            public void onClick(View v, int pos) {

                final Intent intshow = new Intent(mCtx, ViewPagerWallpaper.class);
                intshow.putExtra("POSITION", pos);
                intshow.putExtra("array", (Serializable) searchList);
                mCtx.startActivity(intshow);
                showInterstitialAds();

            }
        });

    }

    @Override
    public int getItemCount() {
        return searchList.size();
    }

    class CategoryViewHolder extends RecyclerView.ViewHolder  implements View.OnClickListener{


        ImageView imageView;
        TextView textView;

        public CategoryViewHolder(View itemView) {
            super(itemView);


            imageView = itemView.findViewById(R.id.ivWallpaper);
            textView = itemView.findViewById(R.id.tvWallpaper);
            itemView.setOnClickListener(this);


        }
        @Override
        public void onClick(View v) {
            listener.onClick(v,getAdapterPosition());
        }
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    searchList = wallpaperList;
                } else {
                    ArrayList<Wallpaper> filteredList = new ArrayList<>();
                    for (Wallpaper row : wallpaperList) {
                        if (row.getName().toLowerCase().contains(charString.toLowerCase())||row.getCategory().toLowerCase().contains(charString.toLowerCase())) {
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
                searchList = (ArrayList<Wallpaper>) filterResults.values;
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
