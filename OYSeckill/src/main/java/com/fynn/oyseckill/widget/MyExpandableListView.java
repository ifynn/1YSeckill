package com.fynn.oyseckill.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ExpandableListView;

/**
 * Created by Fynn on 2016/9/6.
 */
public class MyExpandableListView extends ExpandableListView {

    public MyExpandableListView(Context context) {
        super(context);
    }

    public MyExpandableListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * Collapse all groups.
     */
    public void collapseAll() {
        int count = getCount();
        for (int i = 0; i < count; i++) {
            collapseGroup(i);
        }
    }

    /**
     * Expand all groups.
     */
    public void expandAll() {
        int count = getCount();
        for (int i = 0; i < count; i++) {
            expandGroup(i);
        }
    }
}
