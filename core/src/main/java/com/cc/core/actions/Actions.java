package com.cc.core.actions;

import com.cc.core.log.KLog;
import com.cc.core.rpc.Rpc;
import com.cc.core.rpc.RpcArgs;
import com.cc.core.utils.StrUtils;
import com.google.gson.annotations.SerializedName;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Actions {
    private static Map<String, Action> actions = new HashMap<>();

    static {
        loadActions();
    }

    private static void loadActions() {
        /*try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(MyApplication.application.getAssets().open("actions.ini")));
            try {
                for (String line = reader.readLine(); line != null; line = reader.readLine()) {
                    String name = line.trim();
                    if (name.length() > 0) addAction(name);
                }
            } finally {
                reader.close();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }*/
        try {
            for (String a : ActionNames.ACTION_NAMES) {
                addAction(a);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("rawtypes")
    private static void addAction(String className) throws Exception {
        Class action = Class.forName(className);
        if (isAction(action)) {
            put((Action) action.newInstance());
        }
    }

    @SuppressWarnings("rawtypes")
    private static boolean isAction(Class actionCandidate) {
        boolean isImplementation = !actionCandidate.isInterface();
        return isImplementation && Action.class.isAssignableFrom(actionCandidate);
    }

    private static void put(Action action) {
        if (getActions().containsKey(action.key())) {
            Action duplicate = getActions().get(action.key());
            throw new RuntimeException("Found duplicate action key:'" + action.key() + "'. [" + duplicate.getClass().getName() + "," + action.getClass().getName() + "]");
        }
        if (action.key() == null) {
            System.out.println("Skipping " + action.getClass() + ". Key is null.");
        } else {
            getActions().put(action.key(), action);
        }
    }

    public static Action lookup(String key) {
        Action action = getActions().get(key);
        if (action == null) {
            action = new NullAction();
            ((NullAction) action).setMissingKey(key);
        }
        return action;
    }

    public static Action lookup(Class<? extends Action> clazz) {
        for (String key : actions.keySet()) {
            if (actions.get(key).getClass() == clazz) {
                return actions.get(key);
            }
        }

        return new NullAction(clazz.getName());
    }

    public static Map<String, Action> getActions() {
        return actions;
    }

    public static ActionResult execute(Class<? extends Action> clazz, Object... args) {
        Action action = lookup(clazz);
        if (isWechatAction(action)) {
            try {
                return Rpc.call(RpcArgs.newMessage(RawAction.fromAction(action, args)));
            } catch (Exception e) {
                e.printStackTrace();
                return ActionResult.failedResult(e);
            }
        } else {
            return action.execute(args);
        }
    }

    public static String executeCommand(String key, Object... args) {
        Action action = lookup(key);
        KLog.e("--->>>>", "execute action:" + action.key());
        return StrUtils.toJson(action.execute(args));
    }

    public static ActionResult receivedAction(String rawAction) {
        RawAction raw = StrUtils.fromJson(rawAction, RawAction.class);
        KLog.e(raw.toString());
        Action action = lookup(raw.actionName);
        return action.execute(raw.args != null ? raw.args.toArray() : null);
    }

    private static boolean isWechatAction(Action action) {
        return action.key().startsWith("wechat:");
    }

    public static class RawAction {
        @SerializedName("actionName")
        public String actionName;

        @SerializedName("args")
        public List<? extends Object> args;

        @Override
        public String toString() {
            return "RawAction{" +
                    "actionName='" + actionName + '\'' +
                    ", args=" + args +
                    '}';
        }

        public static RawAction fromAction(Action action, Object... args) {
            RawAction a = new RawAction();
            a.actionName = action.key();
            a.args = Arrays.asList(args);
            return a;
        }
    }
}
