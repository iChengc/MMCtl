package com.kcrason.highperformancefriendscircle.adapters;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableStringBuilder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.cc.core.utils.StrUtils;
import com.cc.core.wechat.model.user.Friend;
import com.cc.core.wechat.model.user.User;
import com.cc.wechatmanager.R;
import com.cc.wechatmanager.model.LoginUserResult;
import com.kcrason.highperformancefriendscircle.Constants;
import com.kcrason.highperformancefriendscircle.beans.PraiseBean;
import com.kcrason.highperformancefriendscircle.enums.TranslationState;
import com.kcrason.highperformancefriendscircle.interfaces.OnItemClickPopupMenuListener;
import com.kcrason.highperformancefriendscircle.interfaces.OnPraiseOrCommentClickListener;
import com.kcrason.highperformancefriendscircle.interfaces.OnTimerResultListener;
import com.kcrason.highperformancefriendscircle.span.TextMovementMethod;
import com.kcrason.highperformancefriendscircle.utils.TimerUtils;
import com.kcrason.highperformancefriendscircle.utils.Utils;
import com.kcrason.highperformancefriendscircle.widgets.CommentOrPraisePopupWindow;
import com.kcrason.highperformancefriendscircle.widgets.NineGridView;
import com.kcrason.highperformancefriendscircle.beans.FriendCircleBean;
import com.kcrason.highperformancefriendscircle.beans.OtherInfoBean;
import com.kcrason.highperformancefriendscircle.beans.UserBean;
import com.kcrason.highperformancefriendscircle.widgets.VerticalCommentWidget;

import java.util.ArrayList;
import java.util.List;

import ch.ielse.view.imagewatcher.ImageWatcher;

/**
 * @author KCrason
 * @date 2018/4/27
 */
