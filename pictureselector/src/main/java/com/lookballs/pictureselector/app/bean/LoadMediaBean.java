package com.lookballs.pictureselector.app.bean;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

public class LoadMediaBean implements Parcelable {
    private long id;//文件ID
    private String absolutePath;//文件绝对路径
    private String realPath;//适配了Android Q的真实文件路径
    private long duration;//视频时长
    private int mimeType;//需要显示的文件类型
    private String pictureType;//真实的文件类型
    private int width;//文件宽度
    private int height;//文件高度
    private long bucketId;//文件夹ID
    private String bucketDisplayName;//文件夹名
    private String displayName;//文件名
    private long size;//文件大小
    private long dateAdded;//文件创建时间

    public int position;
    public int num;

    public LoadMediaBean() {

    }

    public LoadMediaBean(long id, String absolutePath, String realPath, long duration, int mimeType, String pictureType, int width, int height, long bucketId, String bucketDisplayName, String displayName, long size, long dateAdded) {
        this.id = id;
        this.absolutePath = absolutePath;
        this.realPath = realPath;
        this.duration = duration;
        this.mimeType = mimeType;
        this.pictureType = pictureType;
        this.width = width;
        this.height = height;
        this.bucketId = bucketId;
        this.bucketDisplayName = bucketDisplayName;
        this.displayName = displayName;
        this.size = size;
        this.dateAdded = dateAdded;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getPictureType() {
        if (TextUtils.isEmpty(pictureType)) {
            pictureType = "image/jpeg";
        }
        return pictureType;
    }

    public void setPictureType(String pictureType) {
        this.pictureType = pictureType;
    }

    public String getAbsolutePath() {
        return absolutePath;
    }

    public void setAbsolutePath(String absolutePath) {
        this.absolutePath = absolutePath;
    }

    public String getRealPath() {
        return realPath;
    }

    public void setRealPath(String realPath) {
        this.realPath = realPath;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public int getMimeType() {
        return mimeType;
    }

    public void setMimeType(int mimeType) {
        this.mimeType = mimeType;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public long getBucketId() {
        return bucketId;
    }

    public void setBucketId(long bucketId) {
        this.bucketId = bucketId;
    }

    public String getBucketDisplayName() {
        return bucketDisplayName;
    }

    public void setBucketDisplayName(String bucketDisplayName) {
        this.bucketDisplayName = bucketDisplayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public long getDateAdded() {
        return dateAdded;
    }

    public void setDateAdded(long dateAdded) {
        this.dateAdded = dateAdded;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeString(this.absolutePath);
        dest.writeString(this.realPath);
        dest.writeLong(this.duration);
        dest.writeInt(this.mimeType);
        dest.writeString(this.pictureType);
        dest.writeInt(this.width);
        dest.writeInt(this.height);
        dest.writeLong(this.bucketId);
        dest.writeString(this.bucketDisplayName);
        dest.writeString(this.displayName);
        dest.writeLong(this.size);
        dest.writeLong(this.dateAdded);

        dest.writeInt(this.position);
        dest.writeInt(this.num);
    }

    protected LoadMediaBean(Parcel in) {
        this.id = in.readLong();
        this.absolutePath = in.readString();
        this.realPath = in.readString();
        this.duration = in.readLong();
        this.mimeType = in.readInt();
        this.pictureType = in.readString();
        this.width = in.readInt();
        this.height = in.readInt();
        this.bucketId = in.readLong();
        this.bucketDisplayName = in.readString();
        this.displayName = in.readString();
        this.size = in.readLong();
        this.dateAdded = in.readLong();

        this.position = in.readInt();
        this.num = in.readInt();
    }

    public static final Creator<LoadMediaBean> CREATOR = new Creator<LoadMediaBean>() {
        @Override
        public LoadMediaBean createFromParcel(Parcel source) {
            return new LoadMediaBean(source);
        }

        @Override
        public LoadMediaBean[] newArray(int size) {
            return new LoadMediaBean[size];
        }
    };
}
