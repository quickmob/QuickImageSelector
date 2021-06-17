package com.lookballs.pictureselector;

import android.app.Activity;
import android.content.Intent;

import androidx.fragment.app.Fragment;

import com.lookballs.pictureselector.app.activity.PicturePreviewActivity;
import com.lookballs.pictureselector.app.bean.LoadMediaBean;
import com.lookballs.pictureselector.config.PictureConfig;
import com.lookballs.pictureselector.config.PictureOptions;
import com.lookballs.pictureselector.util.CommonUtil;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

public final class PictureSelector {

    private final WeakReference<Activity> mActivity;
    private final WeakReference<Fragment> mFragment;

    private PictureSelector(Activity activity) {
        this(activity, null);
    }

    private PictureSelector(Fragment fragment) {
        this(fragment.getActivity(), fragment);
    }

    private PictureSelector(Activity activity, Fragment fragment) {
        mActivity = new WeakReference<>(activity);
        mFragment = new WeakReference<>(fragment);
    }

    public static PictureSelector create(Activity activity) {
        return new PictureSelector(activity);
    }

    public static PictureSelector create(Fragment fragment) {
        return new PictureSelector(fragment);
    }

    public PictureOptions choose(Choose choose) {
        return new PictureOptions(this, choose);
    }

    /**
     * 选择的数据接收器
     *
     * @param data
     * @return
     */
    public static ArrayList<LoadMediaBean> obtainMultipleResult(Intent data, boolean isCameraResult) {
        ArrayList<LoadMediaBean> result = new ArrayList<>();
        if (data != null) {
            if (isCameraResult) {
                result = (ArrayList<LoadMediaBean>) data.getSerializableExtra(PictureConfig.EXTRA_RESULT_CAMERA);
            } else {
                result = (ArrayList<LoadMediaBean>) data.getSerializableExtra(PictureConfig.EXTRA_RESULT_SELECT);
            }
            if (result == null) {
                result = new ArrayList<>();
            }
            return result;
        }
        return result;
    }

    /**
     * 对外的选择的数据接收器
     *
     * @param data
     * @return
     */
    public static void onActivityResult(int requestCode, int resultCode, Intent data, PictureOptions.OnPictureSelectResult onPictureSelectResult) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == PictureConfig.CHOOSE_REQUEST) {
                //图片、视频、音频选择结果回调
                ArrayList<LoadMediaBean> selectList = obtainMultipleResult(data, false);
                if (onPictureSelectResult != null) {
                    onPictureSelectResult.onResult(selectList);
                }
            }
        }
    }

    /**
     * 对外的选择的数据接收器
     *
     * @param data
     * @return
     */
    public static void onActivityResult(int requestCode, int resultCode, Intent data, PictureOptions.OnCameraResult onCameraResult) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == PictureConfig.CAMERA_REQUEST) {
                //拍摄结果回调
                ArrayList<LoadMediaBean> selectList = obtainMultipleResult(data, true);
                if (onCameraResult != null) {
                    onCameraResult.onResult(selectList);
                }
            }
        }
    }

    /**
     * 对外的打开图片预览页面
     *
     * @param mActivity
     * @param position
     * @param mImageList
     */
    public static void openPicturePreview(Activity mActivity, ArrayList<LoadMediaBean> mImageList, int position) {
        if (!CommonUtil.isFastClick()) {
            PicturePreviewActivity.openActivity(mActivity, mImageList, position);
        }
    }

    //全部TYPE_ALL、图片TYPE_IMAGE、视频TYPE_VIDEO
    public static enum Choose {
        TYPE_ALL(PictureConfig.TYPE_ALL),
        TYPE_IMAGE(PictureConfig.TYPE_IMAGE),
        TYPE_VIDEO(PictureConfig.TYPE_VIDEO);

        private int mValue;

        Choose(int value) {
            mValue = value;
        }

        public int getValue() {
            return mValue;
        }
    }

    //设置选择模式MULTIPLE（多选）、SINGLE（单选）、NONE（不选）
    public static enum SelectMode {
        NONE(PictureConfig.NONE),
        SINGLE(PictureConfig.SINGLE),
        MULTIPLE(PictureConfig.MULTIPLE);

        private int mValue;

        SelectMode(int value) {
            mValue = value;
        }

        public int getValue() {
            return mValue;
        }
    }

    //设置主题样式（请参照默认主题样式进行配置）
    public static enum ThemeStyle {
        DEFAULT(R.style.picture_default_style),
        WHITE(R.style.picture_white_style),
        NUM(R.style.picture_num_style);

        private int mValue;

        ThemeStyle(int value) {
            mValue = value;
        }

        public int getValue() {
            return mValue;
        }
    }

    public Activity getActivity() {
        return mActivity != null ? mActivity.get() : null;
    }

    public Fragment getFragment() {
        return mFragment != null ? mFragment.get() : null;
    }
}
