package com.lookballs.pictureselector.config;

import android.os.Parcel;
import android.os.Parcelable;

import com.lookballs.pictureselector.R;

public class PictureOptionsBean implements Parcelable {

    public int mimeType;
    public String appointFolderName;
    public String appointShowName;
    public boolean isGif;
    public long videoMaxS;
    public long videoMinS;
    public int maxSelectNum;
    public int minSelectNum;
    public int spanCount;
    public String outputCameraPath;
    public int themeStyleId;
    public int selectMode;
    public boolean isSelectImageVideo;
    public int recordVideoSecond;
    public int videoQuality;
    public boolean isPreview;
    public static PictureOptions.OnPermissionDenied onPermissionDenied;
    public static PictureOptions.OnPictureSelectResult onPictureSelectResult;
    public static PictureOptions.OnCameraResult onCameraResult;

    public String[] specifiedFormat;

    private void reset() {
        mimeType = PictureConfig.TYPE_IMAGE;
        appointFolderName = "";
        appointShowName = "";
        isGif = false;
        videoMaxS = 0;
        videoMinS = 0;
        maxSelectNum = 0;
        minSelectNum = 9;
        spanCount = 4;
        outputCameraPath = "";
        themeStyleId = R.style.picture_default_style;
        selectMode = PictureConfig.MULTIPLE;
        isSelectImageVideo = false;
        recordVideoSecond = 60;
        videoQuality = 1;
        isPreview = true;
        specifiedFormat = null;
    }

    public static PictureOptionsBean getInstance() {
        return InstanceHolder.INSTANCE;
    }

    public static PictureOptionsBean getCleanInstance() {
        PictureOptionsBean selectionSpec = getInstance();
        selectionSpec.reset();
        return selectionSpec;
    }

    /**
     * 释放监听器
     */
    public static void destroy() {
        PictureOptionsBean.onPermissionDenied = null;
        PictureOptionsBean.onPictureSelectResult = null;
        PictureOptionsBean.onCameraResult = null;
    }

    private static final class InstanceHolder {
        private static final PictureOptionsBean INSTANCE = new PictureOptionsBean();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.mimeType);
        dest.writeString(this.appointFolderName);
        dest.writeString(this.appointShowName);
        dest.writeByte(this.isGif ? (byte) 1 : (byte) 0);
        dest.writeLong(this.videoMaxS);
        dest.writeLong(this.videoMinS);
        dest.writeInt(this.maxSelectNum);
        dest.writeInt(this.minSelectNum);
        dest.writeInt(this.spanCount);
        dest.writeString(this.outputCameraPath);
        dest.writeInt(this.themeStyleId);
        dest.writeInt(this.selectMode);
        dest.writeByte(this.isSelectImageVideo ? (byte) 1 : (byte) 0);
        dest.writeInt(this.recordVideoSecond);
        dest.writeInt(this.videoQuality);
        dest.writeByte(this.isPreview ? (byte) 1 : (byte) 0);
        dest.writeStringArray(this.specifiedFormat);
    }

    public PictureOptionsBean() {

    }

    protected PictureOptionsBean(Parcel in) {
        this.mimeType = in.readInt();
        this.appointFolderName = in.readString();
        this.appointShowName = in.readString();
        this.isGif = in.readByte() != 0;
        this.videoMaxS = in.readLong();
        this.videoMaxS = in.readLong();
        this.maxSelectNum = in.readInt();
        this.minSelectNum = in.readInt();
        this.spanCount = in.readInt();
        this.outputCameraPath = in.readString();
        this.themeStyleId = in.readInt();
        this.selectMode = in.readInt();
        this.isSelectImageVideo = in.readByte() != 0;
        this.recordVideoSecond = in.readInt();
        this.videoQuality = in.readInt();
        this.isPreview = in.readByte() != 0;
        this.specifiedFormat = in.createStringArray();
    }

    public static final Creator<PictureOptionsBean> CREATOR = new Creator<PictureOptionsBean>() {
        @Override
        public PictureOptionsBean createFromParcel(Parcel source) {
            return new PictureOptionsBean(source);
        }

        @Override
        public PictureOptionsBean[] newArray(int size) {
            return new PictureOptionsBean[size];
        }
    };
}
