package com.carrey.common.view.photopick;

import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.carrey.common.CommonActivity;
import com.carrey.common.R;
import com.carrey.common.util.BaseConstant;
import com.carrey.common.util.BitmapTools;
import com.carrey.common.util.FileUtil;
import com.carrey.common.util.SystemUtil;
import com.carrey.common.util.UIUtil;
import com.carrey.common.view.photopick.adapter.PhotoViewPagerAdapter;

import org.xutils.common.Callback;

import java.util.ArrayList;

/**
 * 预览图片
 * PhotoViewActivity
 * chenbo
 * 2015年3月16日 下午3:46:27
 * @version 1.0
 */
public class PhotoViewActivity extends CommonActivity {
    public final static String EXTRA_PHOTOS = "imgUrlList";
    public final static String EXTRA_INDEX = "selectIndex";
    public final static String EXTRA_TYPE = "type";
    
    public final static int TYPE_VIEW = 0;
    public final static int TYPE_LOCAL = 1;
    
    private ViewPager mViewPager;
    private ImageButton mCtrlBtn;
    private TextView mCountText;
    private View mTitleLayout;

    private BitmapTools mBitmapTools;
    private PhotoViewPagerAdapter mPagerAdapter;
    private ArrayList<String> imgUrlList;
    
    @Override
    protected void initViews() {
        getWindow().setBackgroundDrawable(null);
        setContentView(R.layout.activity_photo_view);
        
        mCountText = (TextView) findViewById(R.id.pageStateTxt);
        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        mCtrlBtn = (ImageButton) findViewById(R.id.ctrlBtn);
        mTitleLayout = findViewById(R.id.photoview_title);
        
        mViewPager.addOnPageChangeListener(new OnPageChangeListener() {

            @Override
            public void onPageSelected(int arg0) {
                if (imgUrlList != null) {
                    mCountText.setText(String.format("%d/%d", arg0 + 1, imgUrlList.size()));
                }
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {

            }

            @Override
            public void onPageScrollStateChanged(int arg0) {

            }
        });
        if (SystemUtil.isTintStatusBarAvailable(this)) {
            ((RelativeLayout.LayoutParams)mTitleLayout.getLayoutParams()).topMargin += SystemUtil.getStatusBarHeight();
        }
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        mBitmapTools = new BitmapTools(this);
        imgUrlList = getIntent().getStringArrayListExtra(EXTRA_PHOTOS);
        int idx = getIntent().getIntExtra(EXTRA_INDEX, 0);
        
        mCountText.setText(String.format("%d/%d", idx + 1, imgUrlList.size()));
        
        mPagerAdapter = new PhotoViewPagerAdapter(this, imgUrlList);
        mViewPager.setAdapter(mPagerAdapter);
        mViewPager.setCurrentItem(idx, false);
        
        int type = getIntent().getIntExtra(EXTRA_TYPE, 0);
        if (type == TYPE_VIEW) {
            mCtrlBtn.setVisibility(View.VISIBLE);
        } else {
            mCtrlBtn.setVisibility(View.GONE);
        }
        
    }
    
    public void onClick(View view) {
        if (view.getId() == R.id.ctrlBtn) {
            mBitmapTools.display((ImageView) view, imgUrlList.get(mViewPager.getCurrentItem()), new Callback.CacheCallback<Drawable>() {
                @Override
                public boolean onCache(Drawable result) {
                    return false;
                }

                @Override
                public void onSuccess(Drawable result) {
                    String path = BaseConstant.SAVE_PATH + "/" + System.currentTimeMillis() + ".jpg";
                    FileUtil.saveBitmap(((BitmapDrawable) result).getBitmap(), path);
                    UIUtil.toastShort(PhotoViewActivity.this, "图片已经成功保存到:" + path);
                }

                @Override
                public void onError(Throwable ex, boolean isOnCallback) {
                    UIUtil.toastShort(PhotoViewActivity.this, "图片保存失败!");
                }

                @Override
                public void onCancelled(CancelledException cex) {

                }

                @Override
                public void onFinished() {

                }
            });
//            mBitmapTools.display(view, imgUrlList.get(mViewPager.getCurrentItem()), new BitmapLoadCallBack<View>() {
//
//                @Override
//                public void onLoadCompleted(View container, String uri, Bitmap bitmap, BitmapDisplayConfig config, BitmapLoadFrom from) {
//                    String path = BaseConstant.SAVE_PATH + "/" + System.currentTimeMillis() + ".jpg";
//                    FileUtil.saveBitmap(bitmap, path);
//                    UIUtil.toastShort(PhotoViewActivity.this, "图片已经成功保存到:" + path);
//                }
//
//                @Override
//                public void onLoadFailed(View container, String uri, Drawable drawable) {
//                    UIUtil.toastShort(PhotoViewActivity.this, "图片保存失败!");
//                }
//
//            });
        }
    }

    @Override
    protected boolean useSwipeBackLayout() {
        return false;
    }

    @Override
    protected boolean isShowTitleBar() {
        return false;
    }

    @Override
    protected boolean isShowTintStatusBar() {
        return false;
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

    }

}
