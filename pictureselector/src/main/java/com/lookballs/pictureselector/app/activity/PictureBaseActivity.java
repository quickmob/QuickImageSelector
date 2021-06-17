package com.lookballs.pictureselector.app.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.blankj.utilcode.util.BarUtils;
import com.lookballs.pictureselector.R;
import com.lookballs.pictureselector.app.bean.LoadMediaBean;
import com.lookballs.pictureselector.config.PictureConfig;
import com.lookballs.pictureselector.config.PictureOptionsBean;
import com.lookballs.pictureselector.helper.PictureHelper;
import com.lookballs.pictureselector.util.AttrsUtil;

import java.util.ArrayList;

public abstract class PictureBaseActivity extends AppCompatActivity {

    //自动改变字体颜色的临界值标识位
    private final int IMMERSION_BOUNDARY_COLOR = 0xFFBABABA;

    protected PictureOptionsBean optionsBean;
    protected boolean isCheckNumMode = false;
    protected boolean isImmersionbarEnable = true;
    private int statusBarColor = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            optionsBean = savedInstanceState.getParcelable(PictureConfig.EXTRA_CONFIG);
        } else {
            optionsBean = PictureOptionsBean.getInstance();
        }
        if (this instanceof PictureSelectorActivity || this instanceof PicturePreviewActivity) {
            int themeStyleId = optionsBean.themeStyleId;
            setTheme(themeStyleId);
        }
        super.onCreate(savedInstanceState);
        initConfig();

        if (getLayoutId() > 0) {
            setContentView(getLayoutId());

            if (isImmersionbar()) {
                immersionbar();
            }
        }
    }

    /**
     * 获取配置参数
     */
    private void initConfig() {
        try {
            if (this instanceof PictureSelectorActivity) {
                statusBarColor = AttrsUtil.getTypeValueResourceId(this, R.attr.picture_ps_titleBg_color);
            } else if (this instanceof PicturePreviewActivity) {
                statusBarColor = AttrsUtil.getTypeValueResourceId(this, R.attr.picture_pp_titleBg_color);
            }
            isCheckNumMode = AttrsUtil.getTypeValueBoolean(this, R.attr.picture_checkNumMode);
            isImmersionbarEnable = AttrsUtil.getTypeValueBoolean(this, R.attr.picture_isImmersionbarEnable);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (statusBarColor == 0) {
            statusBarColor = R.color.picture_color_000000;
        }
    }

    protected abstract int getLayoutId();

    public boolean isImmersionbar() {
        return isImmersionbarEnable;
    }

    /**
     * 具体沉浸的样式，可以根据需要自行修改状态栏和导航栏的颜色
     */
    public void immersionbar() {
        try {
            int color = ContextCompat.getColor(this, statusBarColor);
            BarUtils.addMarginTopEqualStatusBarHeight(findViewById(R.id.titlebar_rl));//其实这个只需要调用一次即可
            BarUtils.setStatusBarColor(this, color);
            BarUtils.setStatusBarLightMode(this, color > IMMERSION_BOUNDARY_COLOR);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(PictureConfig.EXTRA_CONFIG, optionsBean);
    }

    private void release() {
        if (optionsBean != null) {
            PictureOptionsBean.destroy();
        }
    }

    protected void onSelectResult(ArrayList<LoadMediaBean> images) {
        if (PictureOptionsBean.onPictureSelectResult != null) {
            PictureOptionsBean.onPictureSelectResult.onResult(images);
        } else {
            Intent intent = PictureHelper.putIntentSelectResult(images);
            setResult(Activity.RESULT_OK, intent);
        }
        finishPage();
    }

    protected void onCameraResult(ArrayList<LoadMediaBean> images) {
        if (PictureOptionsBean.onCameraResult != null) {
            PictureOptionsBean.onCameraResult.onResult(images);
        } else {
            Intent intent = PictureHelper.putIntentCameraResult(images);
            setResult(Activity.RESULT_OK, intent);
        }
        finishPage();
    }

    protected void finishPage() {
        release();
        finish();
    }
}
