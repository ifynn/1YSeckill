package org.appu.view;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Fynn on 2016/3/24.
 */
public interface ViewBinder {

    void bind(Activity activity);
    void bind(View view);
    View bind(Object fragment, LayoutInflater inflater, ViewGroup container);
    void bind(Object object, View view);
}
