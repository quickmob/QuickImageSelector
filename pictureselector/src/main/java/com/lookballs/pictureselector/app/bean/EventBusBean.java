package com.lookballs.pictureselector.app.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class EventBusBean implements Parcelable {

    public int what;
    public int position;
    public ArrayList<LoadMediaBean> mediaBeans = new ArrayList<>();

    public EventBusBean() {
        super();
    }

    public EventBusBean(int what) {
        super();
        this.what = what;
    }

    public EventBusBean(int what, ArrayList<LoadMediaBean> mediaBeans) {
        super();
        this.what = what;
        this.mediaBeans = mediaBeans;
    }

    public EventBusBean(int what, int position) {
        super();
        this.what = what;
        this.position = position;
    }

    public EventBusBean(int what, ArrayList<LoadMediaBean> mediaBeans, int position) {
        super();
        this.what = what;
        this.position = position;
        this.mediaBeans = mediaBeans;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.what);
        dest.writeInt(this.position);
        dest.writeTypedList(this.mediaBeans);
    }

    protected EventBusBean(Parcel in) {
        this.what = in.readInt();
        this.position = in.readInt();
        this.mediaBeans = in.createTypedArrayList(LoadMediaBean.CREATOR);
    }

    public static final Creator<EventBusBean> CREATOR = new Creator<EventBusBean>() {
        @Override
        public EventBusBean createFromParcel(Parcel source) {
            return new EventBusBean(source);
        }

        @Override
        public EventBusBean[] newArray(int size) {
            return new EventBusBean[size];
        }
    };
}
