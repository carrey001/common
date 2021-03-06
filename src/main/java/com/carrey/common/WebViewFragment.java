package com.carrey.common;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.JsResult;
import android.webkit.URLUtil;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import com.carrey.common.util.ApiCompatibleUtil;
import com.carrey.common.util.BaseConstant;
import com.carrey.common.util.SchemeUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Bob on 2015/7/9.
 */
public class WebViewFragment extends BaseFragment implements View.OnClickListener {
    public final static String EXTRA_URL = "url";
    public final static String EXTRA_SHOW_BOTTOM_BAR = "show_bottom_bar";
    public final static String EXTRA_HEADERS = "headers";
    public final static String EXTRA_CACHE_ABLE = "cache_able";

    private FrameLayout mWebParentView;
    private WebView mWebView;
    private View mBottomBar;
    private ImageButton mPreBtn, mNextBtn, mRefreshBtn;
    private ProgressBar mProgressBar;

//    private ResponseCallback<String> mTitleResponseCallback;
//    private ResponseCallback<String> mPageResponseCallback;

    private Map<String, String> mHeaders;
    private Map<String, Runnable> mFilters = new HashMap<>();
    private String mUrl;
    private boolean mShowBottomBar;
    private boolean mDismissErrorPage = false;
    public String currentUrl;
    private boolean mCacheEnable;
//    private PhotoPicker mPhotoPicker;
    private ValueCallback<Uri> mUploadMessage;
    private ValueCallback<Uri[]> mFilePathCallback;
    private int mProgressBarColor;

    @Override
    protected View onCreateView(LayoutInflater inflater, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_webview, null);
    }

    public void setProgressColor(int color) {
        mProgressBarColor = color;
        if (mProgressBar != null) {
            ClipDrawable d = new ClipDrawable(new ColorDrawable(color), Gravity.LEFT, ClipDrawable.HORIZONTAL);
            mProgressBar.setProgressDrawable(d);
        }
    }

    @Override
    protected void initViews() {
        mWebView = (WebView) findViewById(R.id.webView);
        mProgressBar = (ProgressBar) findViewById(R.id.webview_progressBar);
        mBottomBar = findViewById(R.id.bottomBar);
        mPreBtn = (ImageButton) findViewById(R.id.btn_pre);
        mNextBtn = (ImageButton) findViewById(R.id.btn_next);
        mRefreshBtn = (ImageButton) findViewById(R.id.btn_refersh);
        mWebParentView = (FrameLayout) mWebView.getParent();

        mPreBtn.setOnClickListener(this);
        mNextBtn.setOnClickListener(this);
        mRefreshBtn.setOnClickListener(this);
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
//        mPhotoPicker = new PhotoPicker(this);
//        mPhotoPicker.setMaxCount(1);
//        WebViewUtil.initSettings(mWebView);

//        if (mPageResponseCallback != null) {
//            mPageResponseCallback.onStart();
//        }
        mWebView.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
//                mRefreshBtn.setImageResource(R.drawable.btn_stop);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
//                mRefreshBtn.setImageResource(R.drawable.btn_refersh);
                if (mWebView == null)
                    return;
                if (mWebView.canGoBack())
                    mPreBtn.setEnabled(true);
                else
                    mPreBtn.setEnabled(false);

                if (mWebView.canGoForward()) {
                    mNextBtn.setEnabled(true);
                } else {
                    mNextBtn.setEnabled(false);
                }

//                if (mPageResponseCallback != null) {
//                    mPageResponseCallback.onSuccess(url);
//                    mPageResponseCallback.onFinish();
//                }

                if (mDismissErrorPage) {
//                    UIUtil.hideAllNoticeView(mWebParentView);
                    mDismissErrorPage = false;
                    if (mBottomBar != null) {
                        loadBottomBar();
                    }
                    mProgressBar.setVisibility(View.GONE);
                }

            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                currentUrl = url;
                String tag = Uri.parse(url).getPath();

                if (mFilters.containsKey(tag)) {
                    final Runnable runnable = mFilters.get(tag);
                    if (runnable != null) {
                        runnable.run();
                    }
                } else if (URLUtil.isValidUrl(url)) {
                    mWebView.loadUrl(url, mHeaders);
                } else {
                    SchemeUtil.startActivity(mBaseActivity, url);
                }
                return true;
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                view.stopLoading();
                view.clearView();
//                if (mTitleResponseCallback != null) {
//                    mTitleResponseCallback.onFailure(new HttpException(errorCode), description);
//                }
//                if (mPageResponseCallback != null) {
//                    mPageResponseCallback.onFailure(new HttpException(errorCode), description);
//                }

                mDismissErrorPage = false;
//                UIUtil.showErrorView(mWebParentView, new View.OnClickListener() {
//
//                    @Override
//                    public void onClick(View v) {
//                        mWebView.reload();
//                        mDismissErrorPage = true;
//                        if (mPageResponseCallback != null) {
//                            mPageResponseCallback.onStart();
//                        }
//                    }
//                });

            }
        });

        mWebView.setWebChromeClient(new WebChromeClient() {
            //android 3.0之前版本调用
            public void openFileChooser(ValueCallback<Uri> uploadMsg) {
                mUploadMessage = uploadMsg;
//                mPhotoPicker.pickPhoto();
            }

            //android 3.0+版本调用
            public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType) {
                mUploadMessage = uploadMsg;
//                mPhotoPicker.pickPhoto();
            }

            //android 4.0+版本调用
            public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
                mUploadMessage = uploadMsg;
//                mPhotoPicker.pickPhoto();
            }

            // 5.0+调用
            @Override
            public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
                mFilePathCallback = filePathCallback;
