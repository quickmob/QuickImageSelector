package com.lookballs.pictureselector.model;

import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;

import androidx.fragment.app.FragmentActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

import com.lookballs.pictureselector.app.bean.LoadMediaBean;
import com.lookballs.pictureselector.app.bean.LoadMediaFolderBean;
import com.lookballs.pictureselector.config.PictureConfig;
import com.lookballs.pictureselector.config.PictureOptionsBean;
import com.lookballs.pictureselector.helper.PictureHelper;
import com.lookballs.pictureselector.util.ThreadUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MediaLoaderV2 {
    private final Uri QUERY_URI = MediaStore.Files.getContentUri("external");//查询uri
    private final String ORDER_BY = MediaStore.Files.FileColumns._ID + " DESC";//排序
    private final String NOT_GIF = MediaStore.MediaColumns.MIME_TYPE + "!='image/gif'";//图片不查询gif图片类型
    private final String NOT_GIF_UNKNOWN = NOT_GIF + " AND " + MediaStore.MediaColumns.MIME_TYPE + "!='image/*'";//图片不查询gif图片类型和非图片类型
    private final String MEDIA_TYPE = MediaStore.Files.FileColumns.MEDIA_TYPE;//媒体类型名
    private final int MEDIA_TYPE_IMAGE = MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE;//图片类型
    private final int MEDIA_TYPE_VIDEO = MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO;//视频类型

    private FragmentActivity activity;
    private PictureOptionsBean optionsBean;

    public MediaLoaderV2(FragmentActivity activity) {
        this.activity = activity;
        this.optionsBean = PictureOptionsBean.getInstance();
    }

    //媒体文件数据库字段
    private final String[] PROJECTION = {
            MediaStore.Files.FileColumns._ID,//0
            MediaStore.MediaColumns.DATA,//1
            MediaStore.MediaColumns.MIME_TYPE,//2
            MediaStore.MediaColumns.WIDTH,//3
            MediaStore.MediaColumns.HEIGHT,//4
            MediaStore.MediaColumns.DURATION,//5
            MediaStore.MediaColumns.BUCKET_ID,//6*
            MediaStore.MediaColumns.BUCKET_DISPLAY_NAME,//7
            MediaStore.MediaColumns.DISPLAY_NAME,//8
            MediaStore.MediaColumns.SIZE,//9
            MediaStore.MediaColumns.DATE_ADDED,//10
    };

    //公共查询条件
    private final String COMMON_SELECTION = MEDIA_TYPE + "=?" + " AND " + PROJECTION[9] + ">0";

    //公共查询条件（指定条件）
    private String getCommonSelectionCondition(String condition) {
        String specifiedFormat_condition = getSpecifiedFormatCondition();
        return COMMON_SELECTION
                + (TextUtils.isEmpty(condition) ? "" : " AND " + condition)
                + (TextUtils.isEmpty(specifiedFormat_condition) ? "" : " AND " + "(" + specifiedFormat_condition + ")");
    }

    //公共查询条件（指定条件）
    private String getCommonBidSelectionCondition(String condition) {
        return "(" + getCommonSelectionCondition(condition) + ")"
                + " AND " + PROJECTION[6] + "=?";
    }

    //查询条件（图片、视频）
    private String getAllSelectionCondition(boolean isLoadAll, String duration_condition, boolean isGif) {
        return "("
                + MEDIA_TYPE + "=?"
                + (isGif ? "" : " AND " + NOT_GIF_UNKNOWN)
                + " OR "
                + (MEDIA_TYPE + "=?"
                + " AND " + duration_condition)
                + ")"
                + " AND " + PROJECTION[9] + ">0"
                + (isLoadAll ? "" : " AND " + PROJECTION[6] + "=?");
    }

    //获取指定格式的文件
    private String getSpecifiedFormatCondition() {
        if (optionsBean.specifiedFormat != null && optionsBean.specifiedFormat.length > 0) {
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < optionsBean.specifiedFormat.length; i++) {
                if (!TextUtils.isEmpty(optionsBean.specifiedFormat[i])) {
                    if (i != optionsBean.specifiedFormat.length - 1) {
                        sb.append(PROJECTION[2]).append("=").append("'").append(optionsBean.specifiedFormat[i]).append("'").append(" OR ");
                    } else {
                        sb.append(PROJECTION[2]).append("=").append("'").append(optionsBean.specifiedFormat[i]).append("'");
                    }
                }
            }
            return sb.toString();
        }
        return "";
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

    //获取Android_Q的真实图片路径
    private String getRealPathAndroid_Q(long id) {
        return QUERY_URI.buildUpon().appendPath(String.valueOf(id)).build().toString();
    }

    //首次加载全部图片
    public void loadAll(final LoadMediaCallback loadMediaCallback) {
        loadManager(0, true, loadMediaCallback);
    }

    //根据bucketId加载对应的图片
    public void loadByBucketId(final long bucketId, final LoadMediaCallback loadMediaCallback) {
        if (bucketId == PictureConfig.DEFAULT_BUCKET_ID) {//加载全部
            loadAll(loadMediaCallback);
        } else {//加载bucketId下的
            loadManager(bucketId, false, loadMediaCallback);
        }
    }

    private void loadManager(final long bucketId, final boolean isLoadAll, final LoadMediaCallback loadMediaCallback) {
        loadMediaCallback.loadStart();
        LoaderManager.getInstance(activity).initLoader(optionsBean.mimeType, null, new LoaderManager.LoaderCallbacks<Cursor>() {
            @Override
            public Loader<Cursor> onCreateLoader(int id, Bundle args) {
                CursorLoader cursorLoader = null;
                if (isLoadAll) {
                    cursorLoader = getCursorLoader(id, 0, true);
                } else {
                    cursorLoader = getCursorLoader(id, bucketId, false);
                }
                return cursorLoader;
            }

            @Override
            public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
                workOnThread(cursor, isLoadAll, loadMediaCallback);
            }

            @Override
            public void onLoaderReset(Loader<Cursor> loader) {

            }
        });
    }

    private CursorLoader getCursorLoader(final int id, final long bucketId, final boolean isLoadAll) {
        CursorLoader cursorLoader = null;
        switch (id) {
            case PictureConfig.TYPE_ALL:
                //获取图片、视频
                String allSelection = getAllSelectionCondition(isLoadAll, getDurationCondition(0, 0), optionsBean.isGif);

                String[] allSelectionArgs;
                if (isLoadAll) {
                    allSelectionArgs = new String[]{String.valueOf(MEDIA_TYPE_IMAGE), String.valueOf(MEDIA_TYPE_VIDEO)};
                } else {
                    allSelectionArgs = new String[]{String.valueOf(MEDIA_TYPE_IMAGE), String.valueOf(MEDIA_TYPE_VIDEO), String.valueOf(bucketId)};
                }

                cursorLoader = new CursorLoader(activity, QUERY_URI, PROJECTION, allSelection, allSelectionArgs, ORDER_BY);
                break;
            case PictureConfig.TYPE_IMAGE:
                //只获取图片
                String imageSelection = isLoadAll ? getCommonSelectionCondition("") : getCommonBidSelectionCondition("");
                String imageNotGifSelection = isLoadAll ? getCommonSelectionCondition(NOT_GIF_UNKNOWN) : getCommonBidSelectionCondition(NOT_GIF_UNKNOWN);

                String[] imageSelectionArgs;
                if (isLoadAll) {
                    imageSelectionArgs = new String[]{String.valueOf(MEDIA_TYPE_IMAGE)};
                } else {
                    imageSelectionArgs = new String[]{String.valueOf(MEDIA_TYPE_IMAGE), String.valueOf(bucketId)};
                }

                cursorLoader = new CursorLoader(activity, QUERY_URI, PROJECTION, optionsBean.isGif ? imageSelection : imageNotGifSelection, imageSelectionArgs, ORDER_BY);
                break;
            case PictureConfig.TYPE_VIDEO:
                //只获取视频
                String videoSelection = isLoadAll ? getCommonSelectionCondition(getDurationCondition(0, 0)) : getCommonBidSelectionCondition(getDurationCondition(0, 0));

                String[] videoSelectionArgs;
                if (isLoadAll) {
                    videoSelectionArgs = new String[]{String.valueOf(MEDIA_TYPE_VIDEO)};
                } else {
                    videoSelectionArgs = new String[]{String.valueOf(MEDIA_TYPE_VIDEO), String.valueOf(bucketId)};
                }

                cursorLoader = new CursorLoader(activity, QUERY_URI, PROJECTION, videoSelection, videoSelectionArgs, ORDER_BY);
                break;
        }
        return cursorLoader;
    }

    private void workOnThread(final Cursor cursor, final boolean isLoadAll, final LoadMediaCallback loadMediaCallback) {
        ThreadUtil.postDelayed(new Runnable() {
            @Override
            public void run() {
                ThreadUtil.executeByIo(new ThreadUtil.SimpleTask<List<LoadMediaFolderBean>>() {
                    @Override
                    public List<LoadMediaFolderBean> doInBackground() throws Throwable {
                        return loadData(cursor, isLoadAll);
                    }

                    @Override
                    public void onSuccess(List<LoadMediaFolderBean> result) {
                        loadMediaCallback.loadComplete((ArrayList<LoadMediaFolderBean>) result);
                        loadMediaCallback.loadEnd();
                        //解决后台返回到页面时再次被调用问题
                        LoaderManager.getInstance(activity).destroyLoader(optionsBean.mimeType);
                    }

                    @Override
                    public void onFail(Throwable t) {
                        super.onFail(t);
                        loadMediaCallback.loadError(t);
                        loadMediaCallback.loadEnd();
                        //解决后台返回到页面时再次被调用问题
                        LoaderManager.getInstance(activity).destroyLoader(optionsBean.mimeType);
                    }
                });
            }
        }, isLoadAll ? 0 : 100);
    }

    private ArrayList<LoadMediaFolderBean> loadData(final Cursor cursor, final boolean isLoadAll) {
        //获取图片
        ArrayList<LoadMediaFolderBean> allImageFolders = new ArrayList<>();
        ArrayList<LoadMediaBean> allImages = new ArrayList<>();
        if (cursor != null && cursor.moveToFirst()) {
            if (cursor.getCount() > 0) {
                int idIndex = cursor.getColumnIndexOrThrow(PROJECTION[0]);
                int pathIndex = cursor.getColumnIndexOrThrow(PROJECTION[1]);
                int pictureTypeIndex = cursor.getColumnIndexOrThrow(PROJECTION[2]);
                int widthIndex = cursor.getColumnIndexOrThrow(PROJECTION[3]);
                int heightIndex = cursor.getColumnIndexOrThrow(PROJECTION[4]);
                int durationIndex = cursor.getColumnIndexOrThrow(PROJECTION[5]);
                int bucketIdIndex = cursor.getColumnIndexOrThrow(PROJECTION[6]);
                int bucketDisplayNameIndex = cursor.getColumnIndexOrThrow(PROJECTION[7]);
                int displayNameIndex = cursor.getColumnIndexOrThrow(PROJECTION[8]);
                int sizeIndex = cursor.getColumnIndexOrThrow(PROJECTION[9]);
                int dateAddedIndex = cursor.getColumnIndexOrThrow(PROJECTION[10]);

                do {
                    long id = cursor.getLong(idIndex);
                    //String path = SdkVersionUtils.isAndroid_Q() ? getRealPathAndroid_Q(id) : cursor.getString(pathIndex);
                    String path = cursor.getString(pathIndex);
                    String pictureType = cursor.getString(pictureTypeIndex);
                    int width = cursor.getInt(widthIndex);
                    int height = cursor.getInt(heightIndex);
                    int duration = cursor.getInt(durationIndex);
                    long bucketId = cursor.getLong(bucketIdIndex);
                    String bucketDisplayName = cursor.getString(bucketDisplayNameIndex);
                    String displayName = cursor.getString(displayNameIndex);
                    long size = cursor.getLong(sizeIndex);
                    long dateAdded = cursor.getLong(dateAddedIndex);

                    //将扫描到的文件信息加入到allImages中
                    LoadMediaBean image = new LoadMediaBean(id, path, duration, optionsBean.mimeType, pictureType, width, height, bucketId, bucketDisplayName, displayName, size, dateAdded);
                    allImages.add(image);
                    //获取对应文件的文件夹信息并加入到allImageFolders中
                    LoadMediaFolderBean imageFolder = PictureHelper.getImageFolder(path, allImageFolders);
                    imageFolder.setImageNum(imageFolder.getImageNum() + 1);
                    imageFolder.setBucketId(bucketId);
                    //将扫描到的文件信息加入到allImageFolders中
                    ArrayList<LoadMediaBean> images = imageFolder.getImages();
                    images.add(image);
                } while (cursor.moveToNext());

                //获取到的所有文件信息后将文件放入一个虚拟文件夹，也是默认文件夹
                if (isLoadAll) {
                    if (allImages.size() > 0) {
                        PictureHelper.sortFolder(allImageFolders);
                        LoadMediaFolderBean allImageFolder = new LoadMediaFolderBean();
                        allImageFolder.setImageNum(allImages.size());
                        allImageFolder.setBucketId(PictureConfig.DEFAULT_BUCKET_ID);
                        allImageFolder.setFirstImagePath(allImages.get(0).getPath());
                        allImageFolder.setName(PictureHelper.getTitleText(activity, optionsBean.mimeType));
                        allImageFolder.setImages(allImages);
                        allImageFolders.add(0, allImageFolder);
                    }
                }
            }
        }
        //关闭cursor
        if (cursor != null && !cursor.isClosed()) {
            //在android 4.0及其以上的版本中，Cursor会自动关闭，不需要用户自己关闭。
            //否则会报android.database.StaleDataException: Attempted to access a cursor after it has been closed错误
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                cursor.close();
            }
        }
        return allImageFolders;
    }

    public interface LoadMediaCallback {
        default void loadStart() {

        }

        void loadComplete(ArrayList<LoadMediaFolderBean> folders);

        default void loadError(Throwable t) {

        }

        default void loadEnd() {

        }
    }
}
