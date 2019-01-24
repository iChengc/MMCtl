package com.cc.wechatmanager.actions;

public class ActionResult {
    public boolean success;
    public String message;
    public Object data;

    public ActionResult() {

    }

    public ActionResult(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public static ActionResult successResult() {
        ActionResult result = new ActionResult();
        result.success = true;
        result.message = "ok";
        return result;
    }

    public static ActionResult failedResult(Throwable thr) {
        ActionResult result = new ActionResult();
        result.success = false;
        result.message = thr == null ? "unknown error" : thr.getClass().getName() + ":" + thr.getMessage();
        return result;
    }
}
