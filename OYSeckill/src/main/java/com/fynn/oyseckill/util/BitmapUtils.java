package com.fynn.oyseckill.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

/**
 * Created by Fynn on 2016/8/3.
 */
public class BitmapUtils {

    public static Bitmap uriToBitmap(Uri uri) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 4;
        Bitmap bitmap = BitmapFactory.decodeFile(ImageFileUtils.getRealFilePath(uri), options);
        return bitmap;
    }
}
