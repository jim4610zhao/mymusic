package com.cyl.musiclake.ui.main;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.cyl.musicapi.BaseApiImpl;
import com.cyl.musiclake.MusicApp;
import com.cyl.musiclake.R;
import com.cyl.musiclake.common.Constants;
import com.cyl.musiclake.ui.base.BaseActivity;
import com.cyl.musiclake.utils.SPUtils;
import com.cyl.musiclake.utils.SystemUtils;
import com.google.android.material.snackbar.Snackbar;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.openapi.WBAPIFactory;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.tencent.tauth.Tencent;

/**
 * Created by 永龙 on 2016/3/19.
 */
public class WelcomeActivity extends BaseActivity {


    ConstraintLayout container;
    ImageView heardCoverIv;


    @Override
    protected void listener() {

    }

    @Override
    protected int getLayoutResID() {
        return R.layout.activity_welcome;
    }

    @Override
    protected void initView() {
        container = findViewById(R.id.wel_container);
        heardCoverIv = findViewById(R.id.iv_header_cover);
    }

    @Override
    protected void initData() {
        //初始化WebView
        BaseApiImpl.INSTANCE.initWebView(MusicApp.getInstance());
        initLogin();

        if (SystemUtils.isR() && !Environment.isExternalStorageManager()) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
            startActivityForResult(intent, 0);
        } else {
            initWelcome();
        }

    }


    private void initLogin() {
        //创建微博实例
        AuthInfo authInfo = new AuthInfo(this, Constants.APP_KEY, Constants.REDIRECT_URL, Constants.SCOPE);
        MusicApp.iwbapi = WBAPIFactory.createWBAPI(this);
        MusicApp.iwbapi.registerApp(this, authInfo);

        //腾讯
        MusicApp.mTencent = Tencent.createInstance(Constants.APP_ID, this);
        //初始化socket，因后台服务器压力大，暂时注释
//        SocketManager.INSTANCE.initSocket();
    }


    @Override
    protected void initInjector() {

    }


    /**
     * 检查服务是否运行
     */
    private void initWelcome() {
        boolean isFirst = SPUtils.getAnyByKey(SPUtils.SP_KEY_FIRST_COMING, true);
        if (isFirst) {
            getCoverImageUrl();
            SPUtils.putAnyCommit(SPUtils.SP_KEY_FIRST_COMING, false);
        } else {
            mHandler.postDelayed(WelcomeActivity.this::startMainActivity, 1000);
        }
    }

    /**
     * 欢迎界面跳转到主界面
     */
    private void startMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setAction(getIntent().getAction());
        startActivity(intent);
        overridePendingTransition(0, 0);
        finish();
    }

    private void getCoverImageUrl() {
        mHandler.postDelayed(WelcomeActivity.this::startMainActivity, 3000);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 0 && Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                if (Environment.isExternalStorageManager()) {
                    // 已经获取MANAGE_EXTERNAL_STORAGE权限
                    initWelcome();
                    Toast.makeText(this, "已获取MANAGE_EXTERNAL_STORAGE权限", Toast.LENGTH_SHORT).show();
                } else {
                    // 未获取MANAGE_EXTERNAL_STORAGE权限
                    Toast.makeText(this, "未获取MANAGE_EXTERNAL_STORAGE权限", Toast.LENGTH_SHORT).show();
                }

        }
    }

}
