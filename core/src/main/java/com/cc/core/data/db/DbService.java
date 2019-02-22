package com.cc.core.data.db;

import android.os.Looper;

import com.cc.core.Constant;
import com.cc.core.WorkerHandler;
import com.cc.core.actions.ActionResult;
import com.cc.core.command.Callback;
import com.cc.core.data.db.model.DBPassword;
import com.cc.core.data.db.model.User;
import com.cc.core.log.KLog;
import com.cc.core.rpc.Rpc;
import com.cc.core.rpc.RpcArgs;
import com.cc.core.utils.StrUtils;

import org.greenrobot.greendao.AbstractDao;

import java.util.HashMap;
import java.util.Map;

public class DbService {
    private static DbService mInstance = new DbService();
    private DbService(){}

    private final static int INSERT = 0;
    private final static int UPDATE = 1;
    private final static int DELETE = 2;
    private final static int GET = 4;
    private final static int RAW = 5;

    public static DbService getInstance() {
        return mInstance;
    }

    public void init() {
        Rpc.asRpcServer(Constant.DBHANDLER_PORT);
    }

    public void insertLoginUser(User user, Callback callback) {
        execute(genRpcArgs(INSERT, user), callback);
    }

    public void insertDbPassword(DBPassword password, Callback callback) {
        execute(genRpcArgs(INSERT, password), callback);
    }

    private void execute(final RpcArgs args, final Callback callback) {
        if (Looper.getMainLooper().getThread() == Thread.currentThread()) {
            WorkerHandler.postOnWorkThread(new Runnable() {
                @Override
                public void run() {
                    executeInner(args, callback);
                }
            });
        } else {
            executeInner(args, callback);
        }
    }

    private void executeInner(RpcArgs args, final Callback callback) {
        ActionResult result = Rpc.call(args);
        KLog.e("insertLoginUser result:" + result.toString());
        if (callback != null) {
            callback.onResult(StrUtils.toJson(result));
        }
    }

    private RpcArgs genRpcArgs(int operation, Object dao) {
        RpcArgs args = new RpcArgs();
        args.setTime(System.currentTimeMillis());
        args.setType(RpcArgs.CallType.EXECUTE_DB);
        Map<String, Object> data = new HashMap<>();
        data.put("daoClass", dao.getClass().getName());
        data.put("data", StrUtils.toJson(dao));
        data.put("operation", String.valueOf(operation));
        args.setData(StrUtils.toJson(data));

        return args;
    }

    public ActionResult deliverDbOperation(String raw) {
        if (raw == null) {
            return ActionResult.Companion.successResult();
        }
        try {
            KLog.e(">>>>>>deliverDbOperation:" + raw);
            Map rawData = StrUtils.fromJson(raw, Map.class);
            Class daoClass = Class.forName(rawData.get("daoClass").toString());
            AbstractDao dao = getDao(daoClass);
            int operation = Integer.valueOf(rawData.get("operation").toString());
            Object data = StrUtils.fromJson(rawData.get("data").toString(), daoClass);
            switch (operation) {
                case INSERT:
                    long insert = dao.insertOrReplace(data);
                    if (insert > 0) {
                        return  ActionResult.Companion.successResult();
                    } else {
                        return ActionResult.Companion.failedResult("Insert return " + operation);
                    }

            }
            return ActionResult.Companion.failedResult("Unknown operation:" + operation);
        } catch (Exception e) {
            KLog.e("Failed to deliver DB Operation", e);
            return ActionResult.Companion.failedResult(e);
        }
    }

    private AbstractDao getDao(Class className) {
        if (className == User.class) {
            return DBManager.getInstance().getSession().getUserDao();
        }

        return null;
    }
}
