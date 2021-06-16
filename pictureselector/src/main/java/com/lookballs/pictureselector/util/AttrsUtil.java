package com.lookballs.pictureselector.util;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.TypedValue;

/**
 * style动态获取
 */
public class AttrsUtil {

    /**
     * get attrs ResourceId
     *
     * @param context
     * @param attr
     * @return
     */
    public static int getTypeValueResourceId(Context context, int attr) {
        TypedValue typedValue = new TypedValue();
        int[] attribute = new int[]{attr};
        TypedArray array = context.obtainStyledAttributes(typedValue.resourceId, attribute);
        int resourceId = array.getResourceId(0, 0);
        array.recycle();
        return resourceId;
    }

    /**
     * attrs status color or black
     *
     * @param context
     * @param attr
     * @return
     */
    public static boolean getTypeValueBoolean(Context context, int attr) {
        TypedValue typedValue = new TypedValue();
        int[] attribute = new int[]{attr};
        TypedArray array = context.obtainStyledAttributes(typedValue.resourceId, attribute);
        boolean statusFont = array.getBoolean(0, false);
        array.recycle();
        return statusFont;
    }

    /**
     * attrs drawable
     *
     * @param context
     * @param attr
     * @return
     */
    public static Drawable getTypeValueDrawable(Context context, int attr) {
        TypedValue typedValue = new TypedValue();
        int[] attribute = new int[]{attr};
        TypedArray array = context.obtainStyledAttributes(typedValue.resourceId, attribute);
        Drawable drawable = array.getDrawable(0);
        array.recycle();
        return drawable;
    }

}
