package com.cc.core.actions

class NullAction : Action {
    private var missingKey: String? = null
    constructor()
    constructor(missingKey: String) {
        this.missingKey = missingKey
    }

    override fun execute(actionId : String, vararg args: Any?): ActionResult? {
        return ActionResult(actionId, false, "Could not find implementation for: '$missingKey'")
    }

    override fun key(): String? {
        return "NullAction"
    }

    fun setMissingKey(missingKey: String?) {
        this.missingKey = missingKey
    }
}