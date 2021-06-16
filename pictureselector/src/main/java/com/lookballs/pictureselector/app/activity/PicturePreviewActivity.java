package com.lookballs.pictureselector.app.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

import com.lookballs.pictureselector.R;
import com.lookballs.pictureselector.app.adapter.PicturePreviewAdapter;
import com.lookballs.pictureselector.app.bean.EventBusBean;
import com.lookballs.pictureselector.app.bean.LoadMediaBean;
import com.lookballs.pictureselector.config.PictureConfig;
import com.lookballs.pictureselector.helper.PictureHelper;
import com.lookballs.pictureselector.helper.SaveDataHelper;
import com.lookballs.pictureselector.widget.BugViewPager;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;

public class PicturePreviewActivity extends PictureBaseActivity implements View.OnClickListener {

    //布局
    private ImageView backIv;
    private CheckBox checkCb;
    private TextView titleTv, okTv, checkNumTv;
    private LinearLayout checkLl;
    private BugViewPager photoVp;

    //其他
    private PicturePreviewAdapter mAdapter;
    private ArrayList<LoadMediaBean> mImgList;
    private ArrayList<LoadMediaBean> mSelectImgList;
    private int mPosition = 0;
    private boolean isClickPreView = false;

    public static void openActivity(Context mContext, ArrayList<LoadMediaBean> mImgList, ArrayList<LoadMediaBean> mSelectImgList, int mPosition, boolean isClickPreView) {
        Bundle bundle = new Bundle();
        if (isClickPreView) {
            bundle.putParcelableArrayList("mImgList", mImgList);
        } else {
            SaveDataHelper.getInstance().saveData("mImgList", mImgList);
        }
        bundle.putParcelableArrayList("mSelectImgList", mSelectImgList);
        bundle.putInt("mPosition", mPosition);
        bundle.putBoolean("isClickPreView", isClickPreView);
        mContext.startActivity(new Intent(mContext, PicturePreviewActivity.class).putExtras(bundle));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initAdapter();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_picture_preview;
    }

    private void initView() {
        photoVp = findViewById(R.id.photo_vp);
        backIv = findViewById(R.id.back_iv);
        titleTv = findViewById(R.id.title_tv);
        okTv = findViewById(R.id.ok_tv);
        checkNumTv = findViewById(R.id.checkNum_tv);
        checkCb = findViewById(R.id.check_cb);
        checkLl = findViewById(R.id.check_ll);
        backIv.setOnClickListener(this);
        checkLl.setOnClickListener(this);
        okTv.setOnClickListener(this);

        mPosition = getIntent().getIntExtra("mPosition", 0);
        isClickPreView = getIntent().getBooleanExtra("isClickPreView", isClickPreView);
        mSelectImgList = getIntent().getParcelableArrayListExtra("mSelectImgList");
        if (isClickPreView) {//底部预览按钮点击过来
            mImgList = getIntent().getParcelableArrayListExtra("mImgList");
        } else {//item点击过来
            mImgList = (ArrayList<LoadMediaBean>) SaveDataHelper.getInstance().getData("mImgList");
        }
        if (mSelectImgList == null) {
            mSelectImgList = new ArrayList<>();
        }
        if (mImgList == null) {
            mImgList = new ArrayList<>();
        }
    }