public class FriendCircleAdapter extends RecyclerView.Adapter<FriendCircleAdapter.BaseFriendCircleViewHolder>
        implements OnItemClickPopupMenuListener {

    private Context mContext;

    private LayoutInflater mLayoutInflater;

    private List<FriendCircleBean> mFriendCircleBeans = new ArrayList<>();

    private RequestOptions mRequestOptions;

    private int mAvatarSize;

    private DrawableTransitionOptions mDrawableTransitionOptions;

    private CommentOrPraisePopupWindow mCommentOrPraisePopupWindow;

    private OnPraiseOrCommentClickListener mOnPraiseOrCommentClickListener;

    private LinearLayoutManager mLayoutManager;

    private RecyclerView mRecyclerView;

    private ImageWatcher mImageWatcher;

    private List<Friend> frinds;

    private User loginUser;

    public FriendCircleAdapter(Context context, RecyclerView recyclerView, ImageWatcher imageWatcher) {
        this.mContext = context;
        this.mImageWatcher = imageWatcher;
        mRecyclerView = recyclerView;
        this.mLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        this.mAvatarSize = Utils.dp2px(44f);
        this.mLayoutInflater = LayoutInflater.from(context);
        this.mRequestOptions = new RequestOptions().centerCrop();
        this.mDrawableTransitionOptions = DrawableTransitionOptions.withCrossFade();
        if (context instanceof OnPraiseOrCommentClickListener) {
            this.mOnPraiseOrCommentClickListener = (OnPraiseOrCommentClickListener) context;
        }
    }

    public void setFrinds(List<Friend> frinds) {
        if (frinds != null) {
            this.frinds = frinds;
        }
    }

    public void setLoginUser(User user) {
        loginUser = user;
    }

    public void setFriendCircleBeans(List<FriendCircleBean> friendCircleBeans) {
        this.mFriendCircleBeans = friendCircleBeans;
        notifyDataSetChanged();
    }

    public void addFriendCircleBeans(List<FriendCircleBean> friendCircleBeans) {
        if (friendCircleBeans != null) {
            if (mFriendCircleBeans == null) {
                mFriendCircleBeans = new ArrayList<>();
            }
            this.mFriendCircleBeans.addAll(friendCircleBeans);
            notifyItemRangeInserted(mFriendCircleBeans.size(), friendCircleBeans.size());
        }
    }

    @Override
    public BaseFriendCircleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == Constants.FriendCircleType.FRIEND_CIRCLE_TYPE_ONLY_WORD) {
            return new OnlyWordViewHolder(mLayoutInflater.inflate(R.layout.item_recycler_firend_circle_only_word, parent, false));
        } else if (viewType == Constants.FriendCircleType.FRIEND_CIRCLE_TYPE_WORD_AND_URL) {
            return new WordAndUrlViewHolder(mLayoutInflater.inflate(R.layout.item_recycler_firend_circle_word_and_url, parent, false));
        } else if (viewType == Constants.FriendCircleType.FRIEND_CIRCLE_TYPE_WORD_AND_IMAGES) {
            return new WordAndImagesViewHolder(mLayoutInflater.inflate(R.layout.item_recycler_firend_circle_word_and_images, parent, false));
        }
        return null;
    }

    @Override
    public void onBindViewHolder(BaseFriendCircleViewHolder holder, int position) {
        if (holder != null && mFriendCircleBeans != null && position < mFriendCircleBeans.size()) {
            final FriendCircleBean friendCircleBean = mFriendCircleBeans.get(position);
            makeUserBaseData(holder, friendCircleBean, position);
            if (holder instanceof OnlyWordViewHolder) {
                OnlyWordViewHolder onlyWordViewHolder = (OnlyWordViewHolder) holder;
            } else if (holder instanceof WordAndUrlViewHolder) {
                WordAndUrlViewHolder wordAndUrlViewHolder = (WordAndUrlViewHolder) holder;
                 wordAndUrlViewHolder.layoutUrl.setOnClickListener(new View.OnClickListener() {
                     @Override
                     public void onClick(View v) {
                         Toast.makeText(mContext, "Url:" + friendCircleBean.getUrl(), Toast.LENGTH_SHORT).show();
                     }
                 });
                 wordAndUrlViewHolder.cartTitle.setText(friendCircleBean.getCardTitle());
                 if (friendCircleBean.getImageUrls() != null && !friendCircleBean.getImageUrls().isEmpty()) {
                     Glide.with(wordAndUrlViewHolder.cardThumb.getContext()).load(friendCircleBean.getImageUrls()
                             .get(0)).into(wordAndUrlViewHolder.cardThumb);
                 }
            } else if (holder instanceof WordAndImagesViewHolder) {
                final WordAndImagesViewHolder wordAndImagesViewHolder = (WordAndImagesViewHolder) holder;
                wordAndImagesViewHolder.nineGridView.setOnImageClickListener(new NineGridView.OnImageClickListener() {
                    @Override
                    public void onImageClick(int position, View view) {
                        mImageWatcher.show((ImageView) view, wordAndImagesViewHolder.nineGridView.getImageViews(),
                                friendCircleBean.getImageUrls());
                    }
                });
                wordAndImagesViewHolder.nineGridView.setAdapter(new NineImageAdapter(mContext, mRequestOptions,
                        mDrawableTransitionOptions, friendCircleBean.getImageUrls()));
            }
        }
    }


    private void makeUserBaseData(BaseFriendCircleViewHolder holder, final FriendCircleBean friendCircleBean, final int position) {
        holder.txtContent.setText(friendCircleBean.getContentSpan());
        setContentShowState(holder, friendCircleBean);

        holder.txtContent.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                TranslationState translationState = friendCircleBean.getTranslationState();
                if (translationState == TranslationState.END) {
                    Utils.showPopupMenu(mContext, FriendCircleAdapter.this, position, v, TranslationState.END);
                } else {
                    Utils.showPopupMenu(mContext, FriendCircleAdapter.this, position, v, TranslationState.START);
                }
                return true;
            }
        });

        updateTargetItemContent(position, holder, friendCircleBean.getTranslationState(),
                friendCircleBean.getContentSpan(), false);

        UserBean userBean = friendCircleBean.getUserBean();
        if (userBean != null) {
            if (loginUser != null && loginUser.getWechatId().equals(userBean.getUserName())) {

                holder.txtUserName.setText(loginUser.getNickname());
                Glide.with(mContext).load(loginUser.getAvatar())
                        .apply(mRequestOptions.override(mAvatarSize, mAvatarSize))
                        .transition(mDrawableTransitionOptions)
                        .into(holder.imgAvatar);
            } else {
                Friend f = getFriendById(userBean.getUserName());
                if (f != null) {
                    holder.txtUserName.setText(f.getNickname());
                    Glide.with(mContext).load(f.getAvatar())
                            .apply(mRequestOptions.override(mAvatarSize, mAvatarSize))
                            .transition(mDrawableTransitionOptions)
                            .into(holder.imgAvatar);
                } else {
                    holder.imgAvatar.setImageResource(R.drawable.avatar_icon);
                    holder.txtUserName.setText(userBean.getUserName());
                }
            }
        }

        OtherInfoBean otherInfoBean = friendCircleBean.getOtherInfoBean();

        if (otherInfoBean != null) {
            holder.txtSource.setText(otherInfoBean.getSource());
            holder.txtPublishTime.setText(otherInfoBean.getTime());
        }

        if (friendCircleBean.isShowPraise() || friendCircleBean.isShowComment()) {
            holder.layoutPraiseAndComment.setVisibility(View.VISIBLE);
            if (friendCircleBean.isShowComment() && friendCircleBean.isShowPraise()) {
                holder.viewLine.setVisibility(View.VISIBLE);
            } else {
                holder.viewLine.setVisibility(View.GONE);
            }
            if (friendCircleBean.isShowPraise()) {
                holder.txtPraiseContent.setVisibility(View.VISIBLE);
                holder.txtPraiseContent.setText(friendCircleBean.getPraiseSpan());
            } else {
                holder.txtPraiseContent.setVisibility(View.GONE);
            }
            if (friendCircleBean.isShowComment()) {
                holder.verticalCommentWidget.setVisibility(View.VISIBLE);
                holder.verticalCommentWidget.addComments(friendCircleBean.getCommentBeans(), false);
            } else {
                holder.verticalCommentWidget.setVisibility(View.GONE);
            }
        } else {
            holder.layoutPraiseAndComment.setVisibility(View.GONE);
        }

        holder.imgPraiseOrComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mContext instanceof Activity) {
                    if (mCommentOrPraisePopupWindow == null) {
                        mCommentOrPraisePopupWindow = new CommentOrPraisePopupWindow(mContext);
                    }
                    mCommentOrPraisePopupWindow
                            .setOnPraiseOrCommentClickListener(mOnPraiseOrCommentClickListener)
                            .setCurrentPosition(position);
                    if (mCommentOrPraisePopupWindow.isShowing()) {
                        mCommentOrPraisePopupWindow.dismiss();
                    } else {
                        mCommentOrPraisePopupWindow.showPopupWindow(v);
                    }
                }
            }
        });

        //holder.txtLocation.setOnClickListener(v -> Toast.makeText(mContext, "You Click Location", Toast.LENGTH_SHORT).show());
    }

    private Friend getFriendById(String id) {
        if (frinds == null) {
            return null;
        }

        for (Friend f : frinds) {
            if (StrUtils.stringNotNull(id).equals(f.getWechatId())) {
                return f;
            }
        }

        return null;
    }

    private void setContentShowState(final BaseFriendCircleViewHolder holder, final FriendCircleBean friendCircleBean) {
        if (friendCircleBean.isShowCheckAll()) {
            holder.txtState.setVisibility(View.VISIBLE);
            setTextState(holder, friendCircleBean.isExpanded());
            holder.txtState.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (friendCircleBean.isExpanded()) {
                        friendCircleBean.setExpanded(false);
                    } else {
                        friendCircleBean.setExpanded(true);
                    }
                    setTextState(holder, friendCircleBean.isExpanded());
                }
            });
        } else {
            holder.txtState.setVisibility(View.GONE);
            holder.txtContent.setMaxLines(Integer.MAX_VALUE);
        }
    }

    private void setTextState(BaseFriendCircleViewHolder holder, boolean isExpand) {
        if (isExpand) {
            holder.txtContent.setMaxLines(Integer.MAX_VALUE);
            holder.txtState.setText("收起");
        } else {
            holder.txtContent.setMaxLines(4);
            holder.txtState.setText("全文");
        }
    }

    @Override
    public int getItemViewType(int position) {
        return mFriendCircleBeans.get(position).getViewType();
    }

    @Override
    public int getItemCount() {
        return mFriendCircleBeans == null ? 0 : mFriendCircleBeans.size();
    }


    @Override
    public void onItemClickCopy(int position) {
        Toast.makeText(mContext, "已复制", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onItemClickTranslation(final int position) {
        if (mFriendCircleBeans != null && position < mFriendCircleBeans.size()) {
            mFriendCircleBeans.get(position).setTranslationState(TranslationState.CENTER);
            notifyTargetItemView(position, TranslationState.CENTER, null);
            TimerUtils.timerTranslation(new OnTimerResultListener() {
                @Override
                public void onTimerResult() {
                    if (mFriendCircleBeans != null && position < mFriendCircleBeans.size()) {
                        mFriendCircleBeans.get(position).setTranslationState(TranslationState.END);
                        notifyTargetItemView(position, TranslationState.END, mFriendCircleBeans.get(position).getContentSpan());
                    }
                }
            });
        }
    }

    @Override
    public void onItemClickHideTranslation(int position) {
        if (mFriendCircleBeans != null && position < mFriendCircleBeans.size()) {
            mFriendCircleBeans.get(position).setTranslationState(TranslationState.START);
            notifyTargetItemView(position, TranslationState.START, null);
        }
    }


    private void updateTargetItemContent(final int position, BaseFriendCircleViewHolder baseFriendCircleViewHolder,
                                         TranslationState translationState, SpannableStringBuilder translationResult, boolean isStartAnimation) {
        if (translationState == TranslationState.START) {
            baseFriendCircleViewHolder.layoutTranslation.setVisibility(View.GONE);
        } else if (translationState == TranslationState.CENTER) {
            baseFriendCircleViewHolder.layoutTranslation.setVisibility(View.VISIBLE);
            baseFriendCircleViewHolder.divideLine.setVisibility(View.GONE);
            baseFriendCircleViewHolder.translationTag.setVisibility(View.VISIBLE);
            baseFriendCircleViewHolder.translationDesc.setText(R.string.translating);
            baseFriendCircleViewHolder.txtTranslationContent.setVisibility(View.GONE);
            Utils.startAlphaAnimation(baseFriendCircleViewHolder.translationDesc, isStartAnimation);
        } else {
            baseFriendCircleViewHolder.layoutTranslation.setVisibility(View.VISIBLE);
            baseFriendCircleViewHolder.divideLine.setVisibility(View.VISIBLE);
            baseFriendCircleViewHolder.translationTag.setVisibility(View.GONE);
            baseFriendCircleViewHolder.translationDesc.setText(R.string.translated);
            baseFriendCircleViewHolder.txtTranslationContent.setVisibility(View.VISIBLE);
            baseFriendCircleViewHolder.txtTranslationContent.setText(translationResult);
            Utils.startAlphaAnimation(baseFriendCircleViewHolder.txtTranslationContent, isStartAnimation);
            baseFriendCircleViewHolder.txtTranslationContent.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    Utils.showPopupMenu(mContext, FriendCircleAdapter.this, position, v, TranslationState.END);
                    return true;
                }
            });
        }
    }


    private void notifyTargetItemView(int position, TranslationState translationState, SpannableStringBuilder translationResult) {
        View childView = mLayoutManager.findViewByPosition(position);
        if (childView != null) {
            RecyclerView.ViewHolder viewHolder = mRecyclerView.getChildViewHolder(childView);
            if (viewHolder instanceof BaseFriendCircleViewHolder) {
                BaseFriendCircleViewHolder baseFriendCircleViewHolder = (BaseFriendCircleViewHolder) viewHolder;
                updateTargetItemContent(position, baseFriendCircleViewHolder,
                        translationState, translationResult, true);
            }
        }
    }


    @Override
    public void onItemClickCollection(int position) {
        Toast.makeText(mContext, "已收藏", Toast.LENGTH_SHORT).show();
    }


    static class WordAndImagesViewHolder extends BaseFriendCircleViewHolder {

        NineGridView nineGridView;

        public WordAndImagesViewHolder(View itemView) {
            super(itemView);
            nineGridView = itemView.findViewById(R.id.nine_grid_view);
        }
    }


    static class WordAndUrlViewHolder extends BaseFriendCircleViewHolder {

        LinearLayout layoutUrl;
        TextView cartTitle;
        ImageView cardThumb;
        public WordAndUrlViewHolder(View itemView) {
            super(itemView);
            layoutUrl = itemView.findViewById(R.id.layout_url);
            cardThumb = itemView.findViewById(R.id.card_thumb);
            cartTitle = itemView.findViewById(R.id.card_title);
        }
    }

    static class OnlyWordViewHolder extends BaseFriendCircleViewHolder {

        public OnlyWordViewHolder(View itemView) {
            super(itemView);
        }
    }

    static class BaseFriendCircleViewHolder extends RecyclerView.ViewHolder {

        public VerticalCommentWidget verticalCommentWidget;
        public TextView txtUserName;
        public View viewLine;
        public TextView txtPraiseContent;
        public ImageView imgAvatar;
        public TextView txtSource;
        public TextView txtPublishTime;
        public ImageView imgPraiseOrComment;
        public TextView txtLocation;
        public TextView txtContent;
        public TextView txtState;
        public LinearLayout layoutTranslation;
        public TextView txtTranslationContent;
        public View divideLine;
        public ImageView translationTag;
        public TextView translationDesc;
        public LinearLayout layoutPraiseAndComment;

        public BaseFriendCircleViewHolder(View itemView) {
            super(itemView);
            verticalCommentWidget = itemView.findViewById(R.id.vertical_comment_widget);
            txtUserName = itemView.findViewById(R.id.txt_user_name);
            txtPraiseContent = itemView.findViewById(R.id.praise_content);
            viewLine = itemView.findViewById(R.id.view_line);
            imgAvatar = itemView.findViewById(R.id.img_avatar);
            txtSource = itemView.findViewById(R.id.txt_source);
            txtPublishTime = itemView.findViewById(R.id.txt_publish_time);
            imgPraiseOrComment = itemView.findViewById(R.id.img_click_praise_or_comment);
            txtLocation = itemView.findViewById(R.id.txt_location);
            txtContent = itemView.findViewById(R.id.txt_content);
            txtState = itemView.findViewById(R.id.txt_state);
            txtTranslationContent = itemView.findViewById(R.id.txt_translation_content);
            layoutTranslation = itemView.findViewById(R.id.layout_translation);
            layoutPraiseAndComment = itemView.findViewById(R.id.layout_praise_and_comment);
            divideLine = itemView.findViewById(R.id.view_divide_line);
            translationTag = itemView.findViewById(R.id.img_translating);
            translationDesc = itemView.findViewById(R.id.txt_translation_desc);
            txtPraiseContent.setMovementMethod(new TextMovementMethod());
        }
    }

    public FriendCircleBean getItem(int position) {
        if (mFriendCircleBeans == null || position >= mFriendCircleBeans.size()) {
            return null;
        }

        return mFriendCircleBeans.get(position);
    }
}
