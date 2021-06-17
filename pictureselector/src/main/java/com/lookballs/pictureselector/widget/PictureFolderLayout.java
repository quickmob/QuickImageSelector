package com.lookballs.pictureselector.widget;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.blankj.utilcode.util.ScreenUtils;
import com.lookballs.pictureselector.R;
import com.lookballs.pictureselector.app.adapter.PictureFolderAdapter;
import com.lookballs.pictureselector.app.bean.LoadMediaBean;
import com.lookballs.pictureselector.app.bean.LoadMediaFolderBean;
import com.lookballs.pictureselector.util.CommonUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 自定义相册文件夹
 */
public class PictureFolderLayout extends RelativeLayout {

    private Context mContext;

    private LinearLayout rootLayout;
    private MaxHeightRecyclerView folderRv;

    private PictureFolderAdapter mAdapter;
    private Animation animationIn, animationOut;

    public PictureFolderLayout(Context context) {
        super(context);
        this.mContext = context;
        initView();
    }

    public PictureFolderLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        initView();
    }

    private void initView() {
        setVisibility(GONE);
        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_picture_folder, this);
        rootLayout = view.findViewById(R.id.rootLayout_ll);
        folderRv = view.findViewById(R.id.folder_rv);
        rootLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!CommonUtil.isFastClick()) {
                    call.onClick();
                }
            }
        });
        animationIn = AnimationUtils.loadAnimation(mContext, R.anim.picture_window_in);
        animationOut = AnimationUtils.loadAnimation(mContext, R.anim.picture_window_out);
        animationIn.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                rootLayout.setBackground(new ColorDrawable(Color.argb(200, 0, 0, 0)));
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        initAdapter();
    }

    private void initAdapter() {
        mAdapter = new PictureFolderAdapter(mContext);
        folderRv.setMaxHeight((int) (ScreenUtils.getScreenHeight() * 0.7));
        folderRv.setLayoutManager(new LinearLayoutManager(mContext));
        folderRv.setAdapter(mAdapter);
    }

    public void bindFolder(ArrayList<LoadMediaFolderBean> folders) {
        mAdapter.bindFolderData(folders);
    }

    public void updateFolder(ArrayList<LoadMediaFolderBean> folders) {
        mAdapter.updateFolderData(folders);
    }

    /**
     * 设置选中状态
     */
    public void notifyDataCheckedStatus(ArrayList<LoadMediaBean> medias) {
        try {
            // 获取选中图片
            ArrayList<LoadMediaFolderBean> folders = mAdapter.getFolderData();
            for (LoadMediaFolderBean folder : folders) {
                folder.setCheckedNum(0);
            }
            if (medias.size() > 0) {
                for (LoadMediaFolderBean folder : folders) {
                    int num = 0;// 记录当前相册下有多少张是选中的
                    List<LoadMediaBean> images = folder.getImages();
                    for (LoadMediaBean media : images) {
                        String path = media.getRealPath();
                        for (LoadMediaBean m : medias) {
                            if (path.equals(m.getRealPath())) {
                                num++;
                                folder.setCheckedNum(num);
                            }
                        }
                    }
                }
            }
            mAdapter.bindFolderData(folders);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void folderOpen() {
        setVisibility(VISIBLE);
        startAnimation(animationIn);
    }

    public void folderClose() {
        rootLayout.setBackground(new ColorDrawable(Color.argb(0, 0, 0, 0)));
        startAnimation(animationOut);
        setVisibility(GONE);
    }

    public void setOnItemClickListener(PictureFolderAdapter.OnItemClickListener onItemClickListener) {
        mAdapter.setOnItemClickListener(onItemClickListener);
    }

    private RootLayoutClickListener call;

    public interface RootLayoutClickListener {
        void onClick();
    }

    public void getRootLayoutClickListener(RootLayoutClickListener call) {
        this.call = call;
    }

}
