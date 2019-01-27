package com.cc.core.data.db.model;

import org.greenrobot.greendao.annotation.Id;

public class Fridend {
    @Id
    private String wechatId;
    // 微信号
    private String alias;
    private String nickname;
}
