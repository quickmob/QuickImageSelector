package com.lookballs.pictureselector.util;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.provider.Settings;
import android.widget.TextView;

/**
 * 公共
 */
public class CommonUtil {

    public enum DrawableDir {
        LEFT, TOP, RIGHT, BOTTOM
    }

    private static long lastClickTime;

    /**
     * 文本内容设置图标
     */
    public static void setCompoundDrawables(Context mContext, Object widget, int resourceId, DrawableDir drawableDir) {
        if (widget != null && widget instanceof TextView) {
            if (resourceId == 0) {
                ((TextView) widget).setCompoundDrawables(null, null, null, null);
                return;
            }
            Drawable drawable = mContext.getApplicationContext().getResources().getDrawable(resourceId);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            switch (drawableDir) {
                case LEFT:
                    ((TextView) widget).setCompoundDrawables(drawable, null, null, null);
                    break;
                case TOP:
                    ((TextView) widget).setCompoundDrawables(null, drawable, null, null);
                    break;
                case RIGHT:
                    ((TextView) widget).setCompoundDrawables(null, null, drawable, null);
                    break;
                case BOTTOM:
                    ((TextView) widget).setCompoundDrawables(null, null, null, drawable);
                    break;
            }
        }
    }

    public static boolean isFastClick() {
        long time = System.currentTimeMillis();
        if (time - lastClickTime < 800) {
            return true;
        }
        lastClickTime = time;
        return false;
    }

    /**
     * 跳转到系统应用详情页面Intent
     *
     * @param isNewTask 是否开启新的任务栈
     */
    public static Intent getApplicationDetailsIntent(Context context, boolean isNewTask) {
        Uri uri = Uri.parse("package:" + context.getPackageName());
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(uri);
        return getIntent(intent, isNewTask);
    }

    private static Intent getIntent(Intent intent, boolean isNewTask) {
        return isNewTask ? intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) : intent;
    }

    /**
     * 扫描指定文件夹并通知系统图库
     *
     * @param mContext
     */
    public static void scanDirAsync(Context mContext, String[] paths, String[] mineTypes) {
        try {
            MediaScannerConnection.scanFile(mContext.getApplicationContext(), paths, mineTypes, new MediaScannerConnection.OnScanCompletedListener() {
                @Override
                public void onScanCompleted(String path, Uri uri) {

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
