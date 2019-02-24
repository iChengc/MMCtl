package com.cc.core.data.db;


import com.cc.core.ApplicationContext;

class DBManager {

    private DaoMaster mDaoMaster;
    private DaoSession mDaoSession;
    private static DBManager mInstance;

    private DBManager(){
        if (mInstance == null) {
            DaoMaster.DevOpenHelper devOpenHelper = new DaoMaster.DevOpenHelper(ApplicationContext.application(), "messenger.db");
            mDaoMaster = new DaoMaster(devOpenHelper.getWritableDatabase());
            mDaoSession = mDaoMaster.newSession();
        }
    }

    public static DBManager getInstance() {
        if (mInstance == null) {
            // 保证异步处理安全操作
            synchronized (DBManager.class) {
                if (mInstance == null) {
                    mInstance = new DBManager();
                }
            }
        }
        return mInstance;
    }

    public DaoMaster getMaster() {
        return mDaoMaster;
    }
    public DaoSession getSession() {
        return mDaoSession;
    }
}
