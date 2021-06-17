package com.lookballs.app.pictureselector;

import android.app.Application;
import android.content.Context;

import com.lookballs.app.pictureselector.http.CustomOkHttpClient;
import com.lookballs.app.pictureselector.http.DataConverter;
import com.lookballs.http.QuickHttp;
import com.lookballs.http.config.HttpConfig;

import okhttp3.Dns;

public class StartApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        initOkhttpConfig(this);
    }

    private void initOkhttpConfig(Context mContext) {
        CustomOkHttpClient.getInstance().createOkHttpClient(15000, Dns.SYSTEM);

        HttpConfig.Builder builder = new HttpConfig.Builder();
        builder.setLogEnabled(true);
        builder.setHttpClient(CustomOkHttpClient.getInstance().getOkHttpClient());
        builder.setDataConverter(new DataConverter());
        QuickHttp.init(builder.build(mContext));
    }
}
