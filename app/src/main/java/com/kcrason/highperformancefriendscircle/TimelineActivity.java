package com.kcrason.highperformancefriendscircle;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.cc.core.command.Callback;
import com.cc.core.command.Command;
import com.cc.core.command.Messenger;
import com.cc.core.log.KLog;
import com.cc.core.utils.StrUtils;
import com.cc.wechatmanager.R;
import com.cc.wechatmanager.model.ContactsResult;
import com.cc.wechatmanager.model.LoginUserResult;
import com.cc.wechatmanager.model.SnsListResult;
import com.kcrason.highperformancefriendscircle.adapters.FriendCircleAdapter;
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
import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class TimelineActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener,
        OnPraiseOrCommentClickListener, ImageWatcher.OnPictureLongPressListener, ImageWatcher.Loader {

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private Disposable mDisposable;
    private FriendCircleAdapter mFriendCircleAdapter;
    private ImageWatcher mImageWatcher;
    private EmojiPanelView mEmojiPanelView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);
        mEmojiPanelView = findViewById(R.id.emoji_panel_view);
        mEmojiPanelView.initEmojiPanel(DataCenter.emojiDataSources);
        mSwipeRefreshLayout = findViewById(R.id.swpie_refresh_layout);
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        mSwipeRefreshLayout.setOnRefreshListener(this);

//        findViewById(R.id.img_back).setOnClickListener(v ->
//                startActivity(new Intent(TimelineActivity.this, EmojiPanelActivity.class)));

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
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
        getContactList();
        getSnsList();
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
                    }
                });*/
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mDisposable != null && !mDisposable.isDisposed()) {
            mDisposable.dispose();
        }
    }

    @Override
    public void onRefresh() {
        asyncMakeData();
    }

    @Override
    public void onPraiseClick(int position) {
        Toast.makeText(this, "You Click Praise!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCommentClick(int position) {
//        Toast.makeText(this, "you click comment", Toast.LENGTH_SHORT).show();
        mEmojiPanelView.showEmojiPanel();
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

    private void getSnsList() {

        Messenger.Companion.sendCommand(genCommand("getSnsList", 0), new Callback() {
            @Override
            public void onResult(@Nullable String result) {
                KLog.e("TimelineActivity", result);
                final SnsListResult result1 = StrUtils.fromJson(result, SnsListResult.class);
                if (result1 == null) {
                    return;
                }
                if (!result1.isSuccess()) {
                    KLog.e("can not get sns list, " + result1.getMessage());
                } else {
                    mSwipeRefreshLayout.post(new Runnable() {
                        @Override
                        public void run() {
                            mSwipeRefreshLayout.setRefreshing(false);
                            mFriendCircleAdapter.setFriendCircleBeans(DataCenter.convert2FriendCircleBeans(result1.getData()));
                        }
                    });
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
                    return;
                }
                if (!result1.isSuccess()) {
                    KLog.e("can not get contact list, " + result1.getMessage());
                } else {
                    mSwipeRefreshLayout.post(new Runnable() {
                        @Override
                        public void run() {

                            mFriendCircleAdapter.setFrinds(result1.getData());
                        }
                    });
                }
            }
        });
    }
}
