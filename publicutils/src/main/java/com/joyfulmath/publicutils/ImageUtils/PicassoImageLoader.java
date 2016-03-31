package com.joyfulmath.publicutils.ImageUtils;

import android.content.Context;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

/**
 * Created by deman on 2016/3/30.
 */
public class PicassoImageLoader implements IImageLoader {

    Picasso picasso;
    Context context;

    private void init()
    {
        picasso = new Picasso.Builder(context).build();
    }

    @Override
    public void initLoader(Context context) {
        this.context = context;
        init();
    }

    @Override
    public void load(String path, ImageView view,int width,int height)
    {
        if(null == path)
        {
            throw new RuntimeException("uri is null!");
        }

        if(width == 0 || height == 0)
        {
            throw new RuntimeException("width & height is 0");
        }

        picasso.load(path)
                .resize(width, height)
                .into(view);
    }

    @Override
    public void load(String path, ImageView view) {
        if(null == path)
        {
            throw new RuntimeException("uri is null!");
        }

        picasso.load(path)
                .into(view);
    }
}
