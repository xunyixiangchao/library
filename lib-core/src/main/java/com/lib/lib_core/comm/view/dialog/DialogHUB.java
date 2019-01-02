package com.lib.lib_core.comm.view.dialog;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lib.lib_core.R;

import work.dd.com.dialog.DialogUiBuilder;
import work.dd.com.utils.ThreadPoolUtil;
import work.dd.com.utils.ViewUtil;

/**
 * 基于view的进度条
 * Created by liuyuhang on 16/5/11.
 */
public class DialogHUB {
    private static final int PROGRESS_DISPLAY_DELAY = 300;

    private LinearLayout mParentView;
    private LinearLayout mNetErrorView;
    private View mProgressView, mMessageView;
    private boolean isProgressShowing = false;
    public boolean isErrorViewShowing = false;

    private LayoutInflater inflater;
    private DialogUiBuilder mProgressUiBuilder, mMessageViewUiBuilder, mNetErrorUiBuilder;

    private int mProgressBackgroundColor;

    /**
     * progress显示有一个延时，如果isProgressStartShow=true表示他准备显示
     */
//    private boolean isProgressPreShow = false;

    /**
     * 将ProgressHub绑定到当前页面
     *
     * @param activity
     */
    public void bindDialog(final Activity activity) {
        bindDialog(activity, activity.getWindow().getDecorView());
    }

