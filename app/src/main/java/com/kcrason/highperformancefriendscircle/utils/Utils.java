package com.kcrason.highperformancefriendscircle.utils;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Paint;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;

import com.cc.wechatmanager.R;
import com.kcrason.highperformancefriendscircle.FriendsCircleApplication;
import com.kcrason.highperformancefriendscircle.enums.TranslationState;
import com.kcrason.highperformancefriendscircle.interfaces.OnItemClickPopupMenuListener;
import com.kcrason.highperformancefriendscircle.interfaces.OnStartSwipeRefreshListener;

import java.util.concurrent.TimeUnit;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;

/**
 * @author KCrason
 * @date 2018/5/6
 */
public class Utils {

    public static int dp2px(float dpValue) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpValue, FriendsCircleApplication.sContext.getResources().getDisplayMetrics());
    }

    public static int getScreenWidth() {
        return FriendsCircleApplication.sContext.getResources().getDisplayMetrics().widthPixels;
    }

    public static int getScreenHeight() {
        return FriendsCircleApplication.sContext.getResources().getDisplayMetrics().heightPixels;
    }

    public static int calcStatusBarHeight(Context context) {
        int statusHeight = -1;
        try {
            Class<?> clazz = Class.forName("com.android.internal.R$dimen");
            Object object = clazz.newInstance();
            int height = Integer.parseInt(clazz.getField("status_bar_height").get(object).toString());
            statusHeight = context.getResources().getDimensionPixelSize(height);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return statusHeight;
    }

    public static boolean calculateShowCheckAllText(String content) {
        Paint textPaint = new Paint();
        textPaint.setTextSize(Utils.dp2px(16f));
        float textWidth = textPaint.measureText(content);
        float maxContentViewWidth = Utils.getScreenWidth() - Utils.dp2px(74f);
        float maxLines = textWidth / maxContentViewWidth;
        return maxLines > 4;
    }


    public static void showSwipeRefreshLayout(final SwipeRefreshLayout swipeRefreshLayout, final OnStartSwipeRefreshListener onStartSwipeRefreshListener) {
        if (swipeRefreshLayout != null) {
            swipeRefreshLayout.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            swipeRefreshLayout.setRefreshing(true);
                                            Single.timer(200, TimeUnit.MILLISECONDS, AndroidSchedulers.mainThread())
                                                    .subscribe(new Consumer<Long>() {
                                                        @Override
                                                        public void accept(Long aLong) throws Exception {
                                                            if (onStartSwipeRefreshListener != null) {
                                                                onStartSwipeRefreshListener.onStartRefresh();
                                                            }
                                                        }
                                                    });
                                        }
                                    }

            );
        }
    }

    public static void hideSwipeRefreshLayout(final SwipeRefreshLayout swipeRefreshLayout) {
        if (swipeRefreshLayout != null) {
            swipeRefreshLayout.post(new Runnable() {
                @Override
                public void run() {
                    swipeRefreshLayout.setRefreshing(false);
                }
            });
        }
    }

    public static void showPopupMenu(Context context, final OnItemClickPopupMenuListener onItemClickPopupMenuListener,
                                     final int position, View view, TranslationState translationState) {
        if (translationState != null) {
            PopupMenu popup = new PopupMenu(context, view);
            if (translationState == TranslationState.START) {
                popup.getMenuInflater().inflate(R.menu.popup_menu_start, popup.getMenu());
            } else if (translationState == TranslationState.CENTER) {
                popup.getMenuInflater().inflate(R.menu.popup_menu_center, popup.getMenu());
            } else if (translationState == TranslationState.END) {
                popup.getMenuInflater().inflate(R.menu.popup_menu_end, popup.getMenu());
            }
            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    int i = item.getItemId();
                    if (i == R.id.copy) {
                        if (onItemClickPopupMenuListener != null) {
                            onItemClickPopupMenuListener.onItemClickCopy(position);
                        }

                    } else if (i == R.id.collection) {
                        if (onItemClickPopupMenuListener != null) {
                            onItemClickPopupMenuListener.onItemClickCollection(position);
                        }

                    } else if (i == R.id.translation) {
                        if (onItemClickPopupMenuListener != null) {
                            onItemClickPopupMenuListener.onItemClickTranslation(position);
                        }

                    } else if (i == R.id.hide_translation) {
                        if (onItemClickPopupMenuListener != null) {
                            onItemClickPopupMenuListener.onItemClickHideTranslation(position);
                        }

                    } else {
                    }
                    return true;
                }
            });
            popup.show(); //showing popup menu
        }
    }


    public static void startAlphaAnimation(final View view, boolean isShowTranslation) {
        if (isShowTranslation && view != null) {
            ValueAnimator valueAnimator = ValueAnimator.ofFloat(0.5f, 1f);
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    view.setAlpha((Float) animation.getAnimatedValue());
                }
            });
            valueAnimator.setDuration(500).start();
        }
    }
}
