package com.lookballs.pictureselector.helper;

import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.lookballs.pictureselector.util.SdkVersionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PermissionsHelper {

    private int mRequestCode;
    private FragmentActivity mActivity;
    private List<String> mPermissions;
    private PermissionsCallback mCallback;

    private PermissionsHelper(FragmentActivity activity) {
        this.mRequestCode = getRandomRequestCode();
        this.mActivity = activity;
    }

    public static PermissionsHelper create(FragmentActivity activity) {
        return new PermissionsHelper(activity);
    }

    public PermissionsHelper permission(String... permissions) {
        if (mPermissions == null) {
            mPermissions = asArrayList(permissions);
        } else {
            mPermissions.addAll(asArrayList(permissions));
        }
        return this;
    }

    public PermissionsHelper permission(String[]... permissions) {
        if (mPermissions == null) {
            int length = 0;
            for (String[] permission : permissions) {
                length += permission.length;
            }
            mPermissions = new ArrayList<>(length);
        }
        for (String[] group : permissions) {
            mPermissions.addAll(asArrayList(group));
        }
        return this;
    }

    public void request(PermissionsCallback callback) {
        //如果传入 Activity 为空或者 Activity 状态非法则直接屏蔽这次权限申请
        if (mActivity == null || mActivity.isFinishing() || (SdkVersionUtils.isAndroid_J() && mActivity.isDestroyed())) {
            return;
        }

        //必须要传入权限或者权限组才能申请权限
        if (mPermissions == null || mPermissions.isEmpty()) {
            throw new NullPointerException("The request permission cannot be empty");
        }

        mCallback = callback;

        //判断这些权限已经全部授予过
        if (isGrantedPermission(mPermissions)) {
            if (mCallback != null) {
                mCallback.onGranted(mPermissions, true);
            }
            return;
        }

        //发起权限请求
        ActivityCompat.requestPermissions(mActivity, list2Array(mPermissions), mRequestCode);
    }

    /**
     * 回调
     */
    public interface PermissionsCallback {
        //授权成功：isAll=true代表全部权限授权了，isAll=false代表部分权限授权了
        void onGranted(List<String> permissions, boolean isAll);

        //授权拒绝：isNever=true代表全部权限被永久拒绝授权了，isNever=false代表全部权限被拒绝授权了
        void onDenied(List<String> permissions, boolean isNever);
    }

    /**
     * 权限授权回调，请在FragmentActivity中配置
     */
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == mRequestCode) {
            List<String> grantList = getGrantedPermissions(permissions, grantResults);
            if (grantList.size() == permissions.length) {
                if (mCallback != null) {
                    mCallback.onGranted(grantList, true);
                }
            } else {
                List<String> deniedList = getDeniedPermissions(permissions, grantResults);
                if (mCallback != null) {
                    mCallback.onDenied(deniedList, isPermissionNever(deniedList));
                    if (!grantList.isEmpty()) {
                        mCallback.onGranted(grantList, false);
                    }
                }
            }
        }
    }

    /**
     * 在权限组中检查是否都已经授权
     *
     * @param permissions 权限组
     */
    private boolean isGrantedPermission(List<String> permissions) {
        for (String permission : permissions) {
            if (isPermissionDenied(permission)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 获取已授予的权限
     *
     * @param permissions  需要请求的权限组
     * @param grantResults 允许结果组
     */
    private List<String> getGrantedPermissions(String[] permissions, int[] grantResults) {
        List<String> grantedPermissions = new ArrayList<>();
        for (int i = 0; i < grantResults.length; i++) {
            //把授予过的权限加入到集合中
            if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                grantedPermissions.add(permissions[i]);
            }
        }
        return grantedPermissions;
    }

    /**
     * 获取没有授予的权限
     *
     * @param permissions  需要请求的权限组
     * @param grantResults 允许结果组
     */
    private List<String> getDeniedPermissions(String[] permissions, int[] grantResults) {
        List<String> deniedPermissions = new ArrayList<>();
        for (int i = 0; i < grantResults.length; i++) {
            //把没有授予过的权限加入到集合中
            if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                deniedPermissions.add(permissions[i]);
            }
        }
        return deniedPermissions;
    }

    /**
     * 在权限组中检查是否有某个权限是否被永久拒绝
     *
     * @param permissions 权限组
     */
    private boolean isPermissionNever(List<String> permissions) {
        for (String permission : permissions) {
            if (isPermissionNever(permission)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 检查权限是否被永久拒绝
     *
     * @param permission 权限
     */
    private boolean isPermissionNever(String permission) {
        return isPermissionDenied(permission) && (!ActivityCompat.shouldShowRequestPermissionRationale(mActivity, permission));
    }

    /**
     * 检查权限是否被拒绝
     *
     * @param permission 权限
     */
    private boolean isPermissionDenied(String permission) {
        return ContextCompat.checkSelfPermission(mActivity, permission) != PackageManager.PERMISSION_GRANTED;
    }

    /**
     * 获得随机的 RequestCode
     */
    private int getRandomRequestCode() {
        //请求码必须在 2 的 16 次方以内
        return new Random().nextInt((int) Math.pow(2, 16));
    }

    /**
     * 将数组转换成 ArrayList
     * 这里解释一下为什么不用 Arrays.asList
     * 第一是返回的类型不是 java.util.ArrayList 而是 java.util.Arrays.ArrayList
     * 第二是返回的 ArrayList 对象是只读的，也就是不能添加任何元素，否则会抛异常
     */
    private <T> ArrayList<T> asArrayList(T... array) {
        ArrayList<T> list = null;
        if (array != null) {
            list = new ArrayList<>(array.length);
            for (T t : array) {
                list.add(t);
            }
        }
        return list;
    }

    /**
     * list转数组
     */
    private String[] list2Array(List<String> list) {
        if (list != null) {
            return list.toArray(new String[list.size() - 1]);
        }
        return null;
    }
}
