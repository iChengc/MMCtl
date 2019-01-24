package com.cc.wechatmanager;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Process;

/**
 * Created by ChengC on 2016/7/7.
 */

public class WorkerHandler {
    private static final String LOG_TAG = "WorkerHandler";

    private static WorkerHandler mInstance;
    private Handler mHandler;
    private Looper mWorkLooper;
    private WorkerHandler() {
    }

    public static WorkerHandler getInstance() {
        if (mInstance == null) {
            mInstance = new WorkerHandler();
        }

        return mInstance;
    }

    public void init() {
        HandlerThread thread = new HandlerThread(LOG_TAG, Process.THREAD_PRIORITY_FOREGROUND);
        thread.start();

        mWorkLooper = thread.getLooper();
        mHandler = new Handler(mWorkLooper);
    }

    public void destroy() {
        mHandler = null;
        mWorkLooper.quit();
        mWorkLooper = null;
    }

    /**
     * <p>Causes the Runnable to be added to the message queue.
     * The runnable will be run on the user interface thread.</p>
     *
     * @param action The Runnable that will be executed.
     *
     * @return Returns true if the Runnable was successfully placed in to the
     *         message queue.  Returns false on failure, usually because the
     *         looper processing the message queue is exiting.
     *
     * @see #postOnWorkThreadDelayed
     * @see #removeCallbacks
     */
    public static boolean postOnWorkThread(Runnable action) {
        return getInstance().mHandler.post(action);
    }

    public static boolean postOnWorkThreadDelayed(Runnable action, long delayMillis) {
        return getInstance().mHandler.postDelayed(action, delayMillis);
    }

    /**
     * <p>Removes the specified Runnable from the message queue.</p>
     *
     * @param action The Runnable to remove from the message handling queue
     *
     * @return true if this view could ask the Handler to remove the Runnable,
     *         false otherwise. When the returned value is true, the Runnable
     *         may or may not have been actually removed from the message queue
     *         (for instance, if the Runnable was not in the queue already.)
     *
     * @see #postOnWorkThread
     * @see #postOnWorkThreadDelayed
     */
    public static boolean removeCallbacks(Runnable action) {
        if (action != null) {
            getInstance().mHandler.removeCallbacks(action);
        }
        return true;
    }
}
