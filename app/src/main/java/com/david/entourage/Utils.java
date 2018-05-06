package com.david.entourage;

import android.content.Context;
import android.content.res.Resources;
import android.support.design.widget.Snackbar;
import android.util.DisplayMetrics;
import android.widget.Toast;

import com.david.entourage.Application.AppController;
import com.google.android.gms.maps.model.Circle;

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

    public static float getZoomLevel(Circle circle) {
        float zoomLevel = 0;
        if (circle != null){
            double radius = circle.getRadius();
            double scale = radius / 500;
            zoomLevel = (float) (16 - Math.log(scale) / Math.log(2));
        }
        return zoomLevel;
    }
}
