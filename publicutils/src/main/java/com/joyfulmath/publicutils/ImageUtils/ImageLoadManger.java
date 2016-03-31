package com.joyfulmath.publicutils.ImageUtils;

import android.content.Context;
import android.widget.ImageView;

/**
 * This is the interface with image loader
 *
 *
 *
 * Created by deman on 2016/3/30.
 */
public class ImageLoadManger {
    private IImageLoader iImageLoader = null;

    private static ImageLoadManger sInstance = null;

    public static synchronized ImageLoadManger getsInstance() {

        if (sInstance == null) {
            sInstance = new ImageLoadManger();
        }
        return sInstance;
    }

    /**
     *  initManager
     * @param context context
     */
    public void initManager(Context context) {
        iImageLoader = new PicassoImageLoader();
        iImageLoader.initLoader(context);
    }

    /**
     * load image with path
     * @param path          path
     * @param imageView imageView
     */
    public void load(String path, ImageView imageView) {

        if(null == iImageLoader)
        {
            throw new RuntimeException("initManager has not been init");
        }
        iImageLoader.load(path, imageView);
    }

    /**
     * load image with path
     * @param path                  path
     * @param imageView         imageView
     * @param width                 width
     * @param height                height
     */
    public void load(String path, ImageView imageView,int width,int height) {
        if(null == iImageLoader)
        {
            throw new RuntimeException("initManager has not been init");
        }
        iImageLoader.load(path, imageView,width,height);
    }
}
