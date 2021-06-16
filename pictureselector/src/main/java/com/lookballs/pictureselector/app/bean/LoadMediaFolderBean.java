package com.lookballs.pictureselector.app.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class LoadMediaFolderBean implements Parcelable {
    private String name;
    private String path;
    private String firstImagePath;
    private int imageNum;
    private int checkedNum;
    private boolean isChecked;
    private ArrayList<LoadMediaBean> images = new ArrayList<>();
    private long bucketId;

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getFirstImagePath() {
        return firstImagePath;
    }

    public void setFirstImagePath(String firstImagePath) {
        this.firstImagePath = firstImagePath;
    }

    public long getBucketId() {
        return bucketId;
    }

    public void setBucketId(long bucketId) {
        this.bucketId = bucketId;
    }

    public int getImageNum() {
        return imageNum;
    }

    public void setImageNum(int imageNum) {
        this.imageNum = imageNum;
    }

    public ArrayList<LoadMediaBean> getImages() {
        if (images == null) {
            images = new ArrayList<>();
        }
        return images;
    }

    public void setImages(ArrayList<LoadMediaBean> images) {
        this.images = images;
    }

    public int getCheckedNum() {
        return checkedNum;
    }

    public void setCheckedNum(int checkedNum) {
        this.checkedNum = checkedNum;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeString(this.path);
        dest.writeString(this.firstImagePath);
        dest.writeLong(this.bucketId);
        dest.writeInt(this.imageNum);
        dest.writeInt(this.checkedNum);
        dest.writeByte(this.isChecked ? (byte) 1 : (byte) 0);
        dest.writeTypedList(this.images);
    }

    public LoadMediaFolderBean() {

    }

    protected LoadMediaFolderBean(Parcel in) {
        this.name = in.readString();
        this.path = in.readString();
        this.firstImagePath = in.readString();
        this.bucketId = in.readLong();
        this.imageNum = in.readInt();
        this.checkedNum = in.readInt();
        this.isChecked = in.readByte() != 0;
        this.images = in.createTypedArrayList(LoadMediaBean.CREATOR);
    }

    public static final Creator<LoadMediaFolderBean> CREATOR = new Creator<LoadMediaFolderBean>() {
        @Override
        public LoadMediaFolderBean createFromParcel(Parcel source) {
            return new LoadMediaFolderBean(source);
        }

        @Override
        public LoadMediaFolderBean[] newArray(int size) {
            return new LoadMediaFolderBean[size];
        }
    };
}
