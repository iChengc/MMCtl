package com.cc.core.actions;

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

    public static ActionResult failedResult(String message) {
        ActionResult result = new ActionResult();
        result.success = false;
        result.message = message;
        return result;
    }

    public boolean isSuccess() {
         return success;
    }

    @Override
    public String toString() {
        return "ActionResult{" +
                "success=" + success +
                ", message='" + message + '\'' +
                ", data=" + data +
                '}';
    }
}
