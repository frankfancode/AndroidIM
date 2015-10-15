package com.frankfann.im.utils;

import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

/**
 * Created by user on 2015/10/15.
 */
public class ImageUtils {
    /**
     *
     * @param url 地址
     * @param image 默认图片
     * @param imageView ImageView
     */
    public static void setImage(String url, int image, ImageView imageView) {
        ImageLoader imageLoader = ImageLoader.getInstance();
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showImageOnLoading(image).showStubImage(image) // 加载开始默认的图片
                .showImageForEmptyUri(image) // url爲空會显示该图片，自己放在drawable里面的
                .showImageOnFail(image) // 加载图片出现问题，会显示该图片

                .cacheInMemory() // 缓存用
                .cacheOnDisc() // 缓存用
                .displayer(new RoundedBitmapDisplayer(5)) // 图片圆角显示，值为整数
                .build();
        imageLoader.displayImage(url, imageView, options);

    }

}