package com.lookballs.pictureselector.app.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.blankj.utilcode.util.FileUtils;
import com.lookballs.pictureselector.R;
import com.lookballs.pictureselector.app.bean.LoadMediaBean;
import com.lookballs.pictureselector.config.PictureConfig;
import com.lookballs.pictureselector.config.PictureOptionsBean;
import com.lookballs.pictureselector.helper.PermissionsHelper;
import com.lookballs.pictureselector.helper.PictureHelper;
import com.lookballs.pictureselector.util.CommonUtil;
import com.lookballs.pictureselector.util.TakePhotoUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class PictureCameraActivity extends PictureBaseActivity {

    //权限申请
    private String[] CAMERA_PERMISSIONS = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA};
    //权限申请类
    private PermissionsHelper permissionsHelper;

    private boolean isVideo;//是否拍摄视频：true拍视频false拍照

    private boolean isExternalEnter = false;//是否是外部调用进来

    public static void openActivity(Activity activity, Fragment fragment, boolean isExternalEnter) {
        if (activity != null) {
            Bundle bundle = new Bundle();
            bundle.putBoolean("isExternalEnter", isExternalEnter);
            Intent intent = new Intent(activity, PictureCameraActivity.class);
            intent.putExtras(bundle);
            if (fragment != null) {
                fragment.startActivityForResult(intent, PictureConfig.CAMERA_REQUEST);
            } else {
                activity.startActivityForResult(intent, PictureConfig.CAMERA_REQUEST);
            }
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        overridePendingTransition(0, 0);
        super.onCreate(savedInstanceState);

        setOnePxActivity();

        if (optionsBean.mimeType == PictureConfig.TYPE_VIDEO) {
            isVideo = true;
        } else {
            isVideo = false;
        }
        isExternalEnter = getIntent().getBooleanExtra("isExternalEnter", false);

        permissionsHelper = PermissionsHelper.create(this);
        permissionCarmera();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, 0);
    }

    @Override
    protected int getLayoutId() {
        return 0;
    }

    /**
     * 设置个1像素的Activity
     */
    private void setOnePxActivity() {
        Window window = getWindow();
        window.setGravity(Gravity.LEFT | Gravity.TOP);
        WindowManager.LayoutParams params = window.getAttributes();
        params.x = 0;
        params.y = 0;
        params.height = 1;
        params.width = 1;
        window.setAttributes(params);
    }

    /**
     * 请求拍照权限
     */
    private void permissionCarmera() {
        permissionsHelper
                .permission(CAMERA_PERMISSIONS)
                .request(new PermissionsHelper.PermissionsCallback() {
                    @Override
                    public void onGranted(List<String> permissions, boolean isAll) {
                        if (isAll) {
                            boolean result = false;
                            if (isVideo) {
                                result = TakePhotoUtil.openCamera(PictureCameraActivity.this, optionsBean.outputCameraPath, optionsBean.recordVideoSecond, optionsBean.videoQuality);
                            } else {
                                result = TakePhotoUtil.openCamera(PictureCameraActivity.this, optionsBean.outputCameraPath);
                            }
                            if (!result) {
                                finishPage();
                            }
                        }
                    }

                    @Override
                    public void onDenied(List<String> permissions, boolean isNever) {
                        if (isNever) {
                            Toast.makeText(PictureCameraActivity.this.getApplicationContext(), getString(R.string.picture_toast_permission_camera_never), Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(PictureCameraActivity.this.getApplicationContext(), getString(R.string.picture_toast_permission_camera), Toast.LENGTH_SHORT).show();
                        }
                        if (PictureOptionsBean.onPermissionDenied != null) {
                            PictureOptionsBean.onPermissionDenied.onDenied(permissions, isNever);
                        }
                        finishPage();
                    }
                });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (permissionsHelper != null) {
            permissionsHelper.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PictureConfig.CAMERA_REQUEST) {//拍照返回
            String outputPath = optionsBean.outputCameraPath;
            switch (resultCode) {
                case Activity.RESULT_OK:
                    //刷新图库
                    CommonUtil.scanDirAsync(this, new String[]{outputPath}, null);
                    //构造返回结果
                    File cameraFile = new File(outputPath);
                    ArrayList<LoadMediaBean> images = new ArrayList<>();
                    long id = -1;
                    String absolutePath = outputPath;
                    String realPath = outputPath;
                    String pictureType = optionsBean.mimeType == PictureConfig.TYPE_VIDEO ? PictureHelper.createVideoType(outputPath) : PictureHelper.createImageType(outputPath);
                    int width = -1;
                    int height = -1;
                    long duration = PictureHelper.getLocalVideoDuration(outputPath);
                    long bucketId = -1;
                    String bucketDisplayName = "";
                    String displayName = "";
                    long size = cameraFile.length();
                    long dateAdded = -1;
                    LoadMediaBean image = new LoadMediaBean(id, absolutePath, realPath, duration, optionsBean.mimeType, pictureType, width, height, bucketId, bucketDisplayName, displayName, size, dateAdded);
                    images.add(image);
                    //回调结果
                    if (isExternalEnter) {
                        onCameraResult(images);
                    } else {
                        Intent intent = PictureHelper.putIntentCameraResult(images);
                        setResult(Activity.RESULT_OK, intent);
                        finish();
                    }
                    break;
                case Activity.RESULT_CANCELED:
                    //删除这个文件
                    FileUtils.delete(outputPath);
                    if (isExternalEnter) {
                        finishPage();
                    } else {
                        finish();
                    }
                    break;
            }
        }
    }

}
