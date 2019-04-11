package com.cc.core.accessibility;

import android.view.accessibility.AccessibilityEvent;

import com.cc.core.log.KLog;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class AccessibilityUtil {
    private static volatile CountDownLatch windowStateChangeLock;
    private static volatile CountDownLatch windowContentChangeLock;
    private static volatile CountDownLatch scrollLock;
    private static volatile CountDownLatch clickLock;
    private static volatile String waitingClassName;
    private static final int WAIT_ACTION_DEFAULT_TIMEOUT = 3 * 1000;

    public static void notifyAccessibilityEvent(AccessibilityEvent event) {
        KLog.d("AccessibilityUtil", "onAccessibilityEvent: " + event);
        switch (event.getEventType()) {
            case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED:
                if (windowStateChangeLock != null && (waitingClassName == null || event.getClassName().equals(waitingClassName))) {
                    windowStateChangeLock.countDown();
                }

                waitingClassName = null;
                break;
            case AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED:
                if (windowContentChangeLock != null && (waitingClassName == null || event.getClassName().equals(waitingClassName))) {
                    windowContentChangeLock.countDown();
                }

                waitingClassName = null;
                break;
            case AccessibilityEvent.TYPE_VIEW_SCROLLED:
                if (scrollLock != null) {
                    scrollLock.countDown();
                }
            case AccessibilityEvent.TYPE_VIEW_CLICKED:
                if (clickLock != null) {
                    clickLock.countDown();
                }
                break;
        }
    }

    public static boolean waitWindowStateChange() {
        return waitWindowStateChange(null, WAIT_ACTION_DEFAULT_TIMEOUT);
    }

    public static boolean waitWindowStateChange(int timeout) {
        return waitWindowStateChange(null, timeout);
    }

    public static boolean waitWindowStateChange(String className) {
        return waitWindowStateChange(className, WAIT_ACTION_DEFAULT_TIMEOUT);
    }

    public static boolean waitWindowStateChange(String className, int timeout) {
        waitingClassName = className;
        windowStateChangeLock = new CountDownLatch(1);

        boolean result;
        try {
            result = windowStateChangeLock.await(timeout, TimeUnit.MILLISECONDS);
            windowStateChangeLock = null;
            waitingClassName = null;
        } catch (InterruptedException ex) {
            result = false;
        }

        return result;
    }

    public static boolean waitWindowContentChange() {
        return waitWindowContentChange(null, WAIT_ACTION_DEFAULT_TIMEOUT);
    }

    public static boolean waitWindowContentChange(int timeout) {
        return waitWindowContentChange(null, timeout);
    }

    public static boolean waitWindowContentChange(String className) {
        return waitWindowContentChange(className, WAIT_ACTION_DEFAULT_TIMEOUT);
    }

    public static boolean waitWindowContentChange(String className, int timeout) {
        waitingClassName = className;
        windowContentChangeLock = new CountDownLatch(1);

        boolean result;
        try {
            result = windowContentChangeLock.await(timeout, TimeUnit.MILLISECONDS);
            windowContentChangeLock = null;
            waitingClassName = null;
        } catch (InterruptedException ex) {
            result = false;
        }

        return result;
    }

    public static boolean waitScroll() {
        return waitScroll(WAIT_ACTION_DEFAULT_TIMEOUT);
    }

    public static boolean waitScroll(int timeout) {
        scrollLock = new CountDownLatch(1);

        boolean result;
        try {
            result = scrollLock.await(timeout, TimeUnit.MILLISECONDS);
            scrollLock = null;
        } catch (InterruptedException ex) {
            result = false;
        }

        return result;
    }

    public static boolean waitClick() {
        return waitClick(WAIT_ACTION_DEFAULT_TIMEOUT);
    }

    public static boolean waitClick(int timeout) {
        clickLock = new CountDownLatch(1);

        boolean result;
        try {
            result = clickLock.await(timeout, TimeUnit.MILLISECONDS);
            clickLock = null;
        } catch (InterruptedException ex) {
            result = false;
        }

        return result;
    }
}
