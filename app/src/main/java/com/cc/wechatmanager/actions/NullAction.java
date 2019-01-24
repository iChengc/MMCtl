package com.cc.wechatmanager.actions;

public class NullAction implements Action {
    private String missingKey;
    @Override
    public ActionResult execute(Object... args) {
        return new ActionResult(false, "Could not find implementation for: '" + missingKey + "'");
    }

    @Override
    public String key() {
        return "NullAction";
    }

    public void setMissingKey(String missingKey) {
        this.missingKey = missingKey;
    }
}
