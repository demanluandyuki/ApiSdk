package com.joyfulmath.publicutils.ImageUtils;

import android.content.Context;
import android.widget.ImageView;

/**
 * Created by deman on 2016/3/30.
 */
public interface IImageLoader {
    void initLoader(Context context);
    void load(String path, ImageView view,int width,int height);
    void load(String path, ImageView view);
}