    private void initAdapter() {
        mAdapter = new PicturePreviewAdapter(this, mImgList);
        photoVp.setAdapter(mAdapter);
        photoVp.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mPosition = position;
                initSelectPic();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        photoVp.setCurrentItem(mPosition);
        initSelectPic();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.back_iv) {
            finish();
        } else if (v.getId() == R.id.check_ll) {
            if (mImgList.size() == 0) {
                return;
            }
            //判断图片是否存在（如原图路径不存在或者路径存在但文件不存在）
            if (!new File(mImgList.get(mPosition).getPath()).exists()) {
                Toast.makeText(this.getApplicationContext(), PictureHelper.tipsFileError(this, mImgList.get(mPosition).getMimeType()), Toast.LENGTH_SHORT).show();
                return;
            }
            //判断不能同时选择图片和视频
            String pictureType = mSelectImgList.size() > 0 ? mSelectImgList.get(0).getPictureType() : "";
            if (!optionsBean.isSelectImageVideo && optionsBean.selectMode == PictureConfig.MULTIPLE) {
                LoadMediaBean image = mImgList.get(mPosition);
                if (!TextUtils.isEmpty(pictureType)) {
                    boolean toEqual = PictureHelper.mimeToEqual(pictureType, image.getPictureType());
                    if (!toEqual) {
                        String str = getString(R.string.picture_toast_rule);
                        Toast.makeText(this.getApplicationContext(), str, Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
            }
            clickSelectPic(pictureType);
        } else if (v.getId() == R.id.ok_tv) {
            clickOnComplete();
        }
    }

    /**
     * 初始化选中图片操作
     */
    private void initSelectPic() {
        if (mImgList.size() > 0) {
            titleTv.setText((mPosition + 1) + "/" + mImgList.size());
            if (optionsBean.selectMode == PictureConfig.NONE) {
                okTv.setEnabled(true);
                okTv.setText(getResources().getString(R.string.picture_text_completed));
                checkLl.setVisibility(View.GONE);
            } else {
                checkLl.setVisibility(View.VISIBLE);
                if (isSelected(mImgList.get(mPosition))) {
                    checkCb.setChecked(true);
                    if (isCheckNumMode) {
                        LoadMediaBean mediaBean = mImgList.get(mPosition);
                        for (LoadMediaBean media : mSelectImgList) {
                            if (media.getPath().equals(mediaBean.getPath())) {
                                mediaBean.num = media.num;
                                checkNumTv.setVisibility(View.VISIBLE);
                                checkNumTv.setText(String.valueOf(mediaBean.num));
                                break;
                            }
                        }
                    } else {
                        checkNumTv.setVisibility(View.GONE);
                    }
                } else {
                    checkCb.setChecked(false);
                    checkNumTv.setVisibility(View.GONE);
                }
                selectNumChange(false);
            }
        } else {
            checkLl.setVisibility(View.GONE);
        }
    }

    /**
     * 更新数字样式选择的顺序
     */
    private void subSelectPosition() {
        if (isCheckNumMode) {
            for (int i = 0; i < mSelectImgList.size(); i++) {
                LoadMediaBean media = mSelectImgList.get(i);
                media.num = i + 1;
            }
            checkNumTv.setVisibility(View.GONE);
        }
    }

    /**
     * 是否已选中
     */
    public boolean isSelected(LoadMediaBean image) {
        boolean result = false;
        for (LoadMediaBean bean : mSelectImgList) {
            if (bean.getPath().equals(image.getPath())) {
                result = true;
                break;
            }
        }
        return result;
    }

    /**
     * 选中图片操作
     */
    private void clickSelectPic(String pictureType) {
        if (mImgList != null && mImgList.size() > 0) {
            LoadMediaBean image = mImgList.get(mPosition);
            boolean isChecked = checkCb.isChecked();
            if (isChecked) {
                //取消选中
                Iterator<LoadMediaBean> it = mSelectImgList.iterator();
                while (it.hasNext()) {
                    LoadMediaBean bean = it.next();
                    if (bean.getPath().equals(image.getPath())) {
                        it.remove();
                        subSelectPosition();
                        break;
                    }
                }
                checkCb.setChecked(false);
            } else {
                //判断图片最多可以选择多少
                if (mSelectImgList.size() >= optionsBean.maxSelectNum && optionsBean.selectMode == PictureConfig.MULTIPLE) {
                    boolean eqImg = pictureType.startsWith(PictureConfig.IMAGE);
                    @SuppressLint("StringFormatMatches") String str = eqImg ? getString(R.string.picture_toast_max_num_img, optionsBean.maxSelectNum) : getString(R.string.picture_toast_max_num_video, optionsBean.maxSelectNum);
                    Toast.makeText(this.getApplicationContext(), str, Toast.LENGTH_SHORT).show();
                    return;
                }
                //选中图片
                if (optionsBean.selectMode == PictureConfig.SINGLE) {
                    mSelectImgList.clear();
                }
                mSelectImgList.add(image);
                image.num = mSelectImgList.size();
                if (isCheckNumMode) {
                    checkNumTv.setVisibility(View.VISIBLE);
                    checkNumTv.setText(String.valueOf(image.num));
                }
                checkCb.setChecked(true);
            }
            selectNumChange(true);
        }
    }

    /**
     * 更新图片选择数量
     */
    public void selectNumChange(boolean isPostEvent) {
        boolean enable = mSelectImgList.size() != 0;
        okTv.setEnabled(enable);
        if (enable) {
            if (optionsBean.selectMode == PictureConfig.SINGLE) {
                okTv.setText(getResources().getString(R.string.picture_text_completed));
            } else {
                okTv.setText(getResources().getString(R.string.picture_text_completed) + "(" + mSelectImgList.size() + "/" + optionsBean.maxSelectNum + ")");
            }
        } else {
            okTv.setText(getString(R.string.picture_text_please_select));
        }
        if (isPostEvent) {
            EventBusBean obj = new EventBusBean(PictureConfig.UPDATE_FLAG, mSelectImgList);
            EventBus.getDefault().post(obj);
        }
    }

    /**
     * 点击已完成操作
     */
    private void clickOnComplete() {
        if (optionsBean.selectMode == PictureConfig.NONE) {
            LoadMediaBean mediaBean = mImgList.get(mPosition);
            mSelectImgList.add(mediaBean);
        }
        if (mSelectImgList.size() < optionsBean.minSelectNum && optionsBean.selectMode == PictureConfig.MULTIPLE) {
            String pictureType = mSelectImgList.size() > 0 ? mSelectImgList.get(0).getPictureType() : "";
            boolean eqImg = pictureType.startsWith(PictureConfig.IMAGE);
            @SuppressLint("StringFormatMatches") String str = eqImg ? getString(R.string.picture_toast_min_num_img, optionsBean.minSelectNum) : getString(R.string.picture_toast_min_num_video, optionsBean.minSelectNum);
            Toast.makeText(this.getApplicationContext(), str, Toast.LENGTH_SHORT).show();
        } else {
            EventBusBean obj = new EventBusBean(PictureConfig.PREVIEW_FLAG, mSelectImgList);
            EventBus.getDefault().post(obj);
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SaveDataHelper.getInstance().clearData("mImgList");
        mImgList = null;
        mSelectImgList = null;
    }
}
