package com.lookballs.pictureselector.config;

public final class PictureConfig {
    public final static int TYPE_ALL = 0;
    public final static int TYPE_IMAGE = 1;
    public final static int TYPE_VIDEO = 2;

    public final static int TYPE_CAMERA = 1;
    public final static int TYPE_PICTURE = 2;

    public final static String EXTRA_SELECT_LIST = "selectList";
    public final static String EXTRA_RESULT_SELECT = "extra_result_select";
    public final static String EXTRA_RESULT_CAMERA = "extra_result_camera";
    public final static String EXTRA_CONFIG = "PictureOptionsBean";

    public final static String IMAGE = "image";
    public final static String VIDEO = "video";

    public static int CAMERA_REQUEST = 666;
    public static int CHOOSE_REQUEST = 999;

    public final static int UPDATE_FLAG = 9001;//预览界面更新选中数据标识
    public final static int PREVIEW_FLAG = 9002;//预览界面图片已完成标识

    public final static int NONE = 0;
    public final static int SINGLE = 1;
    public final static int MULTIPLE = 2;

    public final static long DEFAULT_BUCKET_ID = -999;
}
