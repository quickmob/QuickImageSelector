package com.lookballs.app.pictureselector;

import android.content.Context;
import android.os.Build;
import android.os.Environment;

import com.lookballs.pictureselector.PictureSelector;

import java.io.File;

public class CommonHelper {

    /**
     * 创建拍照输出目录
     *
     * @return
     */
    public static String createCameraOutPath(Context context, PictureSelector.Choose choose) {
        String fileName = System.currentTimeMillis() + (choose == PictureSelector.Choose.TYPE_VIDEO ? ".mp4" : ".jpg");
        File customFile;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            //在Android Q上不能直接使用外部存储目录；且沙盒内的资源是无法通过PictureSelector扫描出来的
            File externalFilesDir = context.getExternalFilesDir(choose == PictureSelector.Choose.TYPE_VIDEO ? Environment.DIRECTORY_MOVIES : Environment.DIRECTORY_PICTURES);
            customFile = new File(externalFilesDir.getAbsolutePath(), "PictureSelector");
            if (!customFile.exists()) {
                customFile.mkdirs();
            }
        } else {
            File rootFile = Environment.getExternalStorageDirectory();
            customFile = new File(rootFile.getAbsolutePath() + File.separator + "PictureSelector");
            if (!customFile.exists()) {
                customFile.mkdirs();
            }
        }
        return customFile.getAbsolutePath() + File.separator + fileName;
    }

}
