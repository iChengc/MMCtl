package com.cc.core.actions

interface Action {
    /**
     * execute the actions
     * @param args
     * @return
     */
    fun execute(actionId : String, vararg args :  Any?): ActionResult?

    /**
     * The key of the action
     * @return
     */
    fun key(): String?
}