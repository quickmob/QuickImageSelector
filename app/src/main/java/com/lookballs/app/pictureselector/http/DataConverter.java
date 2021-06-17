package com.lookballs.app.pictureselector.http;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleOwner;

import com.blankj.utilcode.util.GsonUtils;
import com.google.gson.JsonSyntaxException;
import com.lookballs.http.core.converter.IDataConverter;
import com.lookballs.http.core.exception.DataException;
import com.lookballs.http.utils.QuickLogUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import okhttp3.Headers;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * 自定义数据解析
 */
public final class DataConverter implements IDataConverter {

    private static final String TAG = "DataConverter";

    private long getResponseTimeMill(Response response) {
        try {
            //格林威治时间：Thu, 06 Aug 2020 06:45:10 GMT
            SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss 'GMT'", Locale.ENGLISH);
            Date date = dateFormat.parse(response.header("Date"));
            long gmtTime = date.getTime();
            //转换为当前时区时间
            int offset = Calendar.getInstance().get(Calendar.ZONE_OFFSET);//获取当前时区的偏移时间（单位毫秒）
            long zoneTime = gmtTime + offset;
            return zoneTime;
        } catch (Exception e) {
            return 0;
        }
    }

    private void printText(Response response, String text) {
        StringBuilder sb = new StringBuilder();
        sb.append("请求结果\n");
        sb.append("Url：").append(response.request().url()).append("\n");
        sb.append("ResponseCode：").append(response.code()).append("\n");
        sb.append("ResponseResult：");
        QuickLogUtils.json(QuickLogUtils.logTag, sb.toString(), text);
    }

    @Override
    public Object onSucceed(@Nullable LifecycleOwner lifecycleOwner, Response response, Type type) throws Exception {
        long currentTime = getResponseTimeMill(response);
        QuickLogUtils.i(TAG, "当前服务器时间：" + currentTime);

        ResponseBody body = response.body();
        if (Response.class.equals(type)) {
            //如果这是一个Response对象
            return response;
        } else if (Headers.class.equals(type)) {
            //如果这是一个Headers对象
            return response.headers();
        } else if (Bitmap.class.equals(type)) {
            //如果这是一个Bitmap对象
            return BitmapFactory.decodeStream(body.byteStream());
        } else if (InputStream.class.equals(type)) {
            //如果这是一个InputStream对象
            return body.byteStream();
        } else {
            String text;
            try {
                text = body.string();
            } catch (IOException e) {
                throw new DataException("数据解析异常", e);
            }
            //打印文本
            printText(response, text);

            Object result = null;
            if (String.class.equals(type)) {
                //如果这是一个String对象
                result = text;
            } else if (JSONObject.class.equals(type)) {
                try {
                    //如果这是一个JSONObject对象
                    result = new JSONObject(text);
                } catch (JSONException e) {
                    throw new DataException("数据解析异常", e);
                }
            } else if (JSONArray.class.equals(type)) {
                try {
                    //如果这是一个JSONArray对象
                    result = new JSONArray(text);
                } catch (JSONException e) {
                    throw new DataException("数据解析异常", e);
                }
            } else {
                try {
                    //处理Json解析结果
                    result = GsonUtils.fromJson(text, type);
                } catch (JsonSyntaxException e) {
                    throw new DataException("数据解析异常", e);
                }
            }
            return result;
        }
    }

    @Override
    public Exception onFail(@Nullable LifecycleOwner lifecycleOwner, Exception e) {
        QuickLogUtils.printStackTrace(e);
        return e;
    }

}