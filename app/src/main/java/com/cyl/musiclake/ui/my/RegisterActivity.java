package com.cyl.musiclake.ui.my;

import android.annotation.TargetApi;
import android.os.Build;
import com.google.android.material.textfield.TextInputLayout;
import android.transition.Transition;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import com.cyl.musiclake.R;
import com.cyl.musiclake.ui.base.BaseActivity;
import com.cyl.musiclake.utils.SystemUtils;
import com.cyl.musiclake.utils.ToastUtils;
import com.getbase.floatingactionbutton.FloatingActionButton;

/**
 * 作者：yonglong on 2016/8/11 18:38
 * 邮箱：643872807@qq.com
 * 版本：2.5
 */
public class RegisterActivity extends BaseActivity<RegisterPresenter> implements RegisterContract.View {

    FloatingActionButton fab;
    TextInputLayout emailWrapper;
    TextInputLayout passWordWrapper;
    TextInputLayout userNameWrapper;

    ProgressBar progressBar;

    Button publish;



    @Override
    protected int getLayoutResID() {
        return R.layout.activity_register;
    }

    @Override
    protected void initView() {
        fab = findViewById(R.id.fab);
        emailWrapper = findViewById(R.id.emailWrapper);
        passWordWrapper = findViewById(R.id.passWordWrapper);
        userNameWrapper = findViewById(R.id.userNameWrapper);
        progressBar = findViewById(R.id.progressBar);
        publish = findViewById(R.id.publish);
        publish.setOnClickListener(v -> {
            final String email = emailWrapper.getEditText().getText().toString();
            final String password = passWordWrapper.getEditText().getText().toString();
            final String username = userNameWrapper.getEditText().getText().toString();

            mPresenter.register(email, password, username);
        });
        fab.setOnClickListener(v -> {
            onBackPressed();
        });

    }

    @Override
    protected String setToolbarTitle() {
        return "用户注册";
    }
    @Override
    protected void initData() {
        if (SystemUtils.isLollipop()) {
            getWindow().getEnterTransition().addListener(new EnterTransitionListener());
        }
    }

    @Override
    protected void initInjector() {
        mActivityComponent.inject(this);
    }

    @Override
    public void showLoading() {
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideLoading() {
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void showErrorInfo(int flag, String msg) {
        switch (flag) {
            case 0:
                ToastUtils.show(this, msg);
                break;
            case 1:
                emailWrapper.setError(msg);
                break;
            case 2:
                userNameWrapper.setError(msg);
                break;
            case 3:
                passWordWrapper.setError(msg);
                break;
        }
    }

    @Override
    public void showSuccessInfo(String msg) {
        ToastUtils.show(this, msg);
        finish();
    }

    @Override
    public void updateView() {
        emailWrapper.setErrorEnabled(false);
        passWordWrapper.setErrorEnabled(false);
        userNameWrapper.setErrorEnabled(false);
    }


    @TargetApi(Build.VERSION_CODES.KITKAT)
    private class EnterTransitionListener implements Transition.TransitionListener {
        @Override
        public void onTransitionStart(Transition transition) {

        }

        @Override
        public void onTransitionEnd(Transition transition) {
        }

        @Override
        public void onTransitionCancel(Transition transition) {

        }

        @Override
        public void onTransitionPause(Transition transition) {

        }

        @Override
        public void onTransitionResume(Transition transition) {

        }
    }


}
