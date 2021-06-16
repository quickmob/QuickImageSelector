package com.lookballs.app.pictureselector;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.lookballs.pictureselector.PictureSelector;
import com.lookballs.pictureselector.app.bean.LoadMediaBean;
import com.lookballs.pictureselector.config.PictureMineType;
import com.lookballs.pictureselector.config.PictureOptions;

import java.util.ArrayList;

public class TestFragment extends Fragment {

    private EditText et_appiont_folder, et_span_count, et_select_min_size, et_select_max_size;
    private RadioGroup rgb_style, rgb_choose_mode, rgb_select_mode;
    private CheckBox cb_isCamera, cb_isGif, cb_preview, cb_isSelectImageVideo;
    private TextView tv_selectResult;
    private Button btn_openSelector;

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

    private View rootView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.activity_test, container, false);
        }
        initView();
        return rootView;
    }

    private void initView() {
        et_appiont_folder = rootView.findViewById(R.id.et_appiont_folder);
        et_span_count = rootView.findViewById(R.id.et_span_count);
        et_select_min_size = rootView.findViewById(R.id.et_select_min_size);
        et_select_max_size = rootView.findViewById(R.id.et_select_max_size);
        rgb_style = rootView.findViewById(R.id.rgb_style);
        rgb_choose_mode = rootView.findViewById(R.id.rgb_choose_mode);
        rgb_select_mode = rootView.findViewById(R.id.rgb_select_mode);
        cb_isCamera = rootView.findViewById(R.id.cb_isCamera);
        cb_isSelectImageVideo = rootView.findViewById(R.id.cb_isSelectImageVideo);
        cb_isGif = rootView.findViewById(R.id.cb_isGif);
        cb_preview = rootView.findViewById(R.id.cb_preview);
        tv_selectResult = rootView.findViewById(R.id.tv_selectResult);
        btn_openSelector = rootView.findViewById(R.id.btn_openSelector);
        btn_openSelector.setText("在Fragment中打开图片选择器");

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

        btn_openSelector.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSelector();
            }
        });
    }

    private void openSelector() {
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

        String outputCameraPath = isCamera ? CommonHelper.createCameraOutPath(getActivity(), choose) : "";

        PictureSelector.create(this)
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
                .videoMinSecond(1)//设置视频筛选最小时间，只有当TYPE_ALL、TYPE_VIDEO才有效
                .specifiedFormat(PictureMineType.IMAGE_JPEG, PictureMineType.IMAGE_PNG)//设置指定格式的文件，只有当TYPE_IMAGE、TYPE_VIDEO才有效
                .forResult();//开启图片选择页面
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        PictureSelector.onActivityResult(requestCode, resultCode, data, new PictureOptions.OnPictureSelectResult() {
            @Override
            public void onResult(ArrayList<LoadMediaBean> selectList) {
                StringBuffer sb = new StringBuffer();
                for (int i = 0; i < selectList.size(); i++) {
                    sb.append(selectList.get(i).getPath()).append("\n");
                }
                tv_selectResult.setText("回调结果：\n" + sb.toString());
            }
        });
    }

}
