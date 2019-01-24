package com.cc.core.actions;

public class NullAction implements Action {
    private String missingKey;
    public NullAction(){}
    public NullAction(String missingKey) {
        this.missingKey = missingKey;
    }
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
