package com.cyl.musiclake.ui.base;

import static com.cyl.musiclake.player.PlayManager.mService;
import static com.cyl.musiclake.utils.AnimationUtils.animateView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.ServiceConnection;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewStub;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.cyl.musiclake.IMusicService;
import com.cyl.musiclake.MusicApp;
import com.cyl.musiclake.R;
import com.cyl.musiclake.di.component.ActivityComponent;

import com.cyl.musiclake.di.component.DaggerActivityComponent;
import com.cyl.musiclake.di.module.ActivityModule;
import com.cyl.musiclake.event.MetaChangedEvent;
import com.cyl.musiclake.player.PlayManager;
import com.cyl.musiclake.ui.main.WelcomeActivity;
import com.cyl.musiclake.ui.theme.ThemeStore;
import com.cyl.musiclake.utils.LogUtil;
import com.cyl.musiclake.utils.StatusBarUtil;
import com.cyl.musiclake.utils.SystemUtils;
import com.google.android.material.snackbar.Snackbar;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.trello.rxlifecycle2.LifecycleTransformer;
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import io.reactivex.disposables.Disposable;

/**
 * 基类
 *
 * @author yonglong
 * @date 2016/8/3
 */
public abstract class BaseActivity<T extends BaseContract.BasePresenter> extends RxAppCompatActivity implements ServiceConnection, BaseContract.BaseView {

    @Nullable
    @Inject
    protected T mPresenter;
    protected ActivityComponent mActivityComponent;

    public ViewStub loadingStubView;

    public View loadingView;

    public View emptyStateView;
    public View errorPanelRoot;
    public Button errorButtonRetry;
    public TextView errorTextView;
    public ProgressBar loadingProgressBar;

    public SwipeRefreshLayout mSwipeRefreshLayout;

    public Toolbar mToolbar;
    FrameLayout rootParent;

    protected Handler mHandler;

    private PlayManager.ServiceToken mToken;
    public Boolean isPause = true;

    private List<Disposable> disposables = new ArrayList<>();
    public String TAG = getClass().getSimpleName();

    RxPermissions rxPermissions;

