package com.gdevs.wallpaperapp.Utils;

import android.graphics.Canvas;
import android.graphics.Movie;
import android.os.Handler;
import android.os.Looper;
import android.service.wallpaper.WallpaperService;
import android.view.SurfaceHolder;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class SetGIFAsWallpaperService extends WallpaperService {

    public class GIFWallpaperEngine extends Engine {
        private String LOCAL_GIF = "default_image.gif";
        private Runnable drawGIF = new Runnable() {

            public final void run() {
                GIFWallpaperEngine.this.draw();
            }
        };
        private Handler handler = new Handler(Looper.getMainLooper());
        private SurfaceHolder holder;
        private int mMovieHeight;
        private int mMovieWidth;
        private int mSurfaceHeight;
        private int mSurfaceWidth;
        private Movie movie;
        private float scaleRatio;
        private boolean visible;
        private float x;
        private float y;

        public GIFWallpaperEngine() {
            super();
        }

        public void onCreate(SurfaceHolder surfaceHolder) {
            super.onCreate(surfaceHolder);
            this.holder = surfaceHolder;
        }

        public void onDestroy() {
            super.onDestroy();
            this.handler.removeCallbacks(this.drawGIF);
        }

        public void onSurfaceCreated(SurfaceHolder surfaceHolder) {
            super.onSurfaceCreated(surfaceHolder);
            try {
                BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(new File(Constant.gifPath, Constant.gifName)), 16384);
                bufferedInputStream.mark(16384);
                this.movie = Movie.decodeStream(bufferedInputStream);
            } catch (FileNotFoundException e) {
                try {
                    PrefManager sharedPref = new PrefManager(SetGIFAsWallpaperService.this.getApplicationContext());
                    BufferedInputStream bufferedInputStream2 = new BufferedInputStream(new FileInputStream(new File(sharedPref.getPath() + "/" + sharedPref.getGifName())), 16384);
                    bufferedInputStream2.mark(16384);
                    this.movie = Movie.decodeStream(bufferedInputStream2);
                } catch (FileNotFoundException e2) {
                    e2.printStackTrace();
                    try {
                        this.movie = Movie.decodeStream(SetGIFAsWallpaperService.this.getResources().getAssets().open(this.LOCAL_GIF));
                    } catch (IOException e3) {
                        e3.printStackTrace();
                    }
                }
                e.printStackTrace();
            }
        }

        public void onSurfaceChanged(SurfaceHolder surfaceHolder, int i, int i2, int i3) {
            super.onSurfaceChanged(surfaceHolder, i, i2, i3);
            super.onSurfaceChanged(surfaceHolder, i, i2, i3);
            this.mSurfaceWidth = i2;
            this.mSurfaceHeight = i3;
            this.mMovieWidth = this.movie.width();
            int height = this.movie.height();
            this.mMovieHeight = height;
            int i4 = this.mSurfaceWidth;
            int i5 = this.mMovieWidth;
            float f = ((float) i4) / ((float) i5);
            int i6 = this.mSurfaceHeight;
            if (f > ((float) i6) / ((float) height)) {
                this.scaleRatio = ((float) i4) / ((float) i5);
            } else {
                this.scaleRatio = ((float) i6) / ((float) height);
            }
            float f2 = (float) i5;
            float f3 = this.scaleRatio;
            float f4 = (((float) i4) - (f2 * f3)) / 2.0f;
            this.x = f4;
            float f5 = (((float) i6) - (((float) height) * f3)) / 2.0f;
            this.y = f5;
            this.x = f4 / f3;
            this.y = f5 / f3;
        }

        public void onSurfaceDestroyed(SurfaceHolder surfaceHolder) {
            super.onSurfaceDestroyed(surfaceHolder);
        }

        public void draw() {
            if (this.visible) {
                Canvas lockCanvas = this.holder.lockCanvas();
                lockCanvas.save();
                float f = this.scaleRatio;
                lockCanvas.scale(f, f);
                this.movie.draw(lockCanvas, this.x, this.y);
                lockCanvas.restore();
                this.holder.unlockCanvasAndPost(lockCanvas);
                this.movie.setTime((int) (System.currentTimeMillis() % ((long) this.movie.duration())));
                this.handler.removeCallbacks(this.drawGIF);
                this.handler.postDelayed(this.drawGIF, 0);
            }
        }

        public void onVisibilityChanged(boolean z) {
            this.visible = z;
            if (z) {
                this.handler.post(this.drawGIF);
            } else {
                this.handler.removeCallbacks(this.drawGIF);
            }
        }
    }

    public Engine onCreateEngine() {
        return new GIFWallpaperEngine();
    }

    public void onDestroy() {
        super.onDestroy();
    }
}