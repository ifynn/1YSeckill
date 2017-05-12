package com.fynn.oyseckill.util;

import android.graphics.Bitmap;
import android.os.Build;
import android.os.Environment;
import android.widget.ImageView;

import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiskCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.UsingFreqLimitedMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.imageaware.ImageAware;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.utils.DiskCacheUtils;
import com.nostra13.universalimageloader.utils.MemoryCacheUtils;

import org.appu.AppU;
import org.appu.common.utils.LogU;

import java.io.File;
import java.util.List;

/**
 * Created by Fynn on 2016/7/6.
 */
public class ImageUtils {

    private static volatile ImageUtils imageUtils;
    private static volatile ImageLoader imageLoader;
    private static DisplayImageOptions.Builder dftOptionsBuilder;

    private ImageUtils() {
        imageLoader = ImageLoader.getInstance();
        initImageLoader();
    }

    public static ImageUtils getInstance() {
        if (imageUtils == null) {
            synchronized (ImageUtils.class) {
                if (imageUtils == null) {
                    imageUtils = new ImageUtils();
                }
            }
        }
        return imageUtils;
    }

    public static void display(String url, ImageView imageView) {
        display(url, imageView, null);
    }

    public static void display(String url, ImageView imageView, ImageLoadingListener listener) {
        display(url, imageView, 0, 0, listener, false);
    }

    public static void display(String url, ImageView imageView, int emptyImageResId) {
        display(url, imageView, emptyImageResId, emptyImageResId, null, false);
    }

    public static void displayOriginal(String url, ImageView imageView) {
        displayOriginal(url, imageView, 0);
    }

    public static void displayOriginal(String url, ImageView imageView, int emptyImageResId) {
        display(url, imageView, emptyImageResId, emptyImageResId, null, true);
    }

    public static void displayOriginal(String url, ImageView imageView, int emptyImageResId,
                                       ImageLoadingListener listener) {
        display(url, imageView, emptyImageResId, emptyImageResId, listener, true);
    }

    public static void display(
            String url, ImageView imageView, int dftImageResId,
            int failImageResId, ImageLoadingListener listener, boolean isOriginal) {
        DisplayImageOptions.Builder b = new DisplayImageOptions.Builder().cloneFrom(
                dftOptionsBuilder.build());
        b.showImageForEmptyUri(dftImageResId);
        b.showImageOnFail(failImageResId);
        //b.showImageOnLoading(dftImageResId);//会闪
        if (isOriginal) {
            b.imageScaleType(ImageScaleType.NONE);
            b.bitmapConfig(Bitmap.Config.ARGB_8888);
        }

        ImageAware imageAware = new ImageViewAware(imageView, false);
        imageLoader.displayImage(url, imageAware, b.build(), listener);
    }

    public static String getImageCacheDirPath() {
        String path = AppU.app().getCacheDir().getAbsolutePath();
        boolean isExist = Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
        if (isExist) {
            File exCacheDir = null;
            if (Build.VERSION.SDK_INT >= 8) {
                exCacheDir = AppU.app().getExternalCacheDir();
            } else {
                exCacheDir = new File(Environment.getExternalStorageDirectory(), "oyseckill");
                if (!exCacheDir.exists() || !exCacheDir.isDirectory()) {
                    exCacheDir.mkdir();
                }
            }
            if (exCacheDir != null && exCacheDir.canWrite()) {
                path = exCacheDir.getPath();
            }
        }
        return path;
    }

    public ImageLoader getImageLoader() {
        return imageLoader;
    }

    private void initImageLoader() {
        String path = getImageCacheDirPath();
        File cacheDir = new File(path);
        if (cacheDir.exists() && cacheDir.canWrite()) {
            dftOptionsBuilder = new DisplayImageOptions.Builder()
                    .resetViewBeforeLoading(false) //如果为true，在ListView中可能会闪
                    .cacheInMemory(true).cacheOnDisk(true)
                    //                    .displayer(new FadeInBitmapDisplayer(50))  //淡入,会闪
                    //.displayer(new RoundedBitmapDisplayer(10)) //圆角
                    .imageScaleType(ImageScaleType.IN_SAMPLE_INT)
                    .bitmapConfig(Bitmap.Config.RGB_565) //设置为RGB565比起默认的ARGB_8888要节省大量的内存
                    .delayBeforeLoading(50) ///载入图片前稍做延时可以提高整体滑动的流畅度
                    .considerExifParams(true);

            ImageLoaderConfiguration.Builder config = new ImageLoaderConfiguration.Builder(AppU.app());
            config.threadPriority(Thread.NORM_PRIORITY - 2); //3
            config.threadPriority(Thread.NORM_PRIORITY - 2);
            config.denyCacheImageMultipleSizesInMemory();
            config.diskCacheFileNameGenerator(new Md5FileNameGenerator());
            config.diskCacheSize(50 * 1024 * 1024); // 50MB
            config.tasksProcessingOrder(QueueProcessingType.LIFO);
            config.defaultDisplayImageOptions(dftOptionsBuilder.build());
            config.memoryCache(new UsingFreqLimitedMemoryCache(5 * 1024 * 1024));
            config.memoryCacheSize(5 * 1024 * 1024);
            config.diskCacheFileCount(100);
            config.diskCache(new UnlimitedDiskCache(cacheDir));
            imageLoader.init(config.build());

        } else {
            imageLoader.init(new ImageLoaderConfiguration.Builder(AppU.app()).build());
            LogU.e("No cache dir.");
        }
    }

    public boolean isImgExistOnDisk(String uri) {
        File diskCache = DiskCacheUtils.findInCache(uri, ImageLoader.getInstance().getDiscCache());
        if (diskCache != null) {
            return diskCache.exists() && diskCache.isFile();
        }
        return false;
    }

    public boolean isImgExistOnMemory(String uri) {
        List<Bitmap> listBitMap = MemoryCacheUtils.findCachedBitmapsForImageUri(uri, ImageLoader.getInstance()
                .getMemoryCache());

        if (listBitMap != null && listBitMap.size() > 0) {
            return true;
        }
        return false;
    }

    public void removeCache(String url) {
        DiskCacheUtils.removeFromCache(url, ImageLoader.getInstance().getDiskCache());
        MemoryCacheUtils.removeFromCache(url, ImageLoader.getInstance().getMemoryCache());
    }

    public void clearCache() {
        imageLoader.clearDiskCache();
        imageLoader.clearMemoryCache();
    }
}
