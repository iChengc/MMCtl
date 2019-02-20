package com.cc.core.actions.accessibility;

import android.accessibilityservice.AccessibilityService;
import android.content.Intent;
import android.view.accessibility.AccessibilityEvent;

import com.cc.core.log.KLog;

public class WechatAccessibilityService extends AccessibilityService {
    private static final String TAG = WechatAccessibilityService.class.getSimpleName();
    private static WechatAccessibilityService mInstance;

    //public static final String ACCESSIBILITY_SERVICE_NAME = "com.cc.core/com.cc.core.actions.accessibility.WechatAccessibilityService";
    public WechatAccessibilityService() {
        mInstance = this;
    }

    public static WechatAccessibilityService getInstance() {
        return mInstance;
    }

    @Override
    public void onCreate() {
        mInstance = this;
        super.onCreate();
        KLog.d(TAG, "onServiceConnected");
    }

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        KLog.d(TAG, "onServiceConnected");
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        mInstance = this;
        AccessibilityUtil.notifyAccessibilityEvent(event);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        KLog.d(TAG, "onUnbind");
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onInterrupt() {
        KLog.d(TAG, "onInterrupt");
    }
}
