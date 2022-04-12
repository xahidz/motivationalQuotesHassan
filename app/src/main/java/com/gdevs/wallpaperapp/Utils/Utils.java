package com.gdevs.wallpaperapp.Utils;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.util.Base64;
import android.view.View;
import android.view.Window;

import com.gdevs.wallpaperapp.R;

import java.io.UnsupportedEncodingException;

public class Utils {

    private Context context;

    public Utils(Context context) {
        this.context = context;
    }

    public static void changBGColode(int i, View... viewArr) {
        int i2 = R.drawable.bg_gradient_third;
        if (i == 1) {
            i2 = R.drawable.bg_gradient_first;
        }
        if (i == 2) {
            i2 = R.drawable.bg_gradient_second;
        }
        if (i == 3) {
            i2 = R.drawable.bg_gradient_third;
        }
        if (i == 4) {
            i2 = R.drawable.bg_gradient_fourth;
        }
        if (i == 5) {
            i2 = R.drawable.bg_gradient_fifth;
        }
        for (View view : viewArr) {
            if (view != null) {
                view.setBackgroundResource(i2);
            }
        }
    }


    public void setStatusColor(Window window) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
    }

    public static String decodeString(String encoded) {
        byte[] dataDec = Base64.decode(encoded, Base64.DEFAULT);
        String decodedString = "";
        try {

            decodedString = new String(dataDec, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();

        } finally {

            return decodedString;
        }
    }

    private String encodeString(String s) {
        byte[] data = new byte[0];

        try {
            data = s.getBytes("UTF-8");

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } finally {
            String base64Encoded = Base64.encodeToString(data, Base64.DEFAULT);

            return base64Encoded;

        }
    }
}

