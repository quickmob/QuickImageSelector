package com.lookballs.pictureselector.model;

import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;

import androidx.fragment.app.FragmentActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

import com.lookballs.pictureselector.app.bean.LoadMediaBean;
import com.lookballs.pictureselector.app.bean.LoadMediaFolderBean;
import com.lookballs.pictureselector.config.PictureConfig;
import com.lookballs.pictureselector.config.PictureOptionsBean;
import com.lookballs.pictureselector.helper.PictureHelper;

import java.util.ArrayList;
import java.util.Locale;

public class MediaLoader {
    private final Uri QUERY_URI = MediaStore.Files.getContentUri("external");//查询uri
    private final String ORDER_BY = MediaStore.Files.FileColumns._ID + " DESC";//排序
    private final String COLUMN_DURATION = "duration";//视频文件时长
    private final String NOT_GIF = MediaStore.MediaColumns.MIME_TYPE + "!='image/gif'";//图片不查询gif图片类型

    private FragmentActivity activity;
    private PictureOptionsBean optionsBean;

    public MediaLoader(FragmentActivity activity) {
        this.activity = activity;
        this.optionsBean = PictureOptionsBean.getInstance();
    }

    //媒体文件数据库字段
    private final String[] PROJECTION = {
            MediaStore.Files.FileColumns._ID,
            MediaStore.MediaColumns.DATA,
            MediaStore.MediaColumns.MIME_TYPE,
            MediaStore.MediaColumns.WIDTH,
            MediaStore.MediaColumns.HEIGHT,
            COLUMN_DURATION
    };

    //公共查询条件
    private final String COMMON_SELECTION = MediaStore.Files.FileColumns.MEDIA_TYPE + "=?" + " AND " + MediaStore.MediaColumns.SIZE + ">0";

    //公共查询条件（指定条件）
    private String getCommonSelectionCondition(String condition) {
        return COMMON_SELECTION + " AND " + condition;
    }

    //查询条件（图片、视频）
    private String getAllSelectionCondition(String time_condition, boolean isGif) {
        return "("
                + MediaStore.Files.FileColumns.MEDIA_TYPE + "=?"
                + (isGif ? "" : " AND " + NOT_GIF)
                + " OR "
                + (MediaStore.Files.FileColumns.MEDIA_TYPE + "=?"
                + " AND " + time_condition)
                + ")"
                + " AND "
                + MediaStore.MediaColumns.SIZE + ">0";
    }

    //获取视频的最长或最小时间
    private String getDurationCondition(long exMaxLimit, long exMinLimit) {
        long maxS = optionsBean.videoMaxS == 0 ? Long.MAX_VALUE : optionsBean.videoMaxS;
        if (exMaxLimit != 0) maxS = Math.min(maxS, exMaxLimit);

        return String.format(Locale.CHINA, "%d <%s duration AND duration <= %d",
                Math.max(exMinLimit, optionsBean.videoMinS),
                Math.max(exMinLimit, optionsBean.videoMinS) == 0 ? "" : "=",
                maxS);
    }

