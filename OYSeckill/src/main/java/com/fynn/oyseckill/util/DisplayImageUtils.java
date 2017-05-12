package com.fynn.oyseckill.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;

import com.fynn.oyseckill.R;

import org.appu.AppU;

import java.io.File;

import cn.bmob.v3.datatype.BmobFile;

/**
 * Created by Fynn on 2016/6/22.
 */
public class DisplayImageUtils {

    public static boolean display(ImageView imageView, String filePath) {
        File file = new File(filePath);
        if (file.exists()) {
            Bitmap bitmap = BitmapFactory.decodeFile(filePath);
            imageView.setImageBitmap(bitmap);
            return true;
        }

        return false;
    }

    public static void displayProfile(ImageView imageView) {
        if (!UserHelper.isLogin()) {
            imageView.setImageResource(R.drawable.icon_user_profile_normal);
            return;
        }

        BmobFile profile = UserHelper.getUser().getProfile();
        if (profile != null) {
            ImageUtils.display(profile.getFileUrl(AppU.app()), imageView,
                    R.drawable.icon_user_profile_normal);
        }
    }
}
