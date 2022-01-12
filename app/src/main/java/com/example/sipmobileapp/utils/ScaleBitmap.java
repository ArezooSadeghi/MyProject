package com.example.sipmobileapp.utils;

import android.graphics.Bitmap;
import android.graphics.Matrix;

public class ScaleBitmap {

    public static Bitmap getScaledDownBitmap(Bitmap bitmap, long threshold) {
        long width = bitmap.getWidth();
        long height = bitmap.getHeight();
        long newWidth = width;
        long newHeight = height;

        if (width > height && width > threshold) {
            newWidth = threshold;
            newHeight = (int) (height * (float) newWidth / width);
        }

        if (width > height && width <= threshold) {
            return bitmap;
        }

        if (width < height && height > threshold) {
            newHeight = threshold;
            newWidth = (int) (width * (float) newHeight / height);
        }

        if (width < height && height <= threshold) {
            return bitmap;
        }

        if (width == height && width > threshold) {
            newWidth = threshold;
            newHeight = newWidth;
        }

        if (width == height && width <= threshold) {
            return bitmap;
        }

        return getResizedBitmap(bitmap, newWidth, newHeight);
    }

    private static Bitmap getResizedBitmap(Bitmap bitmap, long newWidth, long newHeight) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;

        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap scaledBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, false);

        return scaledBitmap;
    }
}
