package com.carrey.common;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.carrey.common.util.AndroidBug5497Workaround;
import com.carrey.common.util.ApiCompatibleUtil;
import com.carrey.common.util.SchemeUtil;
import com.carrey.common.util.SystemUtil;
import com.carrey.common.view.swipeback.SwipeBackActivityHelper;

import java.util.Set;

/**
 * 类描述：Activity的基类
 * 创建人：carrey
 * 创建时间：2016/1/19 15:06
 */

public abstract class CommonActivity extends AppCompatActivity {

    protected View mStatusBarTintView;  //状态栏颜色view 沉浸式?
    private FrameLayout mContentLayout;
    public boolean mIsResume;

    private SwipeBackActivityHelper mHelper;//滑动返回

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 竖屏固定
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
        // app启动
        CommonApp.getApp().start();
        if (isValidate()) {
            // 开启滑动关闭界面
            if (useSwipeBackLayout()) {
                mHelper = new SwipeBackActivityHelper(this);
                mHelper.onActivityCreate();
            }

            convertDataToBundle();
            if (useTintStatusBar()) {
                SystemUtil.setTintStatusBarAvailable(this, isStatusBarDarkMode());
            }
            // 初始化头部
            initTopView();
            // 初始化布局
            initViews();
            // 初始化数据
            initData(savedInstanceState);
        } else {
            finish();
        }
    }

    // 初始化头部
    private void initTopView() {
        if (isUseCustomContent()) {
            // 最外层布局
            RelativeLayout base_view = new RelativeLayout(this);
            if (isShowTitleBar()) {
                initTitleBar();
                // 填入View
//                base_view.addView(mTitleBar);
            } else if (isShowTintStatusBar() && ApiCompatibleUtil.hasKitKat()) {
                initTintStatusBar();
                // 填入View
                base_view.addView(mStatusBarTintView);
            }
            // 内容布局
            mContentLayout = new FrameLayout(this);
            mContentLayout.setId(R.id.content);
//            mContentLayout.setPadding(0, 0, 0, ApiCompatibleUtil.hasLollipop() ? SystemUtil.getNavigationBarHeight(this) : 0);
            RelativeLayout.LayoutParams layoutParamsContent = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
            layoutParamsContent.addRule(RelativeLayout.BELOW, R.id.titlebar);
            base_view.addView(mContentLayout, layoutParamsContent);

            // 设置ContentView
            setContentView(base_view, new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
            if (SystemUtil.isTintStatusBarAvailable(this)) {
                if ((getWindow().getAttributes().softInputMode & WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE) != 0) {
                    AndroidBug5497Workaround.assistActivity(this);
                }
            }
        }
    }

    /**
     * 初始化 状态栏
     */
    protected void initTintStatusBar() {
        mStatusBarTintView = new View(this);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
                SystemUtil.getStatusBarHeight());
        mStatusBarTintView.setLayoutParams(params);
        mStatusBarTintView.setId(R.id.titlebar);
    }

    /**
     * 是否使用默认的statusbar
     *
     * @return
     */
    protected boolean isShowTintStatusBar() {
        return true;
    }

    protected void initTitleBar() {
        // 主标题栏   // TODO: inittitle bar
//        mTitleBar = new TitleBar(this);
//        mTitleBar.setId(R.id.titlebar);
//        mTitleBar.setPadding(0, SystemUtil.isTintStatusBarAvailable(this) ? SystemUtil.getStatusBarHeight() : 0, 0, 0);
//        mTitleBar.setLeftClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                finish();
//            }
//        });
    }

    // 跟布局是否使用自定义布局
    private boolean isUseCustomContent() {
        return isShowTitleBar() || (useTintStatusBar() && ApiCompatibleUtil.hasKitKat());
    }

    /**
     * 是否使用默认titlebar
     * useTitleBar
     */
    protected boolean isShowTitleBar() {
        return false;
    }

    /**
     * 是否使用半透明沉浸状态栏
     *
     * @return true 半透明 false 全透明
     */
    protected boolean isStatusBarDarkMode() {
        return false;
    }

    /**
     * 是否使用沉浸式状态栏
     *
     * @return
     */
    protected boolean useTintStatusBar() {
        return true;
    }

    /**
     * 是否用滑动返回
     */
    protected boolean useSwipeBackLayout() {
        return true;
    }

    /**
     * 如果是无效的 直接finish
     */
    protected boolean isValidate() {
        return true;
    }

    /**
     * 用指定的View填充主界面(默认有标题)
     *
     * @param contentView 指定的View
     */
    public void setContentView(View contentView) {
        if (isUseCustomContent()) {
            mContentLayout.removeAllViews();
            mContentLayout.addView(contentView, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT));
        } else {
            super.setContentView(contentView);
        }
    }

    /**
     * 用指定资源ID表示的View填充主界面(默认有标题)
     *
     * @param resId 指定的View的资源ID
     */
    public void setContentView(int resId) {
        setContentView(LayoutInflater.from(this).inflate(resId, null));
    }

    /**
     * 获得内容布局
     * getContentView
     */
    public View getContentView() {
        if (isShowTitleBar()) {
            return mContentLayout;
        } else {
            return getWindow().getDecorView();
        }
    }

    public void setSwipeBackEnable(boolean isSwipeBackEnable) {
        if (mHelper != null) {
            mHelper.getSwipeBackLayout().setEnableGesture(isSwipeBackEnable);
        }
    }

    //当Activity彻底运行起来之后回调onPostCreate方法
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }

    @Override
    public void onPostCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onPostCreate(savedInstanceState, persistentState);
        if (mHelper != null) {
            mHelper.onPostCreate();
        }
    }

    //launchMode为singleTask的时候，通过Intent启到一个Activity,如果系统已经存在一个实例，
    // 系统就会将请求发送到这个实例上，但这个时候，系统就不会再调用通常情况下我们处理请求数据的onCreate方法，
    // 而是调用onNewIntent方法
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        convertDataToBundle();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mIsResume = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        mIsResume = false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //// TODO: 隐藏所有的progressbar
    }

    public void startActivity(Intent intent, boolean useDefaultFlag) {
        if (useDefaultFlag) {
            super.startActivity(intent);
        } else {
            startActivity(intent);
        }
    }

    @Override  //清除栈顶 并设置自己为栈顶
    public void startActivity(Intent intent) {
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        super.startActivity(intent);
    }

    /**
     * scheme 跳转时 传递的参数在data里面
     * 把data的数据转换为bundle的数据
     */
    protected void convertDataToBundle() {
        Uri uri = getIntent().getData();
        if (uri != null && Intent.ACTION_VIEW.equals(getIntent().getAction())) {
            Set<String> params = SchemeUtil.getQueryParameterNames(uri);
            if (params != null) {
                for (String key : params) {
                    String value = uri.getQueryParameter(key);
                    if ("true".equals(value) || "false".equals(value)) {
                        getIntent().putExtra(key, Boolean.parseBoolean(value));
                    } else {
                        getIntent().putExtra(key, value);
                    }
                }
            }
        }
    }


    /**
     * 初始化view initViews
     */
    protected abstract void initViews();

    /**
     * 初始化数据 initData
     *
     * @param savedInstanceState
     */
    protected abstract void initData(Bundle savedInstanceState);

}
