package com.lookballs.pictureselector.app.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import com.blankj.utilcode.util.SizeUtils;
import com.lookballs.pictureselector.PictureSelector;
import com.lookballs.pictureselector.R;
import com.lookballs.pictureselector.app.adapter.PictureFolderAdapter;
import com.lookballs.pictureselector.app.adapter.PictureGridImageAdapter;
import com.lookballs.pictureselector.app.bean.EventBusBean;
import com.lookballs.pictureselector.app.bean.LoadMediaBean;
import com.lookballs.pictureselector.app.bean.LoadMediaFolderBean;
import com.lookballs.pictureselector.config.PictureConfig;
import com.lookballs.pictureselector.config.PictureOptionsBean;
import com.lookballs.pictureselector.helper.PermissionsHelper;
import com.lookballs.pictureselector.helper.PictureHelper;
import com.lookballs.pictureselector.model.MediaLoaderV2;
import com.lookballs.pictureselector.util.CommonUtil;
import com.lookballs.pictureselector.widget.GridSpacingItemDecoration;
import com.lookballs.pictureselector.widget.PictureFolderLayout;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class PictureSelectorActivity extends PictureBaseActivity implements View.OnClickListener {

    //布局
    private RecyclerView photoRv;
    private ImageView backIv;
    private TextView titleTv, previewTv, okTv, folderTv, tipsTv;
    private LinearLayout emptyLayoutLl;
    private PictureFolderLayout folderLayout;

    //其他
    private PictureGridImageAdapter mAdapter;//适配器
    private ArrayList<LoadMediaBean> mImgList;//图片列表
    private boolean isFolderOpen = false;//图片文件夹是否打开
    //权限申请
    private String[] READ_WRITE_PERMISSIONS = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
    //权限申请类
    private PermissionsHelper permissionsHelper;
    //图片加载管理
    private MediaLoaderV2 mediaLoaderV2;

    public static void openActivity(Activity activity, Fragment fragment) {
        if (activity != null) {
            Intent intent = new Intent(activity, PictureSelectorActivity.class);
            if (fragment != null) {
                fragment.startActivityForResult(intent, PictureConfig.CHOOSE_REQUEST);
            } else {
                activity.startActivityForResult(intent, PictureConfig.CHOOSE_REQUEST);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        initView();
        initAdapter(savedInstanceState);
        initData();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_picture_selector;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mAdapter != null) {
            PictureHelper.saveSelectorList(outState, mAdapter.checkImages);
        }
    }

    private void initView() {
        photoRv = findViewById(R.id.photo_rv);
        backIv = findViewById(R.id.back_iv);
        titleTv = findViewById(R.id.title_tv);
        previewTv = findViewById(R.id.preview_tv);
        okTv = findViewById(R.id.ok_tv);
        folderTv = findViewById(R.id.folder_tv);
        folderLayout = findViewById(R.id.folder_pfl);
        emptyLayoutLl = findViewById(R.id.emptyLayout_ll);
        tipsTv = findViewById(R.id.tips_tv);

        okTv.setOnClickListener(this);
        backIv.setOnClickListener(this);
        previewTv.setOnClickListener(this);
        folderTv.setOnClickListener(this);
        titleTv.setText(PictureHelper.getTitleText(this, optionsBean.mimeType));
        folderTv.setText(PictureHelper.getTitleText(this, optionsBean.mimeType));

        if (optionsBean.selectMode == PictureConfig.NONE) {
            okTv.setVisibility(View.GONE);
            previewTv.setVisibility(View.GONE);
        } else {
            okTv.setVisibility(View.VISIBLE);
            if (optionsBean.isPreview) {
                previewTv.setVisibility(View.VISIBLE);
            } else {
                previewTv.setVisibility(View.GONE);
            }
        }
    }

    private void initAdapter(Bundle savedInstanceState) {
        photoRv.setHasFixedSize(true);
        photoRv.addItemDecoration(new GridSpacingItemDecoration(optionsBean.spanCount, SizeUtils.dp2px(2), false));
        photoRv.setLayoutManager(new GridLayoutManager(this, optionsBean.spanCount));
        //解决调用 notifyItemChanged 闪烁问题,取消默认动画
        ((SimpleItemAnimator) photoRv.getItemAnimator()).setSupportsChangeAnimations(false);
        mAdapter = new PictureGridImageAdapter(PictureSelectorActivity.this, isCheckNumMode);
        //防止拍照内存不足时activity被回收，导致拍照后的图片未选中
        if (savedInstanceState != null) {
            ArrayList<LoadMediaBean> selectorList = PictureHelper.obtainSelectorList(savedInstanceState);
            mAdapter.checkImages = selectorList;
        }
        photoRv.setAdapter(mAdapter);
    }

    private void initData() {
        mImgList = new ArrayList<>();
        mAdapter.getAdapterCallBack(new PictureGridImageAdapter.AdapterCallBack() {
            @Override
            public void onSelect(ArrayList<LoadMediaBean> beans) {
                if (optionsBean.selectMode == PictureConfig.NONE) {
                    onSelectResult(beans);
                } else {
                    if (beans.size() > 0) {
                        previewTv.setEnabled(true);
                        previewTv.setText(getResources().getString(R.string.picture_text_preview) + "(" + beans.size() + ")");
                        okTv.setEnabled(true);
                        if (optionsBean.selectMode == PictureConfig.SINGLE) {
                            okTv.setText(getResources().getString(R.string.picture_text_completed));
                        } else {
                            okTv.setText(getResources().getString(R.string.picture_text_completed) + "(" + beans.size() + "/" + optionsBean.maxSelectNum + ")");
                        }
                    } else {
                        previewTv.setEnabled(false);
                        previewTv.setText(getResources().getString(R.string.picture_text_preview));
                        okTv.setEnabled(false);
                        okTv.setText(getResources().getString(R.string.picture_text_please_select));
                    }
                }
            }

            @Override
            public void onCamera() {
                PictureCameraActivity.openActivity(PictureSelectorActivity.this, null, false);
            }
        });
        folderLayout.getRootLayoutClickListener(new PictureFolderLayout.RootLayoutClickListener() {
            @Override
            public void onClick() {
                isFolderOpen = true;
                folderOpenClose();
            }
        });
        folderLayout.setOnItemClickListener(new PictureFolderAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(long bucketId, String folderName, ArrayList<LoadMediaBean> images, boolean isSelected) {
                isFolderOpen = true;
                folderOpenClose();
                if (!isSelected) {
                    folderTv.setText(folderName);
                    mediaLoaderV2.loadByBucketId(bucketId, new MediaLoaderV2.LoadMediaCallback() {
                        @Override
                        public void loadComplete(ArrayList<LoadMediaFolderBean> folders) {
                            if (folders.size() > 0) {
                                mImgList = folders.get(0).getImages();
                                isFolderCanClick(true);
                                isHasImageList(mImgList.size() > 0, folders, true);
                            } else {
                                isFolderCanClick(false);
                                isHasImageList(false, null, false);
                            }
                        }

                        @Override
                        public void loadError(Throwable t) {
                            t.printStackTrace();
                            loadMediaError();
                        }
                    });
                }
            }
        });

        mediaLoaderV2 = new MediaLoaderV2(this);

        permissionsHelper = PermissionsHelper.create(this);
        permissionStorage();
    }

    /**
     * 请求存储权限
     */
    private void permissionStorage() {
        permissionsHelper
                .permission(READ_WRITE_PERMISSIONS)
                .request(new PermissionsHelper.PermissionsCallback() {
                    @Override
                    public void onGranted(List<String> permissions, boolean isAll) {
                        if (isAll) {
                            canGetFileInfo();
                        }
                    }

                    @Override
                    public void onDenied(List<String> permissions, boolean isNever) {
                        if (isNever) {
                            Toast.makeText(PictureSelectorActivity.this.getApplicationContext(), getString(R.string.picture_toast_permission_write_never), Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(PictureSelectorActivity.this.getApplicationContext(), getString(R.string.picture_toast_permission_write), Toast.LENGTH_SHORT).show();
                        }
                        cannotGetFileInfo();
                        if (PictureOptionsBean.onPermissionDenied != null) {
                            PictureOptionsBean.onPermissionDenied.onDenied(permissions, isNever);
                        }
                    }
                });
    }

    /**
     * 获取本地所有图片和视频的权限申请成功后的操作
     */
    private void canGetFileInfo() {
        isFolderCanClick(true);
        mediaLoaderV2.loadAll(new MediaLoaderV2.LoadMediaCallback() {
            @Override
            public void loadComplete(ArrayList<LoadMediaFolderBean> folders) {
                if (folders.size() > 0) {
                    mImgList = folders.get(0).getImages();
                    if (TextUtils.isEmpty(optionsBean.appointFolderName)) {
                        if (mImgList.size() > 0) {
                            isFolderCanClick(true);
                            if (folders.size() > 0) {
                                folders.get(0).setChecked(true);
                            }
                            isHasImageList(true, folders, false);
                        } else {
                            isFolderCanClick(false);
                            isHasImageList(false, null, false);
                        }
                    } else {
                        showAppiontFolderFile(folders);
                    }
                } else {
                    isFolderCanClick(false);
                    isHasImageList(false, null, false);
                }
            }

            @Override
            public void loadError(Throwable t) {
                t.printStackTrace();
                loadMediaError();
            }
        });
    }

    /**
     * 获取本地所有图片和视频的权限申请失败后的操作
     */
    private void cannotGetFileInfo() {
        isFolderCanClick(false);
    }

    /**
     * 有无数据时的布局显示和操作
     */
    private void isHasImageList(boolean isHasData, ArrayList<LoadMediaFolderBean> folders, boolean isUpdateFolder) {
        if (isHasData) {
            emptyLayoutLl.setVisibility(View.GONE);
            mAdapter.bindImagesData(mImgList);
            if (isUpdateFolder) {
                folderLayout.updateFolder(folders);
            } else {
                folderLayout.bindFolder(folders);
            }
        } else {
            emptyLayoutLl.setVisibility(View.VISIBLE);
            tipsTv.setText(getString(R.string.picture_text_empty));
        }
    }

    /**
     * 图库加载失败时的布局显示
     */
    private void loadMediaError() {
        photoRv.setVisibility(View.GONE);
        emptyLayoutLl.setVisibility(View.VISIBLE);
        tipsTv.setText(getString(R.string.picture_toast_load_error));
    }

    /**
     * 底部文件夹按钮是否可以点击
     */
    private void isFolderCanClick(boolean isFolderCanClick) {
        if (isFolderCanClick) {
            folderTv.setEnabled(true);
            folderTv.setSelected(false);
        } else {
            folderTv.setEnabled(false);
            CommonUtil.setCompoundDrawables(this, folderTv, 0, null);
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.back_iv) {
            if (isFolderOpen) {
                folderOpenClose();
            } else {
                finish();
            }
        } else if (v.getId() == R.id.ok_tv) {
            clickOnComplete();
        } else if (v.getId() == R.id.preview_tv) {
            PicturePreviewActivity.openActivity(this, mAdapter.checkImages, mAdapter.checkImages, 0);
        } else if (v.getId() == R.id.folder_tv) {
            folderOpenClose();
        }
    }

    /**
     * 图片文件夹打开和关闭操作
     */
    private void folderOpenClose() {
        if (mImgList.size() > 0) {
            if (isFolderOpen) {
                isFolderOpen = false;
                folderLayout.folderClose();
                folderTv.setSelected(false);
            } else {
                isFolderOpen = true;
                folderLayout.folderOpen();
                folderTv.setSelected(true);
                ArrayList<LoadMediaBean> selectedImages = mAdapter.checkImages;
                folderLayout.notifyDataCheckedStatus(selectedImages);
            }
        }
    }

    /**
     * 点击已完成操作
     */
    private void clickOnComplete() {
        ArrayList<LoadMediaBean> images = mAdapter.checkImages;
        if (images.size() < optionsBean.minSelectNum && optionsBean.selectMode == PictureConfig.MULTIPLE) {
            String pictureType = images.size() > 0 ? images.get(0).getPictureType() : "";
            boolean eqImg = pictureType.startsWith(PictureConfig.IMAGE);
            @SuppressLint("StringFormatMatches") String str = eqImg ? getString(R.string.picture_toast_min_num_img, optionsBean.minSelectNum) : getString(R.string.picture_toast_min_num_video, optionsBean.minSelectNum);
            Toast.makeText(this.getApplicationContext(), str, Toast.LENGTH_SHORT).show();
        } else {
            onSelectResult(images);
        }
    }

    /**
     * 显示指定文件夹里面的内容
     */
    private void showAppiontFolderFile(ArrayList<LoadMediaFolderBean> folders) {
        folderTv.setText(!TextUtils.isEmpty(optionsBean.appointShowName) ? optionsBean.appointShowName : optionsBean.appointFolderName);
        isFolderCanClick(false);

        int i = 0;
        boolean isContains = false;
        Iterator iterator = folders.iterator();
        while (iterator.hasNext()) {
            LoadMediaFolderBean it = (LoadMediaFolderBean) iterator.next();
            if (it.getName().contains(optionsBean.appointFolderName)) {
                isContains = true;
                break;
            }
            i++;
        }
        if (isContains) {//如果设置了并且有这个文件夹内容
            LoadMediaFolderBean folder = folders.get(i);
            folder.setChecked(true);
            ArrayList<LoadMediaBean> mediaBeans = folder.getImages();
            if (mediaBeans.size() > 0) {
                mImgList = mediaBeans;
                isHasImageList(true, folders, false);
            } else {
                isHasImageList(false, null, false);
            }
        } else {//如果设置了但是没有这个文件夹内容
            isHasImageList(false, null, false);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        mImgList = null;
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onStickyEventBus(EventBusBean event) {
        if (event != null) {
            EventBus.getDefault().removeStickyEvent(event);
            switch (event.what) {
                case PictureConfig.UPDATE_FLAG:
                    //预览时勾选图片更新回调
                    ArrayList<LoadMediaBean> selectImages = event.mediaBeans;
                    mAdapter.bindSelectImagesData(selectImages);
                    break;
                case PictureConfig.PREVIEW_FLAG:
                    //预览时点击已完成按钮回调
                    ArrayList<LoadMediaBean> selectImages2 = event.mediaBeans;
                    onSelectResult(selectImages2);
                    break;
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (permissionsHelper != null) {
            permissionsHelper.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PictureConfig.CAMERA_REQUEST) {//拍照返回
            switch (resultCode) {
                case Activity.RESULT_OK:
                    //拍摄结果回调
                    ArrayList<LoadMediaBean> list = PictureSelector.obtainMultipleResult(data, true);
                    onSelectResult(list);
                    break;
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (isFolderOpen) {
                folderOpenClose();
                return false;
            } else {
                finish();
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}
