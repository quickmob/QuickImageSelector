package com.lookballs.pictureselector.app.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.PorterDuff;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.lookballs.pictureselector.R;
import com.lookballs.pictureselector.app.activity.PicturePreviewActivity;
import com.lookballs.pictureselector.app.bean.LoadMediaBean;
import com.lookballs.pictureselector.config.PictureConfig;
import com.lookballs.pictureselector.config.PictureOptionsBean;
import com.lookballs.pictureselector.helper.PictureHelper;
import com.lookballs.pictureselector.util.CommonUtil;
import com.lookballs.pictureselector.util.DateUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;

public class PictureGridImageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private PictureOptionsBean optionsBean;
    private boolean checkNumMode;
    private Animation animation;
    public ArrayList<LoadMediaBean> mImagesList = new ArrayList<>();
    public ArrayList<LoadMediaBean> checkImages = new ArrayList<>();

    public PictureGridImageAdapter(Context mContext, boolean checkNumMode) {
        this.mContext = mContext;
        this.optionsBean = PictureOptionsBean.getInstance();
        this.checkNumMode = checkNumMode;
        this.animation = AnimationUtils.loadAnimation(mContext, R.anim.picture_check_anim);
    }

    /**
     * 设置图片数据
     */
    public void bindImagesData(ArrayList<LoadMediaBean> images) {
        if (images != null) {
            this.mImagesList = images;
        }
        notifyDataSetChanged();
    }

    /**
     * 设置已选图片数据
     */
    public void bindSelectImagesData(ArrayList<LoadMediaBean> selectImages) {
        if (selectImages != null) {
            this.checkImages = selectImages;
        }
        callBack.onSelect(selectImages);
        notifyDataSetChanged();
    }

    /************************************设置适配器回调开始******************************/
    private AdapterCallBack callBack;

    public interface AdapterCallBack {
        void onSelect(ArrayList<LoadMediaBean> beans);

        void onCamera();
    }

    public void getAdapterCallBack(AdapterCallBack callBack) {
        this.callBack = callBack;
    }

    /************************************设置适配器回调结束******************************/

    @Override
    public int getItemViewType(int position) {
        if (!TextUtils.isEmpty(optionsBean.outputCameraPath) && position == 0) {
            return PictureConfig.TYPE_CAMERA;
        } else {
            return PictureConfig.TYPE_PICTURE;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == PictureConfig.TYPE_CAMERA) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_picture_camera, parent, false);
            return new HeaderViewHolder(view);
        } else {
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_picture_grid_image, parent, false);
            return new ViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        int pos = holder.getAdapterPosition();

        if (getItemViewType(pos) == PictureConfig.TYPE_CAMERA) {
            HeaderViewHolder headerHolder = (HeaderViewHolder) holder;
            headerHolder.camera_tv.setText(PictureHelper.getTakePictureText(mContext, optionsBean.mimeType));

            headerHolder.headerView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    callBack.onCamera();
                }
            });
        } else {
            ViewHolder contentHolder = (ViewHolder) holder;
            LoadMediaBean image = mImagesList.get(!TextUtils.isEmpty(optionsBean.outputCameraPath) ? pos - 1 : pos);
            image.position = pos;
            String realPath = image.getRealPath();
            String pictureType = image.getPictureType();
            boolean isVideo = PictureHelper.isVideo(pictureType);
            boolean gif = PictureHelper.isGif(pictureType);
            boolean eqLongImg = PictureHelper.isLongImg(image);
            long duration = image.getDuration();

            contentHolder.ll_check.setVisibility(optionsBean.selectMode == PictureConfig.NONE ? View.GONE : View.VISIBLE);
            contentHolder.gif_tv.setVisibility(gif ? View.VISIBLE : View.GONE);
            contentHolder.longChart_tv.setVisibility(eqLongImg ? View.VISIBLE : View.GONE);
            contentHolder.duration_tv.setText(DateUtil.timeParse(duration));
            if (isSelected(image)) {
                contentHolder.check_cb.setChecked(true);
                if (checkNumMode) {
                    contentHolder.checkNum_tv.setVisibility(View.VISIBLE);
                    for (LoadMediaBean bean : checkImages) {
                        if (bean.getRealPath().equals(image.getRealPath())) {
                            contentHolder.checkNum_tv.setText(String.valueOf(bean.num));
                            break;
                        }
                    }
                } else {
                    contentHolder.checkNum_tv.setVisibility(View.GONE);
                }
            } else {
                contentHolder.check_cb.setChecked(false);
                contentHolder.checkNum_tv.setVisibility(View.GONE);
            }
            setColorFilter(contentHolder, isSelected(image));
            contentHolder.duration_tv.setVisibility(isVideo ? View.VISIBLE : View.GONE);
            CommonUtil.setCompoundDrawables(mContext, contentHolder.duration_tv, R.drawable.picture_ic_video, CommonUtil.DrawableDir.LEFT);

            contentHolder.ll_check.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    checkChangeState(contentHolder, pos, image);
                }
            });
            contentHolder.contentView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (optionsBean.isPreview) {
                        clickItem(pos, image);
                    } else {
                        checkChangeState(contentHolder, pos, image);
                    }
                }
            });
            RequestOptions options = new RequestOptions()
                    .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                    .placeholder(R.drawable.picture_ll_pic_placeholder)
                    .centerCrop();
            Glide.with(mContext)
                    .asDrawable()
                    .load(realPath)
                    .apply(options)
                    .into(contentHolder.picture_iv);
        }
    }

    @Override
    public int getItemCount() {
        return !TextUtils.isEmpty(optionsBean.outputCameraPath) ? mImagesList.size() + 1 : mImagesList.size();
    }

    public class HeaderViewHolder extends RecyclerView.ViewHolder {
        View headerView;
        TextView camera_tv;

        public HeaderViewHolder(View itemView) {
            super(itemView);
            headerView = itemView;
            camera_tv = itemView.findViewById(R.id.camera_tv);
            camera_tv.setText(mContext.getResources().getString(R.string.picture_text_take_picture));
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        View contentView;
        ImageView picture_iv;
        CheckBox check_cb;
        TextView duration_tv, gif_tv, longChart_tv, checkNum_tv;
        LinearLayout ll_check;

        public ViewHolder(View itemView) {
            super(itemView);
            contentView = itemView;
            picture_iv = itemView.findViewById(R.id.picture_iv);
            check_cb = itemView.findViewById(R.id.check_cb);
            checkNum_tv = itemView.findViewById(R.id.checkNum_tv);
            ll_check = itemView.findViewById(R.id.check_ll);
            duration_tv = itemView.findViewById(R.id.duration_tv);
            gif_tv = itemView.findViewById(R.id.gif_tv);
            longChart_tv = itemView.findViewById(R.id.longChart_tv);
        }
    }

    /**
     * 按钮选中和取消操作
     */
    public void checkChangeState(ViewHolder contentHolder, int position, LoadMediaBean image) {
        //判断图片是否存在（如原图路径不存在或者路径存在但文件不存在）
        if (!new File(image.getAbsolutePath()).exists()) {
            Toast.makeText(mContext.getApplicationContext(), PictureHelper.tipsFileError(mContext, image.getMimeType()), Toast.LENGTH_SHORT).show();
            return;
        }
        //判断不能同时选择图片和视频
        String pictureType = checkImages.size() > 0 ? checkImages.get(0).getPictureType() : "";
        if (!optionsBean.isSelectImageVideo && optionsBean.selectMode == PictureConfig.MULTIPLE) {
            if (!TextUtils.isEmpty(pictureType)) {
                boolean toEqual = PictureHelper.mimeToEqual(pictureType, image.getPictureType());
                if (!toEqual) {
                    String str = mContext.getString(R.string.picture_toast_rule);
                    Toast.makeText(mContext.getApplicationContext(), str, Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        }

        boolean isChecked = contentHolder.check_cb.isChecked();
        if (isChecked) {
            //取消选中
            Iterator<LoadMediaBean> it = checkImages.iterator();
            while (it.hasNext()) {
                LoadMediaBean bean = it.next();
                if (bean.getRealPath().equals(image.getRealPath())) {
                    it.remove();
                    subSelectPosition();
                    callBack.onSelect(checkImages);
                    //第一个参数未局部刷新的item值，第二个参数相当于一个标记，任何类型都可以，只要不为空就可以解决局部刷新item导致图片闪烁问题
                    notifyItemChanged(position, "payload");
                    break;
                }
            }
        } else {
            //判断图片最多可以选择多少
            if (checkImages.size() >= optionsBean.maxSelectNum && optionsBean.selectMode == PictureConfig.MULTIPLE) {
                boolean eqImg = pictureType.startsWith(PictureConfig.IMAGE);
                @SuppressLint("StringFormatMatches") String str = eqImg ? mContext.getString(R.string.picture_toast_max_num_img, optionsBean.maxSelectNum) : mContext.getString(R.string.picture_toast_max_num_video, optionsBean.maxSelectNum);
                Toast.makeText(mContext.getApplicationContext(), str, Toast.LENGTH_SHORT).show();
                return;
            }
            //选中图片
            if (optionsBean.selectMode == PictureConfig.SINGLE) {
                if (checkImages.size() > 0) {
                    LoadMediaBean media = checkImages.get(0);
                    notifyItemChanged(media.position, "payload");
                }
                checkImages.clear();
            }
            checkImages.add(image);
            image.num = checkImages.size();
            setAnimation(contentHolder, true);
            setColorFilter(contentHolder, true);
            callBack.onSelect(checkImages);
            //第一个参数未局部刷新的item值，第二个参数相当于一个标记，任何类型都可以，只要不为空就可以解决局部刷新item导致图片闪烁问题
            notifyItemChanged(position, "payload");
        }
    }

    /**
     * 更新数字样式选择的顺序
     */
    private void subSelectPosition() {
        if (checkNumMode) {
            for (int i = 0; i < checkImages.size(); i++) {
                LoadMediaBean media = checkImages.get(i);
                media.num = i + 1;
                notifyItemChanged(media.position, "payload");
            }
        }
    }

    /**
     * 设置选中和取消的动画
     */
    public void setAnimation(ViewHolder contentHolder, boolean isSelect) {
        if (isSelect) {
            contentHolder.check_cb.startAnimation(animation);
        } else {
            contentHolder.check_cb.clearAnimation();
        }
    }

    /**
     * 设置选中和取消的蒙层
     */
    public void setColorFilter(ViewHolder contentHolder, boolean isSelect) {
        if (isSelect) {
            contentHolder.picture_iv.setColorFilter(ContextCompat.getColor(mContext, R.color.picture_color_80000000), PorterDuff.Mode.SRC_ATOP);
        } else {
            contentHolder.picture_iv.setColorFilter(ContextCompat.getColor(mContext, R.color.picture_color_20000000), PorterDuff.Mode.SRC_ATOP);
        }
    }

    /**
     * 是否已选中
     */
    public boolean isSelected(LoadMediaBean image) {
        boolean result = false;
        for (LoadMediaBean bean : checkImages) {
            if (bean.getRealPath().equals(image.getRealPath())) {
                result = true;
                break;
            }
        }
        return result;
    }

    /**
     * item点击操作
     */
    public void clickItem(int position, LoadMediaBean image) {
        //判断图片是否存在（如原图路径不存在或者路径存在但文件不存在）
        if (!new File(image.getAbsolutePath()).exists()) {
            Toast.makeText(mContext.getApplicationContext(), PictureHelper.tipsFileError(mContext, image.getMimeType()), Toast.LENGTH_SHORT).show();
            return;
        }
        PicturePreviewActivity.openActivity(mContext, mImagesList, checkImages, !TextUtils.isEmpty(optionsBean.outputCameraPath) ? position - 1 : position);
    }

}
