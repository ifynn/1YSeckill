package org.appu.view;

import android.app.Activity;
import android.view.View;

/**
 * Created by Fynn on 2016/3/24.
 */
public class ViewFinder {

    private Object obj;

    public ViewFinder(Object obj) {
        this.obj = obj;
    }

    public View finViewById(int id) {
        if (obj instanceof Activity) {
            return ((Activity) obj).findViewById(id);
        } else if (obj instanceof View) {
            return ((View) obj).findViewById(id);
        }
        return null;
    }
}
