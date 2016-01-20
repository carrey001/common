package com.carrey.common.util;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import com.carrey.common.R;

import org.xutils.common.Callback;
import org.xutils.image.ImageOptions;
import org.xutils.x;

/**
 * 类描述：
 * 创建人：carrey
 * 创建时间：2016/1/20 15:01
 */

public class BitmapTools {
    private Context context;
    private int screenWidth;
    private int screenHeight;
    private int halfStandardPadding;
    private ImageOptions.Builder builder;

    public BitmapTools(Context context) {
        this.context = context.getApplicationContext();
        screenWidth = SystemUtil.getScreenWidth();
        screenHeight = SystemUtil.getScreenHeight();
        halfStandardPadding = UIUtil.dip2px(4);
        builder = new ImageOptions.Builder();
    }

    public void setLoadingDrawable(Drawable failureDrawable) {
        builder.setLoadingDrawable(failureDrawable);
    }

    public void setLoadingDrawable(int failureDrawable) {
        builder.setLoadingDrawableId(failureDrawable);
    }

    public void setLoadFailedDrawable(Drawable failureDrawable) {
        builder.setFailureDrawable(failureDrawable);
    }

    public void setLoadFailedDrawable(int failureDrawable) {
        builder.setFailureDrawableId(failureDrawable);
    }

    public void display(ImageView container, String uri) {
        display(container, uri, 0, 0, SizeType.LARGE, 0, null);
    }

    public void display(ImageView container, String uri, SizeType sizeType) {
        display(container, uri, 0, 0, sizeType, 0, null);
    }

    public void display(ImageView container, String uri, SizeType sizeType, int defaultDrawableRes) {
        display(container, uri, 0, 0, sizeType, defaultDrawableRes, null);
    }

    public void display(ImageView container, String uri, int width, int height, int defaultDrawableRes) {
        display(container, uri, width, height, SizeType.CUSTOM, defaultDrawableRes, null);
    }

    public void display(ImageView container, String uri, int width, int height) {
        display(container, uri, width, height, SizeType.CUSTOM, 0, null);
    }

    public void display(ImageView container, String uri, SizeType sizeType, int defaultDrawableRes, Callback.CommonCallback<Drawable> callBack) {
        display(container, uri, 0, 0, sizeType, defaultDrawableRes, callBack);
    }

    /* imageOptions = new ImageOptions.Builder()
                .setSize(DensityUtil.dip2px(120), DensityUtil.dip2px(120))
                .setRadius(DensityUtil.dip2px(5))
                        // 如果ImageView的大小不是定义为wrap_content, 不要crop.
                .setCrop(true)
                        // 加载中或错误图片的ScaleType
                        //.setPlaceholderScaleType(ImageView.ScaleType.MATRIX)
                .setImageScaleType(ImageView.ScaleType.CENTER_CROP)
                .setLoadingDrawableId(R.mipmap.ic_launcher)
                .setFailureDrawableId(R.mipmap.ic_launcher)
                .build();*/

    public void display(ImageView container, String url, int width, int height, final SizeType sizeType, int defaultDrawableRes, Callback.CommonCallback<Drawable> callback) {
        ImageOptions imageOptions = builder.build();
        switch (sizeType) {
            case LARGE:

                if (defaultDrawableRes == 0 && imageOptions.getLoadingDrawable(container) == null) {
                    defaultDrawableRes = R.drawable.ic_default_large;
                }
                if (imageOptions.getMaxWidth() == 0 && imageOptions.getMaxHeight() == 0) {
                    builder.setSize(screenWidth, screenHeight);
                }
                break;
            case MEDIUM:
                if (defaultDrawableRes == 0 && imageOptions.getLoadingDrawable(container) == null) {
                    defaultDrawableRes = R.drawable.ic_default_normal;
                }
                if (imageOptions.getMaxWidth() == 0 && imageOptions.getMaxHeight() == 0) {
                    int mMaxWidth = screenWidth / 2 - halfStandardPadding * 3;
                    builder.setSize(mMaxWidth, mMaxWidth);
                }
                break;
            case SMALL:
                if (defaultDrawableRes == 0 && imageOptions.getLoadingDrawable(container) == null) {
                    defaultDrawableRes = R.drawable.ic_default_small;
                }
                if (imageOptions.getMaxWidth() == 0 && imageOptions.getMaxHeight() == 0) {
                    int sMaxWidth = screenWidth / 4 - halfStandardPadding * 5 / 2;
                    builder.setSize(sMaxWidth, sMaxWidth);
                }
                break;
            case CUSTOM:
                if (defaultDrawableRes == 0 && imageOptions.getLoadingDrawable(container) == null) {
                    if (width > screenWidth / 2) {
                        defaultDrawableRes = R.drawable.ic_default_large;
                    } else if (width > screenWidth / 3) {
                        defaultDrawableRes = R.drawable.ic_default_normal;
                    } else {
                        defaultDrawableRes = R.drawable.ic_default_small;
                    }
                }
                if (width != 0 && height != 0) {
                    builder.setSize(width, height);
                } else {
                    builder.setSize(screenWidth, screenHeight);
                }
                break;
            case ORIGINAL:
                builder.setCircular(true);
                break;
        }
        if (defaultDrawableRes != 0) {
            builder.setLoadingDrawableId(defaultDrawableRes);
            builder.setFailureDrawableId(defaultDrawableRes);
        }

        x.image().bind(container, url, builder.build(), callback);
    }

    public enum SizeType {
        LARGE, MEDIUM, SMALL, CUSTOM, ORIGINAL
    }
}
