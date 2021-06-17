package com.lookballs.pictureselector.helper;

import android.content.Context;
import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.text.TextUtils;

import com.lookballs.pictureselector.R;
import com.lookballs.pictureselector.app.bean.LoadMediaBean;
import com.lookballs.pictureselector.app.bean.LoadMediaFolderBean;
import com.lookballs.pictureselector.config.PictureConfig;
import com.lookballs.pictureselector.util.SdkVersionUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public final class PictureHelper {

    private static int isPictureType(String pictureType) {
        String tempPictureType = pictureType.toUpperCase();
        switch (tempPictureType) {
            case "IMAGE/PNG":
            case "IMAGE/JPEG":
            case "IMAGE/JPG":
            case "IMAGE/WEBP":
            case "IMAGE/GIF":
            case "IMAGE/BMP":
                return PictureConfig.TYPE_IMAGE;
            case "VIDEO/3GP":
            case "VIDEO/AVI":
            case "VIDEO/MP4":
            case "VIDEO/MPEG":
                return PictureConfig.TYPE_VIDEO;
        }
        return PictureConfig.TYPE_IMAGE;
    }

    /**
     * 是否是gif
     *
     * @param pictureType
     * @return
     */
    public static boolean isGif(String pictureType) {
        String tempPictureType = pictureType.toUpperCase();
        switch (tempPictureType) {
            case "IMAGE/GIF":
                return true;
        }
        return false;
    }

    /**
     * 是否是gif
     *
     * @param path
     * @return
     */
    public static boolean isImageGif(String path) {
        if (!TextUtils.isEmpty(path)) {
            int lastIndex = path.lastIndexOf(".");
            if (lastIndex >= 0) {
                String pictureType = path.substring(lastIndex, path.length());
                return pictureType.startsWith(".gif") || pictureType.startsWith(".GIF");
            } else {
                return false;
            }
        }
        return false;
    }

    /**
     * 是否是视频
     *
     * @param pictureType
     * @return
     */
    public static boolean isVideo(String pictureType) {
        return isPictureType(pictureType) == PictureConfig.TYPE_VIDEO;
    }

    /**
     * 判断选择类型是否一样
     *
     * @param p1
     * @param p2
     * @return
     */
    public static boolean mimeToEqual(String p1, String p2) {
        return isPictureType(p1) == isPictureType(p2);
    }

    /**
     * 是否是网络图片
     *
     * @param path
     * @return
     */
    public static boolean isHttpImage(String path) {
        if (!TextUtils.isEmpty(path)) {
            if (path.startsWith("http") || path.startsWith("https")) {
                return true;
            }
        }
        return false;
    }

    /**
     * 创建一个图片格式类型
     *
     * @param path
     * @return
     */
    public static String createImageType(String path) {
        try {
            if (!TextUtils.isEmpty(path)) {
                File file = new File(path);
                String fileName = file.getName();
                int last = fileName.lastIndexOf(".") + 1;
                String temp = fileName.substring(last, fileName.length());
                return "image/" + temp;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "image/jpeg";
        }
        return "image/jpeg";
    }

    /**
     * 创建一个视频格式类型
     *
     * @param path
     * @return
     */
    public static String createVideoType(String path) {
        try {
            if (!TextUtils.isEmpty(path)) {
                File file = new File(path);
                String fileName = file.getName();
                int last = fileName.lastIndexOf(".") + 1;
                String temp = fileName.substring(last, fileName.length());
                return "video/" + temp;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "video/mp4";
        }
        return "video/mp4";
    }

    /**
     * 获取本地视频时长
     *
     * @param videoPath
     * @return
     */
    public static long getLocalVideoDuration(String videoPath) {
        long duration;
        try {
            MediaMetadataRetriever mmr = new MediaMetadataRetriever();
            mmr.setDataSource(videoPath);
            duration = Long.parseLong(mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION));
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
        return duration;
    }

    /**
     * 是否是长图
     *
     * @param media
     * @return true 是 or false 不是
     */
    public static boolean isLongImg(LoadMediaBean media) {
        if (null != media) {
            int width = media.getWidth();
            int height = media.getHeight();
            int h = width * 3;
            return height > h;
        }
        return false;
    }

    /**
     * 根据不同的类型，返回不同的错误提示
     *
     * @param mimeType
     * @return
     */
    public static String tipsFileError(Context context, int mimeType) {
        Context ctx = context.getApplicationContext();
        switch (mimeType) {
            case PictureConfig.TYPE_IMAGE:
                return ctx.getString(R.string.picture_toast_img_error);
            case PictureConfig.TYPE_VIDEO:
                return ctx.getString(R.string.picture_toast_video_error);
            default:
                return ctx.getString(R.string.picture_toast_img_error);
        }
    }

    /**
     * 判断文件路径
     *
     * @param filePath
     * @return
     */
    public static boolean isContent(String filePath) {
        if (TextUtils.isEmpty(filePath)) {
            return false;
        }
        return filePath.startsWith("content://");
    }

    /**
     * 根据不同的类型，返回不同的内容
     *
     * @param mimeType
     * @return
     */
    public static String getTitleText(Context context, int mimeType) {
        Context ctx = context.getApplicationContext();
        switch (mimeType) {
            case PictureConfig.TYPE_IMAGE:
                return ctx.getString(R.string.picture_text_img);
            case PictureConfig.TYPE_VIDEO:
                return ctx.getString(R.string.picture_text_video);
            default:
                return ctx.getString(R.string.picture_text_img_video);
        }
    }

    /**
     * 根据不同的类型，返回不同的内容
     *
     * @param mimeType
     * @return
     */
    public static String getTakePictureText(Context context, int mimeType) {
        Context ctx = context.getApplicationContext();
        switch (mimeType) {
            case PictureConfig.TYPE_IMAGE:
                return ctx.getString(R.string.picture_text_take_picture);
            case PictureConfig.TYPE_VIDEO:
                return ctx.getString(R.string.picture_text_take_video);
            default:
                return ctx.getString(R.string.picture_text_take_picture);
        }
    }

    /**
     * onCreate中取出之前保存的已选图片列表
     *
     * @param bundle
     * @return
     */
    public static ArrayList<LoadMediaBean> obtainSelectorList(Bundle bundle) {
        ArrayList<LoadMediaBean> selectionMedias;
        if (bundle != null) {
            selectionMedias = (ArrayList<LoadMediaBean>) bundle.getSerializable(PictureConfig.EXTRA_SELECT_LIST);
            return selectionMedias;
        }
        selectionMedias = new ArrayList<>();
        return selectionMedias;
    }

    /**
     * onSaveInstanceState保存已选图片列表
     *
     * @param outState
     * @param selectedImages
     */
    public static void saveSelectorList(Bundle outState, ArrayList<LoadMediaBean> selectedImages) {
        outState.putSerializable(PictureConfig.EXTRA_SELECT_LIST, selectedImages);
    }

    /**
     * 回调时将选择数据设置到intent
     *
     * @param data
     * @return
     */
    public static Intent putIntentSelectResult(ArrayList<LoadMediaBean> data) {
        return new Intent().putExtra(PictureConfig.EXTRA_RESULT_SELECT, data);
    }

    /**
     * 回调时将选择数据设置到intent
     *
     * @param data
     * @return
     */
    public static Intent putIntentCameraResult(ArrayList<LoadMediaBean> data) {
        return new Intent().putExtra(PictureConfig.EXTRA_RESULT_CAMERA, data);
    }

    /**
     * 文件夹数量进行排序
     *
     * @param imageFolders
     */
    public static void sortFolder(List<LoadMediaFolderBean> imageFolders) {
        // 文件夹按图片数量排序
        Collections.sort(imageFolders, new Comparator<LoadMediaFolderBean>() {
            @Override
            public int compare(LoadMediaFolderBean lhs, LoadMediaFolderBean rhs) {
                if (lhs.getImages() == null || rhs.getImages() == null) {
                    return 0;
                }
                int lsize = lhs.getImageNum();
                int rsize = rhs.getImageNum();
                return lsize == rsize ? 0 : (lsize < rsize ? 1 : -1);
            }
        });
    }

    /**
     * 获取图片文件夹
     *
     * @param filePath
     * @param folderName
     * @param folders
     * @return
     */
    public static LoadMediaFolderBean getImageFolder(String filePath, String folderName, List<LoadMediaFolderBean> folders) {
        String folderPath = null;
        if (!SdkVersionUtils.isAndroid_Q()) {
            try {
                folderPath = new File(filePath).getParentFile().getAbsolutePath();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            folderPath = "unknown";
        }

        LoadMediaFolderBean folderBean = getLoadMediaFolder(folders, folderName);
        if (folderBean != null) {
            return folderBean;
        } else {
            LoadMediaFolderBean newFolderBean = getNewLoadMediaFolder(folderName, folderPath, filePath);
            folders.add(newFolderBean);
            return newFolderBean;
        }
    }

    private static LoadMediaFolderBean getLoadMediaFolder(List<LoadMediaFolderBean> folders, String folderName) {
        for (LoadMediaFolderBean folderBean : folders) {
            //同一个文件夹下，返回自己，否则创建新文件夹
            if (folderBean.getName().equals(folderName)) {
                return folderBean;
            }
        }
        return null;
    }

    private static LoadMediaFolderBean getNewLoadMediaFolder(String folderName, String folderPath, String firstImagePath) {
        LoadMediaFolderBean newFolderBean = new LoadMediaFolderBean();
        newFolderBean.setName(folderName);
        newFolderBean.setPath(folderPath);
        newFolderBean.setFirstImagePath(firstImagePath);
        return newFolderBean;
    }
}
