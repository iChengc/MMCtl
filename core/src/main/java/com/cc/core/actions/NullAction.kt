package com.cc.core.actions

class NullAction : Action {
    private var missingKey: String? = null
    constructor()
    constructor(missingKey: String) {
        this.missingKey = missingKey
    }

    override fun execute(vararg args: Any?): ActionResult? {
        return ActionResult(false, "Could not find implementation for: '$missingKey'")
    }

    override fun key(): String? {
        return "NullAction"
    }

    fun setMissingKey(missingKey: String?) {
        this.missingKey = missingKey
    }
}