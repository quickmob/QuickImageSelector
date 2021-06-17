package com.lookballs.app.pictureselector;

import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;

import com.blankj.utilcode.util.ToastUtils;
import com.blankj.utilcode.util.UriUtils;
import com.bumptech.glide.Glide;
import com.lookballs.app.pictureselector.http.UploadBean;
import com.lookballs.http.QuickHttp;
import com.lookballs.http.core.model.HttpParams;
import com.lookballs.http.core.model.UploadInfo;
import com.lookballs.http.listener.OnUploadListener;
import com.lookballs.pictureselector.PictureSelector;
import com.lookballs.pictureselector.app.bean.LoadMediaBean;
import com.lookballs.pictureselector.config.PictureMineType;
import com.lookballs.pictureselector.config.PictureOptions;
import com.lookballs.pictureselector.util.SdkVersionUtils;

import java.io.File;
import java.util.ArrayList;

import okhttp3.Call;

public class TestActivity extends FragmentActivity {

    private EditText et_appiont_folder, et_span_count, et_select_min_size, et_select_max_size;
    private RadioGroup rgb_style, rgb_choose_mode, rgb_select_mode;
    private CheckBox cb_isCamera, cb_isGif, cb_preview, cb_isSelectImageVideo;
    private TextView tv_selectResult;

    private String appiontFolder = "";
    private int spanCount = 4;
    private int selectMinSize = 1;
    private int selectMaxSize = 9;
    private boolean isCamera = true;
    private boolean isGif = true;
    private boolean isSelectImageVideo = true;
    private boolean isPreview = true;
    private PictureSelector.ThemeStyle themeStyle = PictureSelector.ThemeStyle.DEFAULT;
    private PictureSelector.Choose choose = PictureSelector.Choose.TYPE_ALL;
    private PictureSelector.SelectMode selectMode = PictureSelector.SelectMode.MULTIPLE;

