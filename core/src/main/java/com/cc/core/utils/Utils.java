package com.cc.core.utils;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.Context;
import android.text.TextUtils;
import android.view.accessibility.AccessibilityManager;

import com.cc.core.ApplicationContext;
import com.cc.core.wechat.model.WeChatMessage;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.BufferedSink;
import okio.BufferedSource;
import okio.Okio;

public class Utils {
    public static boolean sleep(long time) {
        try {
            Thread.sleep(time);
            return true;
        } catch (InterruptedException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean isEmpty(List<?> list) {
        return list == null || list.isEmpty();
    }


    /**
     * 判断AccessibilityService服务是否已经启动 * @param context * @param name * @return
     */
    public static boolean isStartAccessibilityService(Context context, String name) {
        AccessibilityManager am = (AccessibilityManager) context.getSystemService(Context.ACCESSIBILITY_SERVICE);
        List<AccessibilityServiceInfo> serviceInfos = am.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_GENERIC);

        for (AccessibilityServiceInfo info : serviceInfos) {
            String id = info.getId();
            if (id.contains(name)) {
                return true;
            }
        }
        return false;
    }

    public static Gson messageDeserializeGson() {
        return new GsonBuilder().registerTypeAdapter(WeChatMessage.class, new MessageTypeAdapter()).create();
    }

    public static String downloadFile(String url, boolean videoOrImage) {
        if (TextUtils.isEmpty(url)) {
            return null;
        }
        File file = new File(FileUtil.getExternalCacheDir(), MD5.getMD5(url) + (videoOrImage ? ".mp4" : ".jpg"));
        if (file.exists()) {
            return file.getAbsolutePath();
        }
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(url).build();
        try {
            Response response = client.newCall(request).execute();
            if (!response.isSuccessful()) {
                return null;
            }
            ResponseBody body = response.body();
            BufferedSource source = body.source();
            BufferedSink sink = Okio.buffer(Okio.sink(file));
            sink.writeAll(source);
            sink.flush();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return file.getAbsolutePath();
    }
}
