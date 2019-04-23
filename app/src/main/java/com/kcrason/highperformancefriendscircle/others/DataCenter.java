package com.kcrason.highperformancefriendscircle.others;

import android.content.Context;
import android.text.TextUtils;

import com.cc.core.ApplicationContext;
import com.cc.core.wechat.Wechat;
import com.cc.core.wechat.model.sns.SnsComment;
import com.cc.core.wechat.model.sns.SnsInfo;
import com.cc.core.wechat.model.sns.SnsLike;
import com.kcrason.highperformancefriendscircle.Constants;
import com.kcrason.highperformancefriendscircle.beans.CommentBean;
import com.kcrason.highperformancefriendscircle.beans.emoji.EmojiBean;
import com.kcrason.highperformancefriendscircle.beans.emoji.EmojiDataSource;
import com.kcrason.highperformancefriendscircle.beans.FriendCircleBean;
import com.kcrason.highperformancefriendscircle.beans.OtherInfoBean;
import com.kcrason.highperformancefriendscircle.beans.PraiseBean;
import com.kcrason.highperformancefriendscircle.beans.UserBean;
import com.kcrason.highperformancefriendscircle.utils.SpanUtils;
import com.kcrason.highperformancefriendscircle.utils.TimerUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * @author KCrason
 * @date 2018/5/2
 */
public class DataCenter {

