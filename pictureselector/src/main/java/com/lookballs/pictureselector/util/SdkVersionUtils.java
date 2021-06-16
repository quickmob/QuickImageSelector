package com.lookballs.pictureselector.util;

import android.os.Build;

/**
 * Sdk版本判断
 */
public class SdkVersionUtils {

    /**
     * 判断是否是Android J版本
     *
     * @return
     */
    public static boolean isAndroid_J() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1;
    }

    /**
     * 判断是否是Android N版本
     *
     * @return
     */
    public static boolean isAndroid_N() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.N;
    }

    /**
     * 判断是否是Android Q版本
     *
     * @return
     */
    public static boolean isAndroid_Q() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q;
    }

    /**
     * 判断是否是Android R版本
     *
     * @return
     */
    public static boolean isAndroid_R() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.R;
    }
}
