package com.kcrason.highperformancefriendscircle.beans;

import android.text.SpannableStringBuilder;

import com.kcrason.highperformancefriendscircle.utils.Utils;
import com.kcrason.highperformancefriendscircle.enums.TranslationState;

import java.util.ArrayList;
import java.util.List;

public class FriendCircleBean {
    private String snsId;

    private int viewType;

    private String content;

    private List<CommentBean> commentBeans;

    private List<PraiseBean> praiseBeans;

    private List<String> imageUrls;

    private UserBean userBean;

    private OtherInfoBean otherInfoBean;

    private boolean isShowPraise;

    private boolean isExpanded;

    private String url;

    private String thumnUrl;

    private String cardTitle;

    public boolean isExpanded() {
        return isExpanded;
    }

    public void setExpanded(boolean expanded) {
        isExpanded = expanded;
    }

    private boolean isShowComment;

    private boolean isShowCheckAll;

    public boolean isShowCheckAll() {
        return isShowCheckAll;
    }

    public void setShowCheckAll(boolean showCheckAll) {
        isShowCheckAll = showCheckAll;
    }

    private TranslationState translationState = TranslationState.START;

    public void setTranslationState(TranslationState translationState) {
        this.translationState = translationState;
    }

    public TranslationState getTranslationState() {
        return translationState;
    }

    public boolean isShowComment() {
        return isShowComment;
    }

    public boolean isShowPraise() {
        return isShowPraise;
    }

    public OtherInfoBean getOtherInfoBean() {
        return otherInfoBean;
    }

    public void setOtherInfoBean(OtherInfoBean otherInfoBean) {
        this.otherInfoBean = otherInfoBean;
    }

    public void setUserBean(UserBean userBean) {
        this.userBean = userBean;
    }

    public UserBean getUserBean() {
        return userBean;
    }

    public int getViewType() {
        return viewType;
    }

    public void setViewType(int viewType) {
        this.viewType = viewType;
    }

    public String getContent() {
        return content;
    }

    public SpannableStringBuilder getContentSpan() {
        return contentSpan;
    }

    public void setContentSpan(SpannableStringBuilder contentSpan) {
        this.contentSpan = contentSpan;
        this.isShowCheckAll = Utils.calculateShowCheckAllText(contentSpan.toString());
    }

    private SpannableStringBuilder contentSpan;


    public void setContent(String content) {
        this.content = content;
        setContentSpan(new SpannableStringBuilder(content));
    }

    public List<CommentBean> getCommentBeans() {
        return commentBeans;
    }

    public void setCommentBeans(List<CommentBean> commentBeans) {
        isShowComment = commentBeans != null && commentBeans.size() > 0;
        this.commentBeans = commentBeans;
    }

    public List<PraiseBean> getPraiseBeans() {
        return praiseBeans;
    }

    public void setPraiseBeans(List<PraiseBean> praiseBeans) {
        isShowPraise = praiseBeans != null && praiseBeans.size() > 0;
        this.praiseBeans = praiseBeans;
    }

    public void praised(String wechatId, String userName) {
        if (praiseBeans == null) {
            praiseBeans=new ArrayList<>();
        }

        PraiseBean p = new PraiseBean();
        p.setPraiseUserId(wechatId);
        p.setPraiseUserName(userName);
        praiseBeans.add(p);
    }

    public void unpraised(String wechatId) {
        if (praiseBeans == null) {
            return;
        }

        for (PraiseBean p : praiseBeans) {
            if (wechatId.equals(p.getPraiseUserId())) {
                praiseBeans.remove(p);
                return;
            }
        }
    }

    public boolean isLike(String wechatId) {
        if (praiseBeans == null) return false;
        for (PraiseBean p : praiseBeans) {
            if (p.getPraiseUserId().equals(wechatId)) {
                return true;
            }
        }

        return false;
    }

    public List<String> getImageUrls() {
        return imageUrls;
    }

    public void setImageUrls(List<String> imageUrls) {
        this.imageUrls = imageUrls;
    }


    public void setPraiseSpan(SpannableStringBuilder praiseSpan) {
        this.praiseSpan = praiseSpan;
    }

    public SpannableStringBuilder getPraiseSpan() {
        return praiseSpan;
    }

    private SpannableStringBuilder praiseSpan;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getThumnUrl() {
        return thumnUrl;
    }

    public void setThumnUrl(String thumnUrl) {
        this.thumnUrl = thumnUrl;
    }

    public String getCardTitle() {
        return cardTitle;
    }

    public void setCardTitle(String cardTitle) {
        this.cardTitle = cardTitle;
    }

    public String getSnsId() {
        return snsId;
    }

    public void setSnsId(String snsId) {
        this.snsId = snsId;
    }
}
