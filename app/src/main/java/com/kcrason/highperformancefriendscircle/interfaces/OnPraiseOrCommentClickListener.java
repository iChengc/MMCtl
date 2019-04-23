package com.kcrason.highperformancefriendscircle.interfaces;

import com.cc.core.wechat.model.sns.SnsComment;

public interface OnPraiseOrCommentClickListener {
    void onPraiseClick(int position);

    void onCommentClick(int position, SnsComment comment);
}