    /**
     * 将ProgressHub绑定到当前页面
     *
     * @param activity
     * @param view     需要绑定的view
     */
    public void bindDialog(final Activity activity, View view) {
        final Context context = view.getContext();
        if (null == context) {
            return;
        }

        mParentView = (LinearLayout) view.findViewById(R.id.progresshub_root_layout);
        mNetErrorView = (LinearLayout) view.findViewById(R.id.neterror_root_layout);
        if (null == mParentView) {
            throw new NullPointerException("布局中需要引入frame_progress_hub_layout");
        }
        inflater = LayoutInflater.from(context);

        mParentView.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));

        mProgressUiBuilder = new DialogUiBuilder() {
            @Override
            public View onViewCreate(@NonNull LayoutInflater inflater) {
                return inflater.inflate(R.layout.frame_dialog_progress_hub_layout, null);
            }

            @Override
            public void onViewDraw(@NonNull View view, String message) {
                TextView messageView = (TextView) view.findViewById(R.id.textview_progress_message);
                if (!TextUtils.isEmpty(message)) {
                    messageView.setText(message);
                }
            }
        };

        mMessageViewUiBuilder = new DialogUiBuilder() {
            @Override
            public View onViewCreate(@NonNull LayoutInflater inflater) {
                TextView messageView = new TextView(context);
                messageView.setGravity(Gravity.CENTER);
//                messageView.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                messageView.setText("暂无数据，点击重试");
                return messageView;
            }

            @Override
            public void onViewDraw(@NonNull View view, String message) {
                if (view instanceof TextView) {
                    TextView messageView = (TextView) view;
                    if (!TextUtils.isEmpty(message)) {
                        messageView.setText(message);
                    }
                }
            }
        };

        mNetErrorUiBuilder = new DialogUiBuilder() {
            @Override
            public View onViewCreate(@NonNull LayoutInflater inflater) {
                TextView messageView = new TextView(context);
                messageView.setGravity(Gravity.CENTER);
//                messageView.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                messageView.setText("网络错误，点击重试");
                return messageView;
            }

            @Override
            public void onViewDraw(@NonNull View view, String message) {
                if (view instanceof TextView) {
                    TextView messageView = (TextView) view;
                    if (!TextUtils.isEmpty(message)) {
                        messageView.setText(message);
                    }
                }
            }
        };

        updateUi();
    }

    /**
     * 设置顶部和底部的间距
     *
     * @param top
     * @param bottom
     */
    public void setMarginTopAndBottom(int top, int bottom) {

        RelativeLayout.LayoutParams mParentViewLayoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        mParentViewLayoutParams.topMargin = top;
        mParentViewLayoutParams.bottomMargin = bottom;

        mParentView.setLayoutParams(mParentViewLayoutParams);
        mNetErrorView.setLayoutParams(mParentViewLayoutParams);

//        mProgressView.setLayoutParams(mParentViewLayoutParams);
//        mMessageView.setLayoutParams(mParentViewLayoutParams);
    }

    public void setDialogBackground(int color) {
//        mParentView.setBackgroundColor(color);
        mProgressBackgroundColor = color;
    }

    /**
     * 设置自定义布局
     *
     * @param uiBuilder
     */
    public void setProgressUiBuilder(DialogUiBuilder uiBuilder) {
        this.mProgressUiBuilder = uiBuilder;

        updateUi();
    }

    /**
     * 设置自定义布局
     *
     * @param uiBuilder
     */
    public void setMessageViewUiBuilder(DialogUiBuilder uiBuilder) {
        this.mMessageViewUiBuilder = uiBuilder;

        updateUi();
    }

    /**
     * 设置自定义布局
     *
     * @param uiBuilder
     */
    public void setNetErrorUiBuilder(DialogUiBuilder uiBuilder) {
        this.mNetErrorUiBuilder = uiBuilder;

//        updateUi();
    }

    private void updateUi() {
        mParentView.removeAllViews();

        mParentView.setGravity(Gravity.CENTER);

        mProgressView = mProgressUiBuilder.onViewCreate(inflater);
        mMessageView = mMessageViewUiBuilder.onViewCreate(inflater);


        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        layoutParams.gravity = Gravity.CENTER;

        mProgressView.setLayoutParams(layoutParams);
        mMessageView.setLayoutParams(layoutParams);

        mParentView.addView(mProgressView);
        mParentView.addView(mMessageView);
    }

    /**
     * 显示ProgressHUB
     */
    public void showProgress() {
        showProgress(null);
    }

    /**
     * 显示ProgressHUB
     */
    public void showTransparentProgress() {
        showTransparentProgress(null);
    }

    /**
     * 显示ProgressHUB
     * @param isDelay 是否延迟显示
     */
    public void showTransparentProgress(boolean isDelay) {
        showTransparentProgress(null, isDelay);
    }

    /**
     * 显示ProgressHUB
     *
     * @param message
     */
    public void showProgress(String message) {
        showProgress(message, false, mProgressBackgroundColor);
    }

    public void showTransparentProgress(String message) {
        showTransparentProgress(message, false);
    }

    public void showTransparentProgress(String message, boolean delay) {
        showProgress(message, delay, mParentView.getContext().getResources().getColor(android.R.color.transparent));
    }

    /**
     * 显示ProgressHUB
     *
     * @param message
     */
    private void showProgress(final String message, boolean isDelay, final int backgroundColor) {
        if (isDelay) {
            isProgressShowing = true;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (!isProgressShowing) {
                        cleanAnimation();
                        mParentView.setBackgroundColor(backgroundColor);
                        ViewUtil.INSTANCE.swapView(mNetErrorView, mParentView);
                        ViewUtil.INSTANCE.swapView(mMessageView, mProgressView);
                        mProgressUiBuilder.onViewDraw(mProgressView, message);
                        mParentView.setOnClickListener(null);
                    }

                }
            }, PROGRESS_DISPLAY_DELAY);
        } else {
            cleanAnimation();
            this.isProgressShowing = true;
            mParentView.setBackgroundColor(backgroundColor);
            ViewUtil.INSTANCE.swapView(mNetErrorView, mParentView);
            ViewUtil.INSTANCE.swapView(mMessageView, mProgressView);
            mProgressUiBuilder.onViewDraw(mProgressView, message);
            mParentView.setOnClickListener(null);
        }

    }


    /**
     * 显示错误信息的页面（数据为空或者网络错误都可以）
     *
     * @param message
     */
    public void showMessageView(final String message, final View.OnClickListener listener) {
        cleanAnimation();
        isErrorViewShowing = true;
        ViewUtil.INSTANCE.swapView(mNetErrorView, mParentView);
        ViewUtil.INSTANCE.swapView(mProgressView, mMessageView);

        mParentView.setBackgroundColor(mProgressBackgroundColor);

        ThreadPoolUtil.Companion.runOnMainThread(new Runnable() {
            @Override
            public void run() {
                mMessageViewUiBuilder.onViewDraw(mMessageView, message);
            }
        });

        mParentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    showProgress();
                    listener.onClick(v);
                }
            }
        });
    }

    public void showNotErrorView(final View.OnClickListener listener) {
        showNotErrorView(listener, true);
    }

    public void showNotErrorView(final View.OnClickListener listener, final boolean showProgress) {
        cleanAnimation();
        isErrorViewShowing = true;
        ViewUtil.INSTANCE.swapView(mParentView, mNetErrorView);

        mNetErrorView.setBackgroundColor(mProgressBackgroundColor);

        mNetErrorView.removeAllViews();
        View netErrorView = mNetErrorUiBuilder.onViewCreate(inflater);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        netErrorView.setLayoutParams(lp);
        mNetErrorView.addView(netErrorView);

        mNetErrorUiBuilder.onViewDraw(mNetErrorView, "");

        mNetErrorView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    if (showProgress) {
                        showProgress();
                    }
                    listener.onClick(v);
                }
            }
        });
    }

    public void cleanAnimation() {
        if (mParentView != null) {
            mParentView.clearAnimation();
        }
        if (mNetErrorView != null) {
            mNetErrorView.clearAnimation();
        }
    }

    /**
     * 隐藏DialogHub
     */
    public void dismiss() {
        isProgressShowing = false;
        isErrorViewShowing = false;
        AlphaAnimation alphaAnimation = new AlphaAnimation(1.0f, 0.0f);
        alphaAnimation.setDuration(300);
        ViewUtil.INSTANCE.setViewVisibility(View.GONE, alphaAnimation, mParentView, mNetErrorView);
    }

    public boolean isShowingProgress() {
        return isProgressShowing;
    }


}
