package com.cc.wechatmanager.actions;

public interface Action {
    /**
     * execute the actions
     * @param args
     * @return
     */
    ActionResult execute(Object... args);

    /**
     * The key of the action
     * @return
     */
    String key();
}
