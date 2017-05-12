package com.fynn.oyseckill.util;

import android.widget.AbsListView;

import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * Created by Fynn on 16/9/4.
 */
public class PauseOnScrollListener extends
        com.nostra13.universalimageloader.core.listener.PauseOnScrollListener {

    public PauseOnScrollListener(ImageLoader imageLoader, boolean pauseOnScroll, boolean pauseOnFling) {
        super(imageLoader, pauseOnScroll, pauseOnFling);
    }

    public PauseOnScrollListener(ImageLoader imageLoader, boolean pauseOnScroll, boolean pauseOnFling, AbsListView.OnScrollListener customListener) {
        super(imageLoader, pauseOnScroll, pauseOnFling, customListener);
    }

    public PauseOnScrollListener() {
        this(ImageUtils.getInstance().getImageLoader(), true, true);
    }

    public PauseOnScrollListener(AbsListView.OnScrollListener customListener) {
        this(ImageUtils.getInstance().getImageLoader(), true, true, customListener);
    }
}