    public static void init() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                loadEmojis();
            }
        }).start();
    }

    public static final List<EmojiDataSource> emojiDataSources = new ArrayList<>();

    public static void loadEmojis() {
        for (int i = 0; i < 2; i++) {
            EmojiDataSource emojiDataSource = new EmojiDataSource();
            List<EmojiBean> typeEmojiBeans = new ArrayList<>();
            if (i == 0) {
                for (int j = 0; j < Constants.TYPE01_EMOJI_NAME.length; j++) {
                    EmojiBean emojiBean = new EmojiBean();
                    emojiBean.setEmojiName(Constants.TYPE01_EMOJI_NAME[j]);
                    emojiBean.setEmojiResource(Constants.TYPE01_EMOJI_DREWABLES[j]);
                    typeEmojiBeans.add(emojiBean);
                }
                emojiDataSource.setEmojiType(Constants.EmojiType.EMOJI_TYPE_01);
            } else {
                for (int j = 0; j < Constants.TYPE02_EMOJI_NAME.length; j++) {
                    EmojiBean emojiBean = new EmojiBean();
                    emojiBean.setEmojiName(Constants.TYPE02_EMOJI_NAME[j]);
                    emojiBean.setEmojiResource(Constants.TYPE02_EMOJI_DREWABLES[j]);
                    typeEmojiBeans.add(emojiBean);
                }
                emojiDataSource.setEmojiType(Constants.EmojiType.EMOJI_TYPE_02);
            }
            emojiDataSource.setEmojiList(typeEmojiBeans);
            emojiDataSources.add(emojiDataSource);
        }
    }


    public static List<FriendCircleBean> makeFriendCircleBeans(Context context) {
        List<FriendCircleBean> friendCircleBeans = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            FriendCircleBean friendCircleBean = new FriendCircleBean();
            int randomValue = (int) (Math.random() * 300);
            if (randomValue < 100) {
                friendCircleBean.setViewType(Constants.FriendCircleType.FRIEND_CIRCLE_TYPE_ONLY_WORD);
            } else if (randomValue < 200) {
                friendCircleBean.setViewType(Constants.FriendCircleType.FRIEND_CIRCLE_TYPE_WORD_AND_IMAGES);
            } else {
                friendCircleBean.setViewType(Constants.FriendCircleType.FRIEND_CIRCLE_TYPE_WORD_AND_URL);
            }
            friendCircleBean.setCommentBeans(makeCommentBeans(context));
            friendCircleBean.setImageUrls(makeImages());
            List<PraiseBean> praiseBeans = makePraiseBeans();
            friendCircleBean.setPraiseSpan(SpanUtils.makePraiseSpan(context, praiseBeans));
            friendCircleBean.setPraiseBeans(praiseBeans);
            friendCircleBean.setContent(Constants.CONTENT[(int) (Math.random() * 10)]);

            UserBean userBean = new UserBean();
            userBean.setUserName(Constants.USER_NAME[(int) (Math.random() * 30)]);
            userBean.setUserAvatarUrl(Constants.IMAGE_URL[(int) (Math.random() * 50)]);
            friendCircleBean.setUserBean(userBean);


            OtherInfoBean otherInfoBean = new OtherInfoBean();
            otherInfoBean.setTime(Constants.TIMES[(int) (Math.random() * 20)]);
            int random = (int) (Math.random() * 30);
            if (random < 20) {
                otherInfoBean.setSource(Constants.SOURCE[random]);
            } else {
                otherInfoBean.setSource("");
            }
            friendCircleBean.setOtherInfoBean(otherInfoBean);
            friendCircleBeans.add(friendCircleBean);
        }
        return friendCircleBeans;
    }

    public static  List<FriendCircleBean> convert2FriendCircleBeans(List<SnsInfo> snsInfos) {
        List<FriendCircleBean> friendCircleBeans = new ArrayList<>();
        for (SnsInfo sns : snsInfos) {
            FriendCircleBean friendCircleBean = new FriendCircleBean();
            friendCircleBean.setSnsId(sns.getSnsId());
            friendCircleBean.setContent(sns.getDescription());
            UserBean userBean = new UserBean();
            userBean.setUserName(sns.getUserName());
            friendCircleBean.setUserBean(userBean);

            List<CommentBean> commentBeans = new ArrayList<>();
            if (sns.getComments() != null) {
                for (SnsComment c : sns.getComments()) {
                    CommentBean commentBean = new CommentBean();
                    commentBean.setCommentType(TextUtils.isEmpty(c.getReply2()) ?
                            Constants.CommentType.COMMENT_TYPE_SINGLE : Constants.CommentType.COMMENT_TYPE_REPLY);
                    commentBean.setParentUserName(c.getReply2());
                    commentBean.setChildUserName(TextUtils.isEmpty(c.getNickName()) ? c.getWechatId() : c.getNickName());
                    commentBean.setId(c.getId());
                    commentBean.setSnsId(sns.getSnsId());
                    commentBean.setCommentContent(c.getContent());
                    commentBean.setComment(c);
                    commentBean.build(ApplicationContext.application());
                    commentBeans.add(commentBean);
                }
            }
            friendCircleBean.setCommentBeans(commentBeans);

            if (sns.getMedias() != null) {
                List<String> images = new ArrayList<>(sns.getMedias());
                friendCircleBean.setImageUrls(images);
            }


            if (sns.getLikes() != null) {
                List<PraiseBean> praiseBeans = new ArrayList<>();
                for (SnsLike like : sns.getLikes()) {
                    PraiseBean bean = new PraiseBean();
                    bean.setPraiseUserName(like.getNickName());
                    bean.setPraiseUserId(like.getWechatId());
                    praiseBeans.add(bean);
                }
                friendCircleBean.setPraiseBeans(praiseBeans);
                friendCircleBean.setPraiseSpan(SpanUtils.makePraiseSpan(ApplicationContext.application(), praiseBeans));
            }

            if (!TextUtils.isEmpty(sns.getShareTitle())) {
                friendCircleBean.setCardTitle(sns.getShareTitle());
            }

            friendCircleBean.setUrl(sns.getUrl());

            OtherInfoBean otherInfoBean = new OtherInfoBean();
            SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss");
            otherInfoBean.setTime(format.format(new Date(sns.getCreateTime() * 1000)));
            friendCircleBean.setOtherInfoBean(otherInfoBean);

            friendCircleBeans.add(friendCircleBean);
            friendCircleBean.setViewType(sns.getSnsType() == SnsInfo.CARD_TYPE ? Constants.FriendCircleType.FRIEND_CIRCLE_TYPE_WORD_AND_URL
            : (sns.getSnsType() == SnsInfo.IMAGE_TYPE || sns.getSnsType() == SnsInfo.VIDEO_TYPE) ? Constants.FriendCircleType.FRIEND_CIRCLE_TYPE_WORD_AND_IMAGES
            : Constants.FriendCircleType.FRIEND_CIRCLE_TYPE_ONLY_WORD);
        }
        return friendCircleBeans;
    }

    private static List<String> makeImages() {
        List<String> imageBeans = new ArrayList<>();
        int randomCount = (int) (Math.random() * 9);
        if (randomCount == 0) {
            randomCount = randomCount + 1;
        } else if (randomCount == 8) {
            randomCount = randomCount + 1;
        }
        for (int i = 0; i < randomCount; i++) {
            imageBeans.add(Constants.IMAGE_URL[(int) (Math.random() * 50)]);
        }
        return imageBeans;
    }


    private static List<PraiseBean> makePraiseBeans() {
        List<PraiseBean> praiseBeans = new ArrayList<>();
        int randomCount = (int) (Math.random() * 20);
        for (int i = 0; i < randomCount; i++) {
            PraiseBean praiseBean = new PraiseBean();
            praiseBean.setPraiseUserName(Constants.USER_NAME[(int) (Math.random() * 30)]);
            praiseBeans.add(praiseBean);
        }
        return praiseBeans;
    }


    private static List<CommentBean> makeCommentBeans(Context context) {
        List<CommentBean> commentBeans = new ArrayList<>();
        int randomCount = (int) (Math.random() * 20);
        for (int i = 0; i < randomCount; i++) {
            CommentBean commentBean = new CommentBean();
            if ((int) (Math.random() * 100) % 2 == 0) {
                commentBean.setCommentType(Constants.CommentType.COMMENT_TYPE_SINGLE);
                commentBean.setChildUserName(Constants.USER_NAME[(int) (Math.random() * 30)]);
            } else {
                commentBean.setCommentType(Constants.CommentType.COMMENT_TYPE_REPLY);
                commentBean.setChildUserName(Constants.USER_NAME[(int) (Math.random() * 30)]);
                commentBean.setParentUserName(Constants.USER_NAME[(int) (Math.random() * 30)]);
            }

            commentBean.setCommentContent(Constants.COMMENT_CONTENT[(int) (Math.random() * 30)]);
            commentBean.build(context);
            commentBeans.add(commentBean);
        }
        return commentBeans;
    }
}
