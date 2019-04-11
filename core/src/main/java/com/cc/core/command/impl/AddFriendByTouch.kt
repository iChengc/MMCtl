package com.cc.core.command.impl

import com.cc.core.actions.Action
import com.cc.core.actions.ActionResult
import com.cc.core.actions.Actions
import com.cc.core.accessibility.impl.AddFriendActionByAcc

class AddFriendByTouch : Action {
  override fun execute(actionId : String, vararg args: Any?): ActionResult? {

    return Actions.execute(AddFriendActionByAcc::class.java, actionId, *args)
  }

  override fun key(): String? {
    return "addFriendByTouch"
  }

}