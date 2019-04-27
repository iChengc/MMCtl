package com.kcrason.highperformancefriendscircle;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.CircularProgressDrawable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.cc.core.command.Callback;
import com.cc.core.command.Command;
import com.cc.core.command.Messenger;
import com.cc.core.log.KLog;
import com.cc.core.utils.StrUtils;
import com.cc.core.wechat.model.sns.SnsComment;
import com.cc.core.wechat.model.sns.SnsCommentRequest;
import com.cc.core.wechat.model.user.Friend;
import com.cc.core.wechat.model.user.User;
import com.cc.wechatmanager.R;
import com.cc.wechatmanager.model.ContactsResult;
import com.cc.wechatmanager.model.LoginUserResult;
import com.cc.wechatmanager.model.SnsListResult;
import com.kcrason.highperformancefriendscircle.adapters.FriendCircleAdapter;
import com.kcrason.highperformancefriendscircle.beans.CommentBean;
import com.kcrason.highperformancefriendscircle.beans.FriendCircleBean;
import com.kcrason.highperformancefriendscircle.interfaces.OnPraiseOrCommentClickListener;
import com.kcrason.highperformancefriendscircle.interfaces.OnStartSwipeRefreshListener;
import com.kcrason.highperformancefriendscircle.others.DataCenter;
import com.kcrason.highperformancefriendscircle.others.FriendsCircleAdapterDivideLine;
import com.kcrason.highperformancefriendscircle.others.GlideSimpleTarget;
import com.kcrason.highperformancefriendscircle.utils.Utils;
import com.kcrason.highperformancefriendscircle.widgets.EmojiPanelView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import ch.ielse.view.imagewatcher.ImageWatcher;
import io.reactivex.disposables.Disposable;

public class TimelineActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener,
        OnPraiseOrCommentClickListener, ImageWatcher.OnPictureLongPressListener, ImageWatcher.Loader {

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private Disposable mDisposable;
    private FriendCircleAdapter mFriendCircleAdapter;
    private ImageWatcher mImageWatcher;
    private EmojiPanelView mEmojiPanelView;
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);
        mEmojiPanelView = findViewById(R.id.emoji_panel_view);
        mEmojiPanelView.initEmojiPanel(DataCenter.emojiDataSources);
        mEmojiPanelView.setClickSendListener(new EmojiPanelView.OnClickSendBtnListener() {
            @Override
            public void onClickSendBtn(int adapterPosition, SnsComment reply, String comment) {
                FriendCircleBean bean = mFriendCircleAdapter.getItem(adapterPosition);
                if (bean == null || loginUser == null) {
                    return;
                }

                SnsCommentRequest request = new SnsCommentRequest();
                request.setContent(comment);
                request.setSnsId(bean.getSnsId());
                if (reply != null && !reply.getWechatId().equals(loginUser.getWechatId())) {
                    request.setComment(reply);
                }

                commentSns(request);
            }
        });
        mSwipeRefreshLayout = findViewById(R.id.swpie_refresh_layout);
        recyclerView = findViewById(R.id.recycler_view);
        mSwipeRefreshLayout.setOnRefreshListener(this);