    private LinearLayout ll_image;
    private ImageView iv_image;
    private String filePath;
    private ArrayList<LoadMediaBean> selectList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        initView();
    }

    public void btn_openSelector(View view) {//图片选择
        openSelector(false);
    }

    public void btn_take(View view) {//拍照或录像
        openSelector(true);
    }

    public void btn_preview(View view) {//图片预览
        if (selectList != null && selectList.size() > 0) {
            PictureSelector.openPicturePreview(this, selectList, 0);
        }
    }

    public void btn_upload(View view) {//图片上传
        HttpParams httpParams = new HttpParams();
        httpParams.put("image", new File(filePath));
        QuickHttp.post("https://graph.baidu.com/upload/")
                .params(httpParams)
                .async(UploadBean.class, new OnUploadListener<UploadBean>() {
                    @Override
                    public void onStart(Call call) {
                        view.setEnabled(false);
                        ToastUtils.showShort("上传中...");
                    }

                    @Override
                    public void onProgress(UploadInfo info) {

                    }

                    @Override
                    public void onSucceed(UploadBean result) {
                        if (result.getStatus() == 0) {
                            ToastUtils.showShort("上传成功");
                        } else {
                            ToastUtils.showShort("上传失败：" + result.getMsg());
                        }
                    }

                    @Override
                    public void onError(int code, Exception e) {
                        ToastUtils.showShort("上传失败：" + e.getMessage());
                    }

                    @Override
                    public void onEnd(Call call) {
                        view.setEnabled(true);
                    }
                });
    }

    private void initView() {
        et_appiont_folder = findViewById(R.id.et_appiont_folder);
        et_span_count = findViewById(R.id.et_span_count);
        et_select_min_size = findViewById(R.id.et_select_min_size);
        et_select_max_size = findViewById(R.id.et_select_max_size);
        rgb_style = findViewById(R.id.rgb_style);
        rgb_choose_mode = findViewById(R.id.rgb_choose_mode);
        rgb_select_mode = findViewById(R.id.rgb_select_mode);
        cb_isCamera = findViewById(R.id.cb_isCamera);
        cb_isSelectImageVideo = findViewById(R.id.cb_isSelectImageVideo);
        cb_isGif = findViewById(R.id.cb_isGif);
        cb_preview = findViewById(R.id.cb_preview);
        tv_selectResult = findViewById(R.id.tv_selectResult);
        ll_image = findViewById(R.id.ll_image);
        iv_image = findViewById(R.id.iv_image);

        rgb_style.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_default_style:
                        themeStyle = PictureSelector.ThemeStyle.DEFAULT;
                        break;
                    case R.id.rb_white_style:
                        themeStyle = PictureSelector.ThemeStyle.WHITE;
                        break;
                    case R.id.rb_num_style:
                        themeStyle = PictureSelector.ThemeStyle.NUM;
                        break;
                }
            }
        });
        rgb_select_mode.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_multiple:
                        selectMode = PictureSelector.SelectMode.MULTIPLE;
                        break;
                    case R.id.rb_single:
                        selectMode = PictureSelector.SelectMode.SINGLE;
                        break;
                    case R.id.rb_none:
                        selectMode = PictureSelector.SelectMode.NONE;
                        break;
                }
            }
        });
        rgb_choose_mode.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_all:
                        choose = PictureSelector.Choose.TYPE_ALL;
                        break;
                    case R.id.rb_image:
                        choose = PictureSelector.Choose.TYPE_IMAGE;
                        break;
                    case R.id.rb_video:
                        choose = PictureSelector.Choose.TYPE_VIDEO;
                        break;
                }
            }
        });
    }

    private void openSelector(boolean isOpenCamera) {
        appiontFolder = et_appiont_folder.getText().toString().trim();
        if (!TextUtils.isEmpty(et_span_count.getText().toString().trim())) {
            spanCount = Integer.parseInt(et_span_count.getText().toString().trim());
        }
        if (!TextUtils.isEmpty(et_select_min_size.getText().toString().trim())) {
            selectMinSize = Integer.parseInt(et_select_min_size.getText().toString().trim());
        }
        if (!TextUtils.isEmpty(et_select_max_size.getText().toString().trim())) {
            selectMaxSize = Integer.parseInt(et_select_max_size.getText().toString().trim());
        }
        isCamera = cb_isCamera.isChecked();
        isGif = cb_isGif.isChecked();
        isSelectImageVideo = cb_isSelectImageVideo.isChecked();
        isPreview = cb_preview.isChecked();

        String outputCameraPath = isCamera ? CommonHelper.createCameraOutPath(this, choose) : "";

        PictureOptions options = PictureSelector.create(this)
                .choose(choose)//全部TYPE_ALL、图片TYPE_IMAGE、视频TYPE_VIDEO
                .appointFolderName(appiontFolder, appiontFolder)//设置显示指定的文件夹里面的图片（比如需要显示微信对应的文件夹里面的文件 appointFolderName--WeiXin）
                .minSelectNum(selectMinSize)//最小选择数
                .maxSelectNum(selectMaxSize)//设置多选模式下最大选择数,多选模式下有效
                .spanCount(spanCount)//设置每行显示的图片列数
                .isShowCamera(outputCameraPath)//设置是否需要拍摄 1、TYPE_ALL默认显示拍照模式 2、TYPE_IMAGE显示拍照模式 3、TYPE_VIDEO显示录像模式  outputCameraPath拍摄内容保存路径，如果值传空，也视为不开启拍摄
                .isGif(isGif)//设置是否显示gif图片，只有当TYPE_ALL、TYPE_IMAGE才有效
                .themeStyle(themeStyle)//设置主题样式（请参照默认主题样式进行配置）
                .selectMode(selectMode)//设置选择模式MULTIPLE（多选）、SINGLE（单选）、NONE（不选）
                .isSelectImageVideo(isSelectImageVideo)//设置是否可以同时选择图片和视频
                .isPreview(isPreview)//设置是否预览
                .recordVideoSecond(60)//设置拍摄时长，只有当TYPE_VIDEO并且开启了拍摄按钮后才有效
                .videoQuality(1)//设置拍摄质量0 or 1，只有当TYPE_VIDEO并且开启了拍摄按钮后才有效
                .videoMaxSecond(30)//设置视频筛选最大时间，只有当TYPE_ALL、TYPE_VIDEO才有效
                .videoMinSecond(1);//设置视频筛选最小时间，只有当TYPE_ALL、TYPE_VIDEO才有效
                //.specifiedFormat(PictureMineType.IMAGE_JPEG, PictureMineType.IMAGE_PNG);//设置指定格式的文件，只有当TYPE_IMAGE、TYPE_VIDEO才有效
        if (isOpenCamera) {//直接打开相机拍摄
            options.forCameraResult(new PictureOptions.OnCameraResult() {
                @Override
                public void onResult(ArrayList<LoadMediaBean> list) {
                    StringBuffer sb = new StringBuffer();
                    for (int i = 0; i < list.size(); i++) {
                        sb.append(list.get(i).getAbsolutePath()).append("\n");
                    }
                    tv_selectResult.setText("拍摄回调结果：\n" + sb.toString());
                }
            });
        } else {//打开图片选择库
            options.forSelectResult(new PictureOptions.OnPictureSelectResult() {
                @Override
                public void onResult(ArrayList<LoadMediaBean> list) {
                    StringBuffer sb = new StringBuffer();
                    for (int i = 0; i < list.size(); i++) {
                        sb.append(list.get(i).getAbsolutePath()).append("\n");
                    }
                    tv_selectResult.setText("选择回调结果：\n" + sb.toString());

                    TestActivity.this.selectList = list;

                    if (selectList.size() == 1) {
                        filePath = selectList.get(0).getRealPath();

                        ll_image.setVisibility(View.VISIBLE);
                        Glide.with(TestActivity.this)
                                .asDrawable()
                                .load(filePath)
                                .into(iv_image);

                        //判断Android Q设备
                        if (SdkVersionUtils.isAndroid_Q()) {
                            Uri uri = Uri.parse(filePath);
                            File file = UriUtils.uri2File(uri);
                            if (file != null) {
                                filePath = file.getAbsolutePath();
                            }
                        }
                    } else {
                        ll_image.setVisibility(View.GONE);
                    }
                }
            });//开启图片选择页面
        }
    }
}
