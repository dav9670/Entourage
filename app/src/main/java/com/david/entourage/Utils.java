package com.david.entourage;

import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.widget.Toast;

import com.david.entourage.Application.AppController;

public class Utils {

    public static float convertDpToPixel(float dp, Context context){
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * ((float)metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        return px;
    }

    public static float convertPixelsToDp(float px, Context context){
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float dp = px / ((float)metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        return dp;
    }

    public static void debugMessage(String message) {
        Toast toast = Toast.makeText(AppController.getContext(), message, Toast.LENGTH_LONG);
        toast.show();
    }
}
