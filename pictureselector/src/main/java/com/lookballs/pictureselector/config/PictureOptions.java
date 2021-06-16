package com.lookballs.pictureselector.config;

import com.lookballs.pictureselector.PictureSelector;
import com.lookballs.pictureselector.app.activity.PictureSelectorActivity;
import com.lookballs.pictureselector.app.bean.LoadMediaBean;
import com.lookballs.pictureselector.util.CommonUtil;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class PictureOptions {

    private PictureOptionsBean pictureOptionsBean;
    private PictureSelector pictureSelector;

    public PictureOptions(PictureSelector pictureSelector, PictureSelector.Choose choose) {
        this.pictureSelector = pictureSelector;
        pictureOptionsBean = PictureOptionsBean.getCleanInstance();
        pictureOptionsBean.mimeType = choose.getValue();
    }

    /**
     * 设置是否需要拍摄 1、TYPE_ALL默认显示拍照模式 2、TYPE_IMAGE显示拍照模式 3、TYPE_VIDEO显示录像模式
     *
     * @param outputCameraPath 拍摄内容保存路径，如果值传空，也视为不开启拍摄
     * @return
     */
    public PictureOptions isShowCamera(String outputCameraPath) {
        pictureOptionsBean.outputCameraPath = outputCameraPath;
        return this;
    }

    /**
     * 设置显示指定的文件夹里面的图片（比如需要显示微信对应的文件夹里面的文件 appointFolderName--WeiXin）
     *
     * @param appointFolderName 文件夹名称
     * @param appointShowName   将指定文件夹名称显示成指定名称
     * @return
     */
    public PictureOptions appointFolderName(String appointFolderName, String appointShowName) {
        pictureOptionsBean.appointFolderName = appointFolderName;
        pictureOptionsBean.appointShowName = appointShowName;
        return this;
    }

    /**
     * 设置多选模式下最大选择数,多选模式下有效
     *
     * @param maxSelectNum
     * @return
     */
    public PictureOptions maxSelectNum(int maxSelectNum) {
        pictureOptionsBean.maxSelectNum = maxSelectNum;
        return this;
    }

    /**
     * 设置多选模式下最小选择数,多选模式下有效
     *
     * @param minSelectNum
     * @return
     */
    public PictureOptions minSelectNum(int minSelectNum) {
        pictureOptionsBean.minSelectNum = minSelectNum;
        return this;
    }

    /**
     * 设置视频筛选最大时间，只有当TYPE_ALL、TYPE_VIDEO才有效
     *
     * @param videoMaxSecond
     * @return
     */
    public PictureOptions videoMaxSecond(int videoMaxSecond) {
        pictureOptionsBean.videoMaxS = videoMaxSecond * 1000;
        return this;
    }

    /**
     * 设置视频筛选最小时间，只有当TYPE_ALL、TYPE_VIDEO才有效
     *
     * @param videoMinSecond
     * @return
     */
    public PictureOptions videoMinSecond(int videoMinSecond) {
        pictureOptionsBean.videoMinS = videoMinSecond * 1000;
        return this;
    }

    /**
     * 设置是否显示gif图片，只有当TYPE_ALL、TYPE_IMAGE才有效
     *
     * @param isGif
     * @return
     */
    public PictureOptions isGif(boolean isGif) {
        pictureOptionsBean.isGif = isGif;
        return this;
    }

    /**
     * 设置每行显示的图片列数
     *
     * @param spanCount
     * @return
     */
    public PictureOptions spanCount(int spanCount) {
        pictureOptionsBean.spanCount = spanCount;
        return this;
    }

    /**
     * 设置主题样式（请参照默认主题样式进行配置）
     *
     * @param themeStyle
     * @return
     */
    public PictureOptions themeStyle(PictureSelector.ThemeStyle themeStyle) {
        pictureOptionsBean.themeStyleId = themeStyle.getValue();
        return this;
    }

    /**
     * 设置选择模式MULTIPLE（多选）、SINGLE（单选）、NONE（不选）
     *
     * @param selectMode
     * @return
     */
    public PictureOptions selectMode(PictureSelector.SelectMode selectMode) {
        pictureOptionsBean.selectMode = selectMode.getValue();
        return this;
    }

    /**
     * 设置是否可以同时选择图片和视频，只有当TYPE_ALL才有效
     *
     * @param isSelectImageVideo
     * @return
     */
    public PictureOptions isSelectImageVideo(boolean isSelectImageVideo) {
        pictureOptionsBean.isSelectImageVideo = isSelectImageVideo;
        return this;
    }

    /**
     * 设置拍摄时长，只有当TYPE_VIDEO并且开启了拍摄按钮后才有效
     *
     * @param recordVideoSecond
     * @return
     */
    public PictureOptions recordVideoSecond(int recordVideoSecond) {
        pictureOptionsBean.recordVideoSecond = recordVideoSecond;
        return this;
    }

    /**
     * 设置拍摄质量0 or 1，只有当TYPE_VIDEO并且开启了拍摄按钮后才有效
     *
     * @param videoQuality
     * @return
     */
    public PictureOptions videoQuality(int videoQuality) {
        pictureOptionsBean.videoQuality = videoQuality;
        return this;
    }

    /**
     * 设置是否预览
     *
     * @param isPreview
     * @return
     */
    public PictureOptions isPreview(boolean isPreview) {
        pictureOptionsBean.isPreview = isPreview;
        return this;
    }

    /**
     * 设置指定格式的文件，只有当TYPE_IMAGE、TYPE_VIDEO才有效
     *
     * @param specifiedFormat
     * @return
     */
    public PictureOptions specifiedFormat(String... specifiedFormat) {
        pictureOptionsBean.specifiedFormat = specifiedFormat;
        return this;
    }

    /**
     * 设置权限被拒绝后的回调
     *
     * @param onPermissionDenied
     * @return
     */
    public PictureOptions permissionDeniedCallback(OnPermissionDenied onPermissionDenied) {
        if (onPermissionDenied != null) {
            pictureOptionsBean.onPermissionDenied = new WeakReference<>(onPermissionDenied).get();
        }
        return this;
    }

    /**
     * 开启图片选择页面
     */
    public void forResult() {
        forResult(PictureConfig.CHOOSE_REQUEST, null);
    }

    /**
     * 开启图片选择页面
     */
    public void forResult(int requestCode) {
        forResult(requestCode, null);
    }

    /**
     * 开启图片选择页面
     */
    public void forResult(OnPictureSelectResult onPictureSelectResult) {
        forResult(PictureConfig.CHOOSE_REQUEST, onPictureSelectResult);
    }

    /**
     * 开启图片选择页面
     */
    public void forResult(int requestCode, OnPictureSelectResult onPictureSelectResult) {
        if (!CommonUtil.isFastClick()) {
            if (pictureOptionsBean == null) {
                return;
            }
            if (onPictureSelectResult != null) {
                pictureOptionsBean.onPictureSelectResult = new WeakReference<>(onPictureSelectResult).get();
            }
            PictureConfig.CHOOSE_REQUEST = requestCode;
            PictureSelectorActivity.openActivity(pictureSelector.getActivity(), pictureSelector.getFragment(), PictureConfig.CHOOSE_REQUEST);
        }
    }

    /**
     * 权限拒绝回调
     */
    public interface OnPermissionDenied {
        void onDenied(List<String> permissions, boolean isNever);
    }

    /**
     * 图片选择回调
     */
    public interface OnPictureSelectResult {
        void onResult(ArrayList<LoadMediaBean> selectList);
    }
}