//                mPhotoPicker.pickPhoto();
                return true;
            }

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress == 100) {
                    mProgressBar.setVisibility(View.GONE);
                } else {
                    mProgressBar.setVisibility(View.VISIBLE);
                }
                mProgressBar.setProgress(newProgress + 5);
                super.onProgressChanged(view, newProgress);
//                if (mPageResponseCallback != null) {
//                    mPageResponseCallback.onLoading(100, newProgress, false);
//                }
            }

            @Override
            public void onReceivedTitle(WebView view, String title) { // 获取到Title
                super.onReceivedTitle(view, title);
//                if (mTitleResponseCallback != null) {
//                    mTitleResponseCallback.onSuccess(title);
//                }
            }

            @Override
            public void onReceivedIcon(WebView view, Bitmap icon) { // 获取到图标
                super.onReceivedIcon(view, icon);
            }


            @Override
            public boolean onJsAlert(WebView view, String url, final String message, final JsResult result) {
                mBaseActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
//                        UIUtil.showAlert(mBaseActivity, message, new View.OnClickListener() {
//                            @Override
//                            public void onClick(View view) {
//                                result.confirm();
//                            }
//                        }).setOnDismissListener(new DialogInterface.OnDismissListener() {
//                            @Override
//                            public void onDismiss(DialogInterface dialogInterface) {
//                                result.cancel();
//                            }
//                        });
                    }
                });
                return true;
            }

            @Override
            public boolean onJsConfirm(WebView view, String url, final String message, final JsResult result) {
                mBaseActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
//                        UIUtil.showConfirm(mBaseActivity, "确认", message, "确定", new View.OnClickListener() {
//                            @Override
//                            public void onClick(View view) {
//                                result.confirm();
//                            }
//                        }, "取消", null).setOnDismissListener(new DialogInterface.OnDismissListener() {
//                            @Override
//                            public void onDismiss(DialogInterface dialogInterface) {
//                                result.cancel();
//                            }
//                        });
                    }
                });
                return true;
            }
        });

        //屏蔽长按事件， webView长按会调用系统复制控件
        mWebView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                return true;
            }
        });

        Bundle bundle = getArguments();
        if (bundle != null) {
            mShowBottomBar = bundle.getBoolean(EXTRA_SHOW_BOTTOM_BAR);
            mUrl = bundle.getString(EXTRA_URL);
//            mUrl = "file:///android_asset/aa.html";
//            mUrl = "http://www.script-tutorials.com/demos/199/index.html";
            mCacheEnable = bundle.getBoolean(EXTRA_CACHE_ABLE, true);

            setCacheEnable(mCacheEnable);
            loadBottomBar();
            mWebView.loadUrl(mUrl, mHeaders);
            if (mProgressBarColor != 0) {
                setProgressColor(mProgressBarColor);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // 文件上传
        if (requestCode == BaseConstant.REQUEST_CODE_PICK) {
            if (resultCode == Activity.RESULT_OK) {
                if (null == mUploadMessage && null == mFilePathCallback) return;
//                mPhotoPicker.dealResult(requestCode, resultCode, data, new PhotoManager.OnLocalRecentListener() {
//                    @Override
//                    public void onPhotoLoaded(List<PhotoModel> photos) {
//                        if (photos != null && !photos.isEmpty()) {
//                            PhotoModel photo = photos.get(0);
//                            if (mUploadMessage != null) {
//                                mUploadMessage.onReceiveValue(Uri.fromFile(new File(photo.getThumbPath())));
//                                mUploadMessage = null;
//                            }
//                            if (mFilePathCallback != null) {
//                                Uri[] uris = new Uri[1];
//                                uris[0] = Uri.fromFile(new File(photo.getThumbPath()));
//                                mFilePathCallback.onReceiveValue(uris);
//                                mFilePathCallback = null;
//                            }
//                        }
//                    }
//                });
            } else {
                if (mUploadMessage != null) {
                    mUploadMessage.onReceiveValue(null);
                    mUploadMessage = null;
                }
                if (mFilePathCallback != null) {
                    mFilePathCallback.onReceiveValue(null);
                    mFilePathCallback = null;
                }
            }

        }
    }

    /**
     * 添加url拦截过滤关键字（对于特殊的url。可能要单独处理）
     * @param tag 关键字
     * @param runnable 拦截到url后要执行的任务
     */
    public void putFilter(String tag, Runnable runnable) {
        mFilters.put(tag, runnable);
    }

    /**
     * 是否显示底部导航按钮
     * @param showBottomBar
     */
    public void showBottomBar(boolean showBottomBar) {
        Bundle bundle = getArguments();
        if (bundle == null) {
            bundle = new Bundle();
            bundle.putBoolean(EXTRA_SHOW_BOTTOM_BAR, showBottomBar);
            setArguments(bundle);
        } else {
            bundle.putBoolean(EXTRA_SHOW_BOTTOM_BAR, showBottomBar);
        }
        mShowBottomBar = showBottomBar;
        if (mBottomBar != null) {
            loadBottomBar();
        }
    }

    /**
     * 添加http请求的头
     * @param headers
     */
    public void setHeaders(HashMap<String, String> headers) {
        Bundle bundle = getArguments();
        if (bundle == null) {
            bundle = new Bundle();
            bundle.putSerializable(EXTRA_HEADERS, headers);
            setArguments(bundle);
        } else {
            bundle.putSerializable(EXTRA_HEADERS, headers);
        }
        mHeaders = headers;
    }

    /**
     * 加载url
     * @param url
     */
    public void loadUrl(String url) {
        Bundle bundle = getArguments();
        if (bundle == null) {
            bundle = new Bundle();
            bundle.putString(EXTRA_URL, url);
            setArguments(bundle);
        } else {
            bundle.putString(EXTRA_URL, url);
        }
        mUrl = url;
        if (mWebView != null) {
            mWebView.loadUrl(mUrl, mHeaders);
        }
    }

    /**
     * 设置是否使用本地缓存
     * @param cacheEnable
     */
    public void setCacheEnable(boolean cacheEnable) {
        Bundle bundle = getArguments();
        if (bundle == null) {
            bundle = new Bundle();
            bundle.putBoolean(EXTRA_URL, cacheEnable);
            setArguments(bundle);
        } else {
            bundle.putBoolean(EXTRA_URL, cacheEnable);
        }
        mCacheEnable = cacheEnable;
        if (mWebView != null) {
            if (cacheEnable) {
                mWebView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
            } else {
                mWebView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
            }
        }
    }

    /**
     * 添加js调用本地代码
     * @param obj
     * @param name
     */
    @SuppressLint("JavascriptInterface")
    public void addJavaScriptInterface(Object obj, String name) {
        mWebView.addJavascriptInterface(obj, name);
    }

//    public void setTitleResponseCallback(ResponseCallback<String> callback) {
//        mTitleResponseCallback = callback;
//    }
//
//    public void setPageResponseCallback(ResponseCallback<String> callback) {
//        mPageResponseCallback = callback;
//    }

    private void loadBottomBar() {
        if (mShowBottomBar) {
            mBottomBar.setVisibility(View.VISIBLE);
            ((FrameLayout.LayoutParams) mWebView.getLayoutParams()).bottomMargin = getResources().getDimensionPixelOffset(
                    R.dimen.content_bottom_margin);
        } else {
            mBottomBar.setVisibility(View.GONE);
            ((FrameLayout.LayoutParams) mWebView.getLayoutParams()).bottomMargin = 0;
        }
    }

    /**
     * Called when the fragment is visible to the user and actively running. Resumes the WebView.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    public void onPause() {
        super.onPause();
        if (ApiCompatibleUtil.hasHoneycomb()) {
            mWebView.onPause();
        }
    }

    /**
     * Called when the fragment is no longer resumed. Pauses the WebView.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    public void onResume() {
        if (ApiCompatibleUtil.hasHoneycomb()) {
            mWebView.onResume();
        }
        super.onResume();
    }

    /**
     * Called when the fragment is no longer in use. Destroys the internal state of the WebView.
     */
    @Override
    public void onDestroy() {
        if (mWebView != null) {
            mWebView.destroy();
            mWebView = null;
        }
        super.onDestroy();
    }

    /**
     * Gets the WebView.
     */
    public WebView getWebView() {
        return mWebView;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_pre) {
            mWebView.goBack();
        } else if (v.getId() == R.id.btn_next) {
            mWebView.goForward();
        } else if (v.getId() == R.id.btn_refersh) {
            mWebView.reload();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mWebView.loadUrl("about:blank");
        mWebView.setWebChromeClient(null);
    }

    public boolean handleGoBack() {
        if(mWebView.canGoBack()) {
            mWebView.goBack();
            return true;
        } else {
            return false;
        }
    }

    public void reload() {
        mWebView.loadUrl(mUrl, mHeaders);
    }
}