//        findViewById(R.id.img_back).setOnClickListener(v ->
//                startActivity(new Intent(TimelineActivity.this, EmojiPanelActivity.class)));

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (isDestroyed()) {
                    return;
                }
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    Glide.with(TimelineActivity.this).resumeRequests();
                } else {
                    Glide.with(TimelineActivity.this).pauseRequests();
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });
        mImageWatcher = findViewById(R.id.image_watcher);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new FriendsCircleAdapterDivideLine());
        mFriendCircleAdapter = new FriendCircleAdapter(this, recyclerView, mImageWatcher);
        recyclerView.setAdapter(mFriendCircleAdapter);
        mImageWatcher.setTranslucentStatus(Utils.calcStatusBarHeight(this));
        mImageWatcher.setErrorImageRes(R.mipmap.error_picture);
        mImageWatcher.setOnPictureLongPressListener(this);
        mImageWatcher.setLoader(this);
        Utils.showSwipeRefreshLayout(mSwipeRefreshLayout, new OnStartSwipeRefreshListener() {
            @Override
            public void onStartRefresh() {
                asyncMakeData();
            }
        });
    }


    private void asyncMakeData() {
        getLoginUser();
        getContactList();
        getSnsList("0");
        /*mDisposable = Single.create(new SingleOnSubscribe<List<FriendCircleBean>>() {
            @Override
            public void subscribe(SingleEmitter<List<FriendCircleBean>> emitter) throws Exception {
                emitter.onSuccess(DataCenter.makeFriendCircleBeans(TimelineActivity.this));
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<FriendCircleBean>>() {
                    @Override
                    public void accept(List<FriendCircleBean> friendCircleBeans) throws Exception {
                        Utils.hideSwipeRefreshLayout(mSwipeRefreshLayout);
                        if (friendCircleBeans != null) {
                            mFriendCircleAdapter.setFriendCircleBeans(friendCircleBeans);
                        }
                    }
                });*/
        /*mDisposable = Single.create((SingleOnSubscribe<List<FriendCircleBean>>) emitter ->
                emitter.onSuccess(DataCenter.makeFriendCircleBeans(this)))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe((friendCircleBeans, throwable) -> {
                    Utils.hideSwipeRefreshLayout(mSwipeRefreshLayout);
                    if (friendCircleBeans != null && throwable == null) {
                        mFriendCircleAdapter.setFriendCircleBeans(friendCircleBeans);
                    } \"-5405625322811682704\",\n    417\n  ]\n}",
      "id": "d0c984c0-d2e1-4393-b28d-599e351a26ec",
                });*/
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSwipeRefreshLayout.removeCallbacks(getSnsRunnable);
        if (mDisposable != null && !mDisposable.isDisposed()) {
            mDisposable.dispose();
        }
    }

    @Override
    public void onRefresh() {
        recyclerView.requestLayout();
        asyncMakeData();
    }

    @Override
    public void onPraiseClick(int position) {

        FriendCircleBean bean = mFriendCircleAdapter.getItem(position);
        if (bean == null || loginUser == null) {
            return;
        }
        boolean isLike = bean.isLike(loginUser.getWechatId());
        if (isLike) {
            bean.unpraised(loginUser.getWechatId());
        } else {
            bean.praised(loginUser.getWechatId(), loginUser.getNickname());
        }
        likeSns(bean, !isLike);
        mFriendCircleAdapter.notifyDataSetChanged();
        Toast.makeText(this, "You Click Praise!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCommentClick(int position, SnsComment comment) {
//        Toast.makeText(this, "you click comment", Toast.LENGTH_SHORT).show();
        mEmojiPanelView.showEmojiPanel(position, comment);
    }

    @Override
    public void onBackPressed() {
        if (!mImageWatcher.handleBackPressed()) {
            super.onBackPressed();
        }
    }

    @Override
    public void onPictureLongPress(ImageView v, String url, int pos) {

    }


    @Override
    public void load(Context context, String url, ImageWatcher.LoadCallback lc) {
        Glide.with(context).asBitmap().load(url).into(new GlideSimpleTarget(lc));
    }

    private void getSnsList(final String id) {

        Messenger.Companion.sendCommand(genCommand("getSnsList", id), new Callback() {
            @Override
            public void onResult(@Nullable String result) {
                KLog.e("TimelineActivity", result);
                mSwipeRefreshLayout.post(new Runnable() {
                    @Override
                    public void run() {
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                });
                if (progressDialog != null) {
                    progressDialog.dismiss();
                    progressDialog = null;
                }
                final SnsListResult result1 = StrUtils.fromJson(result, SnsListResult.class);
                if (result1 == null) {
                    return;
                }
                if (!result1.isSuccess()) {
                    KLog.e("can not get sns list, " + result1.getMessage());
                } else {
                    if (result1.getData() == null || result1.getData().isEmpty()) {
                        return;
                    }
                    mSwipeRefreshLayout.post(new Runnable() {
                        @Override
                        public void run() {
                            if ("0".equals(id)) {
                                mFriendCircleAdapter.setFriendCircleBeans(DataCenter.convert2FriendCircleBeans(result1.getData()));
                            } else {
                                mFriendCircleAdapter.addFriendCircleBeans(DataCenter.convert2FriendCircleBeans(result1.getData()));
                            }
                        }
                    });

                    getSnsRunnable.result1 = result1;
                    mSwipeRefreshLayout.postDelayed(getSnsRunnable, 2000);
                }
            }
        });
    }

    public static Command genCommand(String key, Object... data) {
        Command c = new Command();
        c.setKey(key);
        c.setId(UUID.randomUUID().toString());
        List<Object> args = new ArrayList<>(Arrays.asList(data));
        c.setArgs(args);
        return c;
    }


/*
    private void getLoginUser() {
        Messenger.Companion.sendCommand(genCommand("getLoginUserInfo"), new Callback() {
            @Override
            public void onResult(@Nullable String result) {
                final LoginUserResult result1 = StrUtils.fromJson(result, LoginUserResult.class);
                if (result1 == null) {
                    return;
                }
                if (!result1.isSuccess()) {
                    com.cc.core.utils.Utils.showToast("无法获取用户信息");
                    KLog.e("can not get login user, " + result1.getMessage());
                } else {
                    mSwipeRefreshLayout.post(new Runnable() {
                        @Override
                        public void run() {

                            listView.updateUserInfo(result1.getData());
                        }
                    });
                }
            }
        });
    }*/

    private void getContactList() {
        Messenger.Companion.sendCommand(genCommand("getContacts"), new Callback() {
            @Override
            public void onResult(@Nullable String result) {
                final ContactsResult result1 = StrUtils.fromJson(result, ContactsResult.class);
                if (result1 == null) {
                    KLog.e("can not get contact list, " + result1.getMessage());
                    return;
                }
                KLog.e("get contact result:" + result);
                if (!result1.isSuccess()) {
                    KLog.e("can not get contact list, " + result1.getMessage());
                } else {
                    mSwipeRefreshLayout.post(new Runnable() {
                        @Override
                        public void run() {
                            List<Friend> contact = result1.getData();
                            mFriendCircleAdapter.setFrinds(contact);
                        }
                    });
                }
            }
        });
    }

    private GetSnsRunnable getSnsRunnable = new GetSnsRunnable();

    class GetSnsRunnable implements  Runnable {
        SnsListResult result1;
        @Override
        public void run() {
            String id = result1.getData().get(result1.getData().size() - 1).getSnsId();
            getSnsList(id);
        }
    }

    private void getLoginUser() {
        Messenger.Companion.sendCommand(genCommand("getLoginUserInfo"), new Callback() {
            @Override
            public void onResult(@Nullable String result) {
                final LoginUserResult result1 = StrUtils.fromJson(result, LoginUserResult.class);
                if (result1 == null) {
                    KLog.e("can not get contact list, " + result1.getMessage());
                    return;
                }
                KLog.e("get contact result:" + result);
                if (!result1.isSuccess()) {
                    KLog.e("can not get contact list, " + result1.getMessage());
                } else {
                    mSwipeRefreshLayout.post(new Runnable() {
                        @Override
                        public void run() {
                            loginUser = result1.getData();
                            mFriendCircleAdapter.setLoginUser(result1.getData());
                        }
                    });
                }
            }
        });
    }

    private void likeSns(FriendCircleBean sns, final boolean isLike) {
        Messenger.Companion.sendCommand(genCommand(isLike ? "snsLike" : "snsLikeCancel", sns.getSnsId()), new Callback() {
            @Override
            public void onResult(@Nullable String result) {
                refreshSnsList(isLike ? "正在点赞" : "正在取消点赞");
            }
        });
    }

    private void commentSns( SnsCommentRequest comment) {
        Messenger.Companion.sendCommand(genCommand("snsComment", comment), new Callback() {
            @Override
            public void onResult(@Nullable String result) {
                refreshSnsList("正在发送评论");
            }
        });
    }

    public void cancelCommentSns( CommentBean comment) {
        if (!TextUtils.isEmpty(comment.getParentWechatId()) && !comment.getParentWechatId().equals(loginUser.getWechatId())) {
            Toast.makeText(this, "只能删除自己的评论！", Toast.LENGTH_SHORT).show();
            return;
        } else if (TextUtils.isEmpty(comment.getParentWechatId()) && !comment.getChildWechatId().equals(loginUser.getWechatId())) {
            Toast.makeText(this, "只能删除自己的评论！", Toast.LENGTH_SHORT).show();
            return;
        }

        SnsCommentRequest request = new SnsCommentRequest();
        request.setSnsId(comment.getSnsId());
        request.setComment(comment.getComment());
        Messenger.Companion.sendCommand(genCommand("snsCommentCancel", request), new Callback() {
            @Override
            public void onResult(@Nullable String result) {
                KLog.e("gggggg", result);
                refreshSnsList("正在删除评论");
            }
        });
    }

    void refreshSnsList(final String message) {
        mSwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                showProgressDialog(message);
            }
        });
        mSwipeRefreshLayout.removeCallbacks(getSnsRunnable);
        getSnsList("0");
    }

    private void showProgressDialog(String message) {

       // mSwipeRefreshLayout.setRefreshing(true);
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(message);
        progressDialog.show();
    }

    private User loginUser;
    private ProgressDialog progressDialog;
}
