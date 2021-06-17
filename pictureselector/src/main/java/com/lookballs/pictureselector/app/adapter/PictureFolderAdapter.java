package com.lookballs.pictureselector.app.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.SizeUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.lookballs.pictureselector.R;
import com.lookballs.pictureselector.app.bean.LoadMediaBean;
import com.lookballs.pictureselector.app.bean.LoadMediaFolderBean;

import java.util.ArrayList;

public class PictureFolderAdapter extends RecyclerView.Adapter<PictureFolderAdapter.ViewHolder> {

    private Context mContext;
    private ArrayList<LoadMediaFolderBean> folders = new ArrayList<>();

    public PictureFolderAdapter(Context mContext) {
        this.mContext = mContext;
    }

    public void bindFolderData(ArrayList<LoadMediaFolderBean> folders) {
        if (folders != null) {
            this.folders = folders;
        }
        notifyDataSetChanged();
    }

    public void updateFolderData(ArrayList<LoadMediaFolderBean> folders) {
        for (int i = 0; i < getFolderData().size(); i++) {
            LoadMediaFolderBean folder = getFolderData().get(i);
            LoadMediaFolderBean updateFolder = folders.get(0);
            if (updateFolder.getBucketId() == folder.getBucketId()) {
                updateFolder.setChecked(true);
                getFolderData().set(i, updateFolder);
                notifyItemChanged(i);
                break;
            }
        }
    }

    public ArrayList<LoadMediaFolderBean> getFolderData() {
        if (folders == null) {
            folders = new ArrayList<>();
        }
        return folders;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_picture_folder, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        int pos = holder.getAdapterPosition();

        LoadMediaFolderBean folder = folders.get(pos);
        String name = folder.getName();
        long bucketId = folder.getBucketId();
        int imageNum = folder.getImageNum();
        String firstImagePath = folder.getFirstImagePath();
        boolean isChecked = folder.isChecked();
        int checkedNum = folder.getCheckedNum();

        holder.sign_tv.setVisibility(checkedNum > 0 ? View.VISIBLE : View.INVISIBLE);
        holder.itemView.setSelected(isChecked);
        holder.imageNum_tv.setText(String.valueOf(imageNum));
        holder.folderName_tv.setText(name);

        RequestOptions options = new RequestOptions()
                .placeholder(R.drawable.picture_ll_pic_placeholder)
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .override(SizeUtils.dp2px(55), SizeUtils.dp2px(55));
        Glide.with(holder.itemView.getContext())
                .asBitmap()
                .load(firstImagePath)
                .apply(options)
                .into(new BitmapImageViewTarget(holder.firstImage_iv) {
                    @Override
                    protected void setResource(Bitmap resource) {
                        RoundedBitmapDrawable circularBitmapDrawable = RoundedBitmapDrawableFactory.create(mContext.getApplicationContext().getResources(), resource);
                        circularBitmapDrawable.setCornerRadius(8);
                        holder.firstImage_iv.setImageDrawable(circularBitmapDrawable);
                    }
                });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean isSelected = folder.isChecked();//是否已经选中
                if (onItemClickListener != null) {
                    for (LoadMediaFolderBean mediaFolder : folders) {
                        mediaFolder.setChecked(false);
                    }
                    folder.setChecked(true);
                    notifyDataSetChanged();
                    onItemClickListener.onItemClick(bucketId, folder.getName(), folder.getImages(), isSelected);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return folders.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView firstImage_iv;
        TextView folderName_tv, imageNum_tv, sign_tv;

        public ViewHolder(View itemView) {
            super(itemView);
            firstImage_iv = itemView.findViewById(R.id.firstImage_iv);
            folderName_tv = itemView.findViewById(R.id.folderName_tv);
            imageNum_tv = itemView.findViewById(R.id.imageNum_tv);
            sign_tv = itemView.findViewById(R.id.sign_tv);
        }
    }

    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(long bucketId, String folderName, ArrayList<LoadMediaBean> images, boolean isSelected);
    }
}
