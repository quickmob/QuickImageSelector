package com.lookballs.pictureselector.app.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.PagerAdapter;

import com.blankj.utilcode.util.UriUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.ImageViewState;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.github.chrisbanes.photoview.PhotoView;
import com.lookballs.pictureselector.R;
import com.lookballs.pictureselector.app.bean.LoadMediaBean;
import com.lookballs.pictureselector.helper.PictureHelper;

import java.io.File;
import java.util.ArrayList;

/**
 * 图片预览
 */
public class PicturePreviewAdapter extends PagerAdapter {

    private Context mContext;
    public ArrayList<LoadMediaBean> mImgList;
    private OnItemClickListener onItemClick;

    public PicturePreviewAdapter(Context context, ArrayList<LoadMediaBean> imgList) {
        this.mContext = context;
        if (imgList != null) {
            this.mImgList = imgList;
        } else {
            this.mImgList = new ArrayList<>();
        }
    }

    @Override
    public int getCount() {
        return mImgList == null ? 0 : mImgList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        (container).removeView((View) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View contentView = LayoutInflater.from(container.getContext()).inflate(R.layout.item_preview_photo, container, false);

        //常规图控件
        PhotoView photoView = contentView.findViewById(R.id.preview_pv);
        //长图控件
        SubsamplingScaleImageView longImg = contentView.findViewById(R.id.longImg_ssiv);
        //播放按钮
        ImageView videoPlay = contentView.findViewById(R.id.videoPlay_iv);

        LoadMediaBean infoBean = mImgList.get(position);
        String path = infoBean.getPath();
        String pcitureType = infoBean.getPictureType();
        boolean isLongImg = PictureHelper.isLongImg(infoBean);
        boolean isGif = PictureHelper.isImageGif(path);
        boolean isVideo = PictureHelper.isVideo(pcitureType);

        photoView.setVisibility(isLongImg && !isGif ? View.GONE : View.VISIBLE);
        longImg.setVisibility(isLongImg && !isGif ? View.VISIBLE : View.GONE);

        if (isVideo) {
            videoPlay.setVisibility(View.VISIBLE);
        } else {
            videoPlay.setVisibility(View.GONE);
        }

        RequestOptions options = new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.NONE);
        if (isGif) {
            Glide.with(container.getContext())
                    .asGif()
                    .load(path)
                    .apply(options)
                    .into(photoView);
        } else if (isLongImg) {
            Glide.with(container.getContext())
                    .asBitmap()
                    .load(path)
                    .apply(options)
                    .into(new CustomTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                            displayLongPic(resource, longImg);
                        }

                        @Override
                        public void onLoadCleared(@Nullable Drawable placeholder) {

                        }
                    });
        } else {
            Glide.with(container.getContext())
                    .asDrawable()
                    .load(path)
                    .apply(options)
                    .into(photoView);
        }

        photoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickOnItem(position, infoBean);
            }
        });
        videoPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickOnPlay(path);
            }
        });

        (container).addView(contentView, 0);
        return contentView;
    }


    public void setOnItemClickListener(OnItemClickListener l) {
        onItemClick = l;
    }

    public interface OnItemClickListener {
        void onItemClick(int position, LoadMediaBean infoBean);
    }

    /**
     * 点击item操作
     */
    private void clickOnItem(int position, LoadMediaBean infoBean) {
        if (onItemClick != null) {
            onItemClick.onItemClick(position, infoBean);
        }
    }

    /**
     * 点击播放按钮操作
     */
    private void clickOnPlay(String path) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        Uri uriPath = UriUtils.file2Uri(new File(path));
        intent.setDataAndType(uriPath, "video/*");
        try {
            mContext.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(mContext.getApplicationContext(), mContext.getResources().getString(R.string.picture_toast_video_not_play), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 加载长图
     *
     * @param bmp
     * @param longImg
     */
    private void displayLongPic(Bitmap bmp, SubsamplingScaleImageView longImg) {
        longImg.setQuickScaleEnabled(true);
        longImg.setZoomEnabled(true);
        longImg.setPanEnabled(true);
        longImg.setDoubleTapZoomDuration(100);
        longImg.setMinimumScaleType(SubsamplingScaleImageView.SCALE_TYPE_CENTER_CROP);
        longImg.setDoubleTapZoomDpi(SubsamplingScaleImageView.ZOOM_FOCUS_CENTER);
        longImg.setImage(ImageSource.cachedBitmap(bmp), new ImageViewState(0, new PointF(0, 0), 0));
    }

}