    //需要检查的权限
    private final String[] mPermissionList = new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            //获取电话状态
            Manifest.permission.READ_PHONE_STATE,
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setUpTheme();
        StatusBarUtil.setTransparentForWindow(this);
        initStatusBar();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
        rootParent = findViewById(R.id.rootParent);
        EventBus.getDefault().register(this);
        rxPermissions = new RxPermissions(this);
        if (this instanceof WelcomeActivity && SystemUtils.isMarshmallow()) {
            checkPermissionAndThenLoad();
        } else {
            initPermissionSuccess();
        }
    }

    /**
     * 检查权限
     */
    @SuppressLint("CheckResult")
    private void checkPermissionAndThenLoad() {
        rxPermissions.request(mPermissionList)
                .subscribe(granted -> {
                    if (granted) {
                        initPermissionSuccess();
                    } else {
                        Snackbar.make(rootParent, getResources().getString(R.string.permission_hint),
                                        Snackbar.LENGTH_INDEFINITE)
                                .setAction(getResources().getString(R.string.sure), view -> checkPermissionAndThenLoad()).show();
                    }
                });
    }

    private void initPermissionSuccess() {
        mToken = PlayManager.bindToService(this, this);
        if (getLayoutResID() != -1) {
            LayoutInflater.from(this).inflate(getLayoutResID(), findViewById(R.id.rootParent));
        } else if (getViewBindingView() != null) {
            ((FrameLayout) findViewById(R.id.rootParent)).addView(getViewBindingView());
        }

        mHandler = new Handler();
        initActivityComponent();
        initInjector();
        initToolBar();
        attachView();
        initView();
    }

    /**
     * 初始化Dagger
     */
    private void initActivityComponent() {
        mActivityComponent = DaggerActivityComponent.builder()
                .applicationComponent(MusicApp.getInstance().getApplicationComponent())
                .activityModule(new ActivityModule(this))
                .build();
    }

    private void initLoading() {
        if (loadingView == null && loadingStubView != null) {
            loadingView = loadingStubView.inflate();
            emptyStateView = loadingView.findViewById(R.id.empty_state_view);
            errorPanelRoot = loadingView.findViewById(R.id.error_panel);
            errorButtonRetry = loadingView.findViewById(R.id.error_button_retry);
            errorTextView = loadingView.findViewById(R.id.error_message_view);
            loadingProgressBar = loadingView.findViewById(R.id.loading_progress_bar);
        }
    }

    private void initToolBar() {
        loadingStubView = findViewById(R.id.loadingView);
        mToolbar = findViewById(R.id.toolbar);
        if (hasToolbar() && mToolbar != null) {
            if (setToolbarTitle() != null)
                mToolbar.setTitle(setToolbarTitle());
            setSupportActionBar(mToolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        mSwipeRefreshLayout = findViewById(R.id.swipe_refresh);
        if (mSwipeRefreshLayout != null) {
            //设置刷新球颜色
            mSwipeRefreshLayout.setColorSchemeColors(Color.parseColor("#05b962"), Color.parseColor("#F4B400"), Color.parseColor("#DB4437"));
            mSwipeRefreshLayout.setProgressBackgroundColorSchemeColor(Color.WHITE);
            mSwipeRefreshLayout.setEnabled(false);
        }
    }

    /**
     * 更新title
     *
     * @param title
     */
    protected void updateTitle(String title) {
        if (hasToolbar() && mToolbar != null) {
            if (setToolbarTitle() != null)
                mToolbar.setTitle(title);
            setSupportActionBar(mToolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    protected int getLayoutResID() {
        return -1;
    }

    protected View getViewBindingView() {
        return null;
    }

    protected abstract void initView();

    protected abstract void initData();

    protected abstract void initInjector();

    protected void listener() {
    }


    protected void retryLoading() {
    }

    protected boolean hasToolbar() {
        return true;
    }

    protected String setToolbarTitle() {
        return null;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        if (mToken != null) {
            PlayManager.unbindFromService(mToken);
            mToken = null;
        }
        for (Disposable disposable : disposables) {
            disposable.dispose();
        }
        detachView();
    }


    /**
     * 贴上view
     */
    private void attachView() {
        if (mPresenter != null) {
            mPresenter.attachView(this);
        }
    }

    /**
     * 分离view
     */
    private void detachView() {
        if (mPresenter != null) {
            mPresenter.detachView();
        }
    }

    @Override
    public void showLoading() {
        initLoading();
        if (emptyStateView != null) animateView(emptyStateView, false, 150);
        if (loadingProgressBar != null) animateView(loadingProgressBar, true, 400);
        animateView(errorPanelRoot, false, 150);
    }

    @Override
    public void hideLoading() {
        if (emptyStateView != null) animateView(emptyStateView, false, 150);
        if (loadingProgressBar != null) animateView(loadingProgressBar, false, 0);
        animateView(errorPanelRoot, false, 150);
    }

    @Override
    public void showEmptyState() {
        initLoading();
        if (emptyStateView != null) animateView(emptyStateView, true, 200);
        if (loadingProgressBar != null) animateView(loadingProgressBar, false, 0);
        animateView(errorPanelRoot, false, 150);
    }

    @Override
    public void showError(String message, boolean showRetryButton) {
        initLoading();
        hideLoading();
        if (errorTextView != null)
            errorTextView.setText(message);
        if (errorButtonRetry != null)
            errorButtonRetry.setOnClickListener(v -> retryLoading());
        if (showRetryButton) animateView(errorButtonRetry, true, 600);
        else animateView(errorButtonRetry, false, 0);
        animateView(errorPanelRoot, true, 300);
    }


    @Override
    public <T> LifecycleTransformer<T> bindToLife() {
        return this.bindToLifecycle();
    }

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        isPause = false;
    }

    @Override
    protected void onStop() {
        super.onStop();
        isPause = true;
    }

    @Override
    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        mService = IMusicService.Stub.asInterface(iBinder);
        listener();
        initData();
    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {
        mService = null;
        LogUtil.d("BaseActivity", "onServiceDisconnected");
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDefaultEvent(MetaChangedEvent event) {
    }


    /**
     * 全屏沉淀式状态栏
     */
    public void initStatusBar() {
        // 延伸显示区域到刘海
        WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            layoutParams.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
            // 延伸显示区域到刘海
            getWindow().setAttributes(layoutParams);
        }
    }

    private void setUpTheme() {
        ThemeStore.THEME_MODE = ThemeStore.getThemeMode();
        LogUtil.d("BaseActivity", "setUpTheme THEME_MODE = " + ThemeStore.THEME_MODE);
        if (ThemeStore.THEME_MODE == ThemeStore.SYSTEM) {
            if (isDarkTheme(this)) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }
        } else {
            if (ThemeStore.THEME_MODE == ThemeStore.NIGHT) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }
        }
    }

    /**
     * 更新App主题
     */
    public void updateAppTheme(int index) {
        ThemeStore.updateThemeMode(index);
        setUpTheme();
    }

    public boolean isDarkTheme(Context context) {
        int flag = context.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        return flag == Configuration.UI_MODE_NIGHT_YES;
    }

    /**
     * https://mp.weixin.qq.com/s/d9QCoBP6kV9VSWvVldVVwA
     * 屏幕适配
     */
    private void setCustomDensity(Activity activity) {
        DisplayMetrics appDisplayMetrics = MusicApp.getAppContext().getResources().getDisplayMetrics();
        //density = px/dp dp值是设计图的宽度
        float targetDensity = appDisplayMetrics.widthPixels / 360f;
        int targetDensityDpi = (int) (160 * targetDensity);
        float targetScaledDensity = targetDensity * (appDisplayMetrics.scaledDensity / appDisplayMetrics.density);

        appDisplayMetrics.density = targetDensity;
        appDisplayMetrics.scaledDensity = targetScaledDensity;
        appDisplayMetrics.densityDpi = targetDensityDpi;

        DisplayMetrics activityDisplayMetrics = activity.getResources().getDisplayMetrics();
        activityDisplayMetrics.densityDpi = targetDensityDpi;
        activityDisplayMetrics.density = targetDensity;
        activityDisplayMetrics.scaledDensity = targetScaledDensity;
        density = targetDensity;
    }

    private float density = 0f;

    @Override
    public Resources getResources() {
        Resources res = super.getResources();
        if (res.getConfiguration().fontScale != 1f) {
            Configuration newConfig = new Configuration();
            newConfig.setToDefaults();
            res.updateConfiguration(newConfig, res.getDisplayMetrics());
        }
        //同步这只density，解决横屏息屏后UI问题，问题原因是息屏后density变化导致， 推出原因是谷歌广告造成，目前只想到这种方式来解决
        if (density != 0f) {
            res.getDisplayMetrics().density = density;
        }
        return res;
    }

}