    public void loadAll(final LoadMediaCallback loadMediaCallback) {
        LoaderManager.getInstance(activity).initLoader(optionsBean.mimeType, null, new LoaderManager.LoaderCallbacks<Cursor>() {
            @Override
            public Loader<Cursor> onCreateLoader(int id, Bundle args) {
                CursorLoader cursorLoader = null;
                switch (id) {
                    case PictureConfig.TYPE_ALL:
                        //获取图片、视频
                        String allSelection = getAllSelectionCondition(getDurationCondition(0, 0), optionsBean.isGif);
                        String[] allSelectionArgs = new String[]{String.valueOf(MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE), String.valueOf(MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO)};
                        cursorLoader = new CursorLoader(activity, QUERY_URI, PROJECTION, allSelection, allSelectionArgs, ORDER_BY);
                        break;
                    case PictureConfig.TYPE_IMAGE:
                        //只获取图片
                        String imageSelection = COMMON_SELECTION;
                        String imageNotGifSelection = getCommonSelectionCondition(NOT_GIF);
                        String[] imageSelectionArgs = new String[]{String.valueOf(MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE)};
                        cursorLoader = new CursorLoader(activity, QUERY_URI, PROJECTION, optionsBean.isGif ? imageSelection : imageNotGifSelection, imageSelectionArgs, ORDER_BY);
                        break;
                    case PictureConfig.TYPE_VIDEO:
                        //只获取视频
                        String videoSelection = getCommonSelectionCondition(getDurationCondition(0, 0));
                        String[] videoSelectionArgs = new String[]{String.valueOf(MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO)};
                        cursorLoader = new CursorLoader(activity, QUERY_URI, PROJECTION, videoSelection, videoSelectionArgs, ORDER_BY);
                        break;
                }
                return cursorLoader;
            }

            @Override
            public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
                try {
                    ArrayList<LoadMediaFolderBean> allImageFolders = new ArrayList<>();
                    ArrayList<LoadMediaBean> allImages = new ArrayList<>();

                    if (cursor != null && cursor.moveToFirst()) {
                        if (cursor.getCount() > 0) {
                            do {
                                String path = cursor.getString(cursor.getColumnIndexOrThrow(PROJECTION[1]));
                                String pictureType = cursor.getString(cursor.getColumnIndexOrThrow(PROJECTION[2]));
                                int w = cursor.getInt(cursor.getColumnIndexOrThrow(PROJECTION[3]));
                                int h = cursor.getInt(cursor.getColumnIndexOrThrow(PROJECTION[4]));
                                int duration = cursor.getInt(cursor.getColumnIndexOrThrow(PROJECTION[5]));
                                //将扫描到的文件信息加入到allImages中
                                LoadMediaBean image = new LoadMediaBean(path, duration, optionsBean.mimeType, pictureType, w, h);
                                allImages.add(image);
                                //获取对应文件的文件夹信息并加入到allImageFolders中
                                LoadMediaFolderBean imageFolder = PictureHelper.getImageFolder(path, allImageFolders);
                                imageFolder.setImageNum(imageFolder.getImageNum() + 1);
                                //将扫描到的文件信息加入到allImageFolders中
                                ArrayList<LoadMediaBean> images = imageFolder.getImages();
                                images.add(image);
                            } while (cursor.moveToNext());
                            //获取到所有文件信息后将文件放入一个虚拟文件夹
                            if (allImages.size() > 0) {
                                PictureHelper.sortFolder(allImageFolders);
                                LoadMediaFolderBean allImageFolder = new LoadMediaFolderBean();
                                allImageFolder.setImageNum(allImages.size());
                                allImageFolder.setFirstImagePath(allImages.get(0).getPath());
                                allImageFolder.setName(PictureHelper.getTitleText(activity, optionsBean.mimeType));
                                allImageFolder.setImages(allImages);
                                allImageFolders.add(0, allImageFolder);
                            }
                            loadMediaCallback.loadComplete(allImageFolders);
                        } else {
                            loadMediaCallback.loadComplete(allImageFolders);
                        }
                    }
                } catch (Exception e) {
                    loadMediaCallback.loadError(e);
                } finally {
                    if (cursor != null) {
                        //在android 4.0及其以上的版本中，Cursor会自动关闭，不需要用户自己关闭。
                        //否则会报android.database.StaleDataException: Attempted to access a cursor after it has been closed错误
                        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                            cursor.close();
                        }
                    }
                }
                //解决后台返回到页面时再次被调用问题
                LoaderManager.getInstance(activity).destroyLoader(optionsBean.mimeType);
            }

            @Override
            public void onLoaderReset(Loader<Cursor> loader) {

            }
        });
    }

    public interface LoadMediaCallback {
        void loadComplete(ArrayList<LoadMediaFolderBean> folders);

        void loadError(Exception exception);
    }
}
