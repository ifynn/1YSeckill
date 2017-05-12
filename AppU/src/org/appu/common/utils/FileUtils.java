package org.appu.common.utils;

import android.graphics.Bitmap;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;

/**
 * Created by Fynn on 2016/6/22.
 */
public class FileUtils {

    public static String saveBitmap2File(Bitmap bmp, String filename) {
        Bitmap.CompressFormat format = Bitmap.CompressFormat.JPEG;
        int quality = 100;
        OutputStream stream = null;
        File file = new File(filename);
        try {
            stream = new FileOutputStream(file);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
        boolean success = bmp.compress(format, quality, stream);
        if (success) {
            return file.getPath();
        }
        return null;
    }
}
