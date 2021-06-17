package com.lookballs.pictureselector.util;

import android.app.Activity;
import android.content.Intent;
import android.provider.MediaStore;
import android.widget.Toast;

import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.UriUtils;
import com.lookballs.pictureselector.R;
import com.lookballs.pictureselector.config.PictureConfig;

import java.io.File;

/**
 * 拍照相关
 */
public class TakePhotoUtil {

    /**
     * 打开相机拍照
     */
    public static boolean openCamera(Activity mActivity, String outputCameraPath) {
        File dir = new File(outputCameraPath);
        if (!dir.exists()) {
            FileUtils.createOrExistsFile(dir);
        }

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, UriUtils.file2Uri(FileUtils.getFileByPath(outputCameraPath)));

        if (intent.resolveActivity(mActivity.getApplicationContext().getPackageManager()) == null) {
            Toast.makeText(mActivity.getApplicationContext(), mActivity.getApplicationContext().getResources().getString(R.string.picture_toast_take_photo_none), Toast.LENGTH_SHORT).show();
            return false;
        }
        mActivity.startActivityForResult(intent, PictureConfig.CAMERA_REQUEST);
        return true;
    }

    /**
     * 打开相机录像
     */
    public static boolean openCamera(Activity mActivity, String outputCameraPath, int recordVideoSecond, int videoQuality) {
        File dir = new File(outputCameraPath);
        if (!dir.exists()) {
            FileUtils.createOrExistsFile(dir);
        }

        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, UriUtils.file2Uri(FileUtils.getFileByPath(outputCameraPath)));
        intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, recordVideoSecond);
        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, videoQuality);

        if (intent.resolveActivity(mActivity.getApplicationContext().getPackageManager()) == null) {
            Toast.makeText(mActivity.getApplicationContext(), mActivity.getApplicationContext().getResources().getString(R.string.picture_toast_take_photo_none), Toast.LENGTH_SHORT).show();
            return false;
        }
        mActivity.startActivityForResult(intent, PictureConfig.CAMERA_REQUEST);
        return true;
    }
}
