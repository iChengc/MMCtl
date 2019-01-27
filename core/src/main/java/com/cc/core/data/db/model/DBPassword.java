package com.cc.core.data.db.model;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class DBPassword {
    @Id
    private String wechatId;
    private String password;
    private String path;
    private String decryptSql;
    @Generated(hash = 413854210)
    public DBPassword(String wechatId, String password, String path,
            String decryptSql) {
        this.wechatId = wechatId;
        this.password = password;
        this.path = path;
        this.decryptSql = decryptSql;
    }
    @Generated(hash = 267195278)
    public DBPassword() {
    }
    public String getWechatId() {
        return this.wechatId;
    }
    public void setWechatId(String wechatId) {
        this.wechatId = wechatId;
    }
    public String getPassword() {
        return this.password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public String getPath() {
        return this.path;
    }
    public void setPath(String path) {
        this.path = path;
    }
    public String getDecryptSql() {
        return this.decryptSql;
    }
    public void setDecryptSql(String decryptSql) {
        this.decryptSql = decryptSql;
    }
}
