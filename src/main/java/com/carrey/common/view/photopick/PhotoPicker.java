package com.carrey.common.view.photopick;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;

import com.carrey.common.util.BaseConstant;
import com.carrey.common.util.SystemUtil;
import com.carrey.common.util.UIUtil;

import java.io.Serializable;
import java.util.List;

/**
 * Created by chenbo on 2015/6/9.
 */
public class PhotoPicker {

    private Activity mContext;

    private Fragment mFragment;
    private Activity mActivity;

    private PhotoManager mPhotoManager;

    private PhotoParam mParam;
    private boolean mIsInCrop;

    public PhotoPicker(Fragment fragment) {
        mContext = fragment.getActivity();
        mFragment = fragment;

        init();
    }

    public PhotoPicker(Activity activity) {
        mContext = activity;
        mActivity = activity;

        init();
    }

    private void init() {
        mPhotoManager = new PhotoManager(mContext);
        mParam = new PhotoParam();
    }

    /**
     * 选择图片
     * pickPhoto
     * @since 1.0
     */
    public void pickPhoto() {
        if (mIsInCrop) {
            UIUtil.toastShort(mContext, "图片正在裁剪中，请稍后...");
            return;
        }
        Intent intent = new Intent(SystemUtil.getApplicationPackageName(mContext) + BaseConstant.ACTION_PICK_PHOTO);
        intent.putExtra(BasePhotoPickActivity.EXTRA_PARAM, mParam);
        if (mFragment != null) {
            mFragment.startActivityForResult(intent, BaseConstant.REQUEST_CODE_PICK);
        } else if (mActivity != null) {
            mActivity.startActivityForResult(intent, BaseConstant.REQUEST_CODE_PICK);
        }
    }

    /**
     * 处理结果，在onActivityResult中调用
     * dealResult
     * @param requestCode
     * @param resultCode
     * @param data
     * @param l
     * @since 1.0
     */
    @SuppressWarnings("unchecked")
    public void dealResult(int requestCode, int resultCode, Intent data, final PhotoManager.OnLocalRecentListener l) {
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case BaseConstant.REQUEST_CODE_PICK:
                    List<PhotoModel> list = (List<PhotoModel>) data.getExtras().getSerializable(PhotoViewActivity.EXTRA_PHOTOS);
                    if (list != null) {
                        if (mParam.mNeedCrop) {
                            l.onPhotoLoaded(list);
                        } else {
                            Dialog dialog = UIUtil.showProgressBar(mContext);
                            if (dialog != null) {
                                dialog.setCancelable(false);
                            }
                            mIsInCrop = true;
                            mPhotoManager.getThumb(list, mParam.mRequestWidth, mParam.mRequestHeight, new PhotoManager.OnLocalRecentListener() {

                                @Override
                                public void onPhotoLoaded(List<PhotoModel> photos) {
                                    mIsInCrop = false;
                                    UIUtil.hideProgressBar(mContext);
                                    if (l != null) {
                                        l.onPhotoLoaded(photos);
                                    }
                                }
                            });
                        }
                    }
                    break;
                default:
                    break;
            }
        }
    }

    public static Intent getCropImageIntent(Uri photoUri, Uri outUri, PhotoParam param) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(photoUri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", param.mRequestWidth);
        intent.putExtra("aspectY", param.mRequestHeight);
        intent.putExtra("outputX", param.mRequestWidth);
        intent.putExtra("outputY", param.mRequestHeight);
        intent.putExtra("return-data", false);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        intent.putExtra(MediaStore.EXTRA_OUTPUT, outUri);
        return intent;
    }

    /**
     * 设置是否需要裁剪
     * setNeedCrop
     * @param needCrop
     * @since 1.0
     */
    public void setNeedCrop(boolean needCrop) {
        mParam.mNeedCrop = needCrop;
        // 裁剪的时候只能一张一张裁剪
        if (needCrop) {
            mParam.mMaxCount = 1;
        }
    }

    /**
     * 设置最多选择多少张照片
     * setMaxCount
     * @param maxCount
     * @since 1.0
     */
    public void setMaxCount(int maxCount) {
        mParam.mMaxCount = maxCount;
        // 裁剪的时候只能一张一张裁剪
        if (mParam.mNeedCrop) {
            mParam.mMaxCount = 1;
        }
    }

    /**
     * 设置图片缩放的尺寸
     * setRequestWH
     * @param width
     * @param height
     * @since 1.0
     */
    public void setRequestWH(int width, int height) {
        mParam.mRequestWidth = width;
        mParam.mRequestHeight = height;
    }

    public static class PhotoParam implements Serializable {
        private static final long serialVersionUID = 1L;

        public int mMaxCount;
        public int mRequestWidth;
        public int mRequestHeight;
        public boolean mNeedCrop;

        public PhotoParam() {
            mMaxCount = 6;
            mRequestWidth = SystemUtil.getScreenWidth() < 720 ? 720 : SystemUtil.getScreenWidth();
            mRequestHeight = SystemUtil.getScreenHeight() < 1080 ? 1080 : SystemUtil.getScreenHeight();
        }
    }
}
