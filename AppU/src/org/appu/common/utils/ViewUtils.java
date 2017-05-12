package org.appu.common.utils;

import android.app.Activity;
import android.view.View;

/**
 * Created by Fynn on 2016/4/26.
 */
public final class ViewUtils {

    public static <E extends View> E findViewById(Activity activity, int resId) {
        return (E) activity.findViewById(resId);
    }

    public static <E extends View> E findViewById(View view, int resId) {
        return (E) view.findViewById(resId);
    }

}
