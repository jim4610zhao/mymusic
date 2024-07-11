package com.cyl.musiclake.ui.base;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.cyl.musiclake.MusicApp;
import com.cyl.musiclake.R;
import com.cyl.musiclake.di.component.DaggerFragmentComponent;
import com.cyl.musiclake.di.component.FragmentComponent;
import com.cyl.musiclake.di.module.FragmentModule;
import com.cyl.musiclake.utils.LogUtil;
import com.trello.rxlifecycle2.LifecycleTransformer;
import com.trello.rxlifecycle2.components.support.RxFragment;

import javax.inject.Inject;

import static com.cyl.musiclake.utils.AnimationUtils.animateView;

/**
 * 作者：YongLong on 2016/8/8 16:58
 * 邮箱：643872807@qq.com
 * 版本：2.5
 */
public abstract class BaseFragment<T extends BaseContract.BasePresenter> extends RxFragment implements BaseContract.BaseView {
    @Nullable
    @Inject
    protected T mPresenter;
    protected FragmentComponent mFragmentComponent;
    public View rootView;
    public Toolbar mToolbar;
    public View emptyStateView;
    public View errorPanelRoot;
    public Button errorButtonRetry;

    public TextView emptyTextView;

    public TextView errorTextView;

    public ProgressBar loadingProgressBar;

    public SwipeRefreshLayout mSwipeRefreshLayout;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogUtil.d("onCreate", String.valueOf(this));
        initFragmentComponent();
        initInjector();
        attachView();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        rootView = super.onCreateView(inflater, container, savedInstanceState)
        LogUtil.d("onCreateView", String.valueOf(this));
        if (rootView == null) {
            rootView = inflater.inflate(getLayoutId(), container, false);
        }
        return rootView;
    }

    /**
     * 初始化FragmentComponent
     */
    private void initFragmentComponent() {
        mFragmentComponent = DaggerFragmentComponent.builder()
                .applicationComponent(MusicApp.getInstance().getApplicationComponent())
                .fragmentModule(new FragmentModule(this))
                .build();
    }
    private void initToolBar() {
        if (rootView != null) {
            mToolbar = rootView.findViewById(R.id.toolbar);
        }
        if (getActivity() != null && mToolbar != null) {
            mToolbar.setTitle(getToolBarTitle());
            AppCompatActivity appCompatActivity = (AppCompatActivity) getActivity();
            appCompatActivity.setSupportActionBar(mToolbar);
            appCompatActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        if (mSwipeRefreshLayout != null) {
            mSwipeRefreshLayout.setColorSchemeColors(Color.BLUE, Color.RED, Color.YELLOW);
//            mSwipeRefreshLayout.setProgressBackgroundColorSchemeColor(Color.parseColor("#FFFFFF"));
            mSwipeRefreshLayout.setEnabled(true);
            mSwipeRefreshLayout.setOnRefreshListener(() -> {
                LogUtil.d("下拉刷新");
                new Handler().postDelayed(() -> {
                    if (mSwipeRefreshLayout != null)
                        mSwipeRefreshLayout.setRefreshing(false);
                    loadData();
                }, 2000);
            });
        }
    }


    protected void listener() {

    }

    protected void retryLoading() {
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        LogUtil.d("onAttach", String.valueOf(this));
    }

    @Override
    public void onDetach() {
        super.onDetach();
        LogUtil.d("onDetach", String.valueOf(this));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        LogUtil.d("onDestroyView", String.valueOf(this));
        detachView();
    }

    public abstract int getLayoutId();

    protected void initViews() {
        if (rootView != null) {
            emptyStateView = rootView.findViewById(R.id.empty_state_view);
            errorPanelRoot = rootView.findViewById(R.id.error_panel);
            errorButtonRetry = rootView.findViewById(R.id.error_button_retry);
            emptyTextView = rootView.findViewById(R.id.tv_empty);
            errorTextView = rootView.findViewById(R.id.error_message_view);
            loadingProgressBar = rootView.findViewById(R.id.loading_progress_bar);
            mSwipeRefreshLayout = rootView.findViewById(R.id.swipe_refresh);
        }
    }

    protected void initInjector() {

    }

    protected void loadData() {

    }

    protected String getToolBarTitle() {
        return "";
    }

    @Override
    public void onResume() {
        super.onResume();
        LogUtil.d("onResume", String.valueOf(this));
        if (getUserVisibleHint()) {
            setUserVisibleHint(true);
        }
    }

    @Override
    public void onActivityCreated(final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        LogUtil.d("onActivityCreated", String.valueOf(this));
        setHasOptionsMenu(true);
        initToolBar();
        initViews();
        loadData();
        listener();
    }



    /**
     * 贴上view
     */
    private void attachView() {
        LogUtil.d("attachView", String.valueOf(this));
        if (mPresenter != null) {
            mPresenter.attachView(this);
        }
    }

    /**
     * 分离view
     */
    private void detachView() {
        LogUtil.d("detachView", String.valueOf(this));
        if (mPresenter != null) {
            mPresenter.detachView();
        }
    }

    @Override
    public void showLoading() {
        if (mSwipeRefreshLayout != null) mSwipeRefreshLayout.setRefreshing(true);
        if (emptyStateView != null) animateView(emptyStateView, false, 150);
        if (loadingProgressBar != null) animateView(loadingProgressBar, true, 400);
        if (errorPanelRoot != null) animateView(errorPanelRoot, false, 150);
    }

    @Override
    public void hideLoading() {
        if (mSwipeRefreshLayout != null) mSwipeRefreshLayout.setRefreshing(false);
        if (emptyStateView != null) animateView(emptyStateView, false, 150);
        if (loadingProgressBar != null) animateView(loadingProgressBar, false, 0);
        if (errorPanelRoot != null) animateView(errorPanelRoot, false, 150);
    }

    @Override
    public void showEmptyState() {
        if (emptyStateView != null) animateView(emptyStateView, true, 200);
        if (loadingProgressBar != null) animateView(loadingProgressBar, false, 0);
        if (errorPanelRoot != null) animateView(errorPanelRoot, false, 150);
    }

    @Override
    public void showError(String message, boolean showRetryButton) {
        hideLoading();
        if (errorTextView != null)
            errorTextView.setText(message);
        if (errorButtonRetry != null) {
            errorButtonRetry.setOnClickListener(v -> retryLoading());
            if (showRetryButton) animateView(errorButtonRetry, true, 600);
            else animateView(errorButtonRetry, false, 0);
        }
        if (errorPanelRoot != null) animateView(errorPanelRoot, true, 300);
    }

    @Override
    public <T> LifecycleTransformer<T> bindToLife() {
        return this.bindToLifecycle();
    }

    @Nullable
    @Override
    public Context getContext() {
        return super.getContext();
    }
}
