package com.cyl.musiclake.ui.main;

import android.widget.TextView;

import com.cyl.musicapi.BaseApiImpl;
import com.cyl.musiclake.R;
import com.cyl.musiclake.ui.base.BaseActivity;
import com.cyl.musiclake.bean.Music;
import com.cyl.musiclake.player.PlayManager;

public class TestActivity extends BaseActivity {
    BaseApiImpl searchApi;
    TextView resultTv;
    TextView statusTv;

    void btn_qq_topList() {
        searchApi.getAllQQTopList(result -> {
            statusTv.setText("getAllQQTopList");
            resultTv.setText(result.toString());
            return null;
        }, null);
    }


    void btn_netease_topList() {
        searchApi.getAllNeteaseTopList(result -> {
            statusTv.setText("getAllNeteaseTopList");
            resultTv.setText(result.toString());
            return null;
        }, null);
    }


    void test() {
        searchApi.getTopList("1", result -> {
            statusTv.setText("getTopList");
            resultTv.setText(result.toString());
            return null;
        }, fail -> {
            return null;
        });
    }


    void test2() {
        searchApi.searchSong("薛之谦", 10, 0, result -> {
            statusTv.setText("searchSong");
            resultTv.setText(result.toString());
            return null;
        }, null);
    }


    void test3() {
        searchApi.getSongDetail("qq", "001Qu4I30eVFYb", result -> {
            statusTv.setText("songDetail");
            resultTv.setText(result.toString());
            return null;
        }, null);
    }


    void test4() {
        searchApi.getLyricInfo("qq", "001Qu4I30eVFYb", result -> {
            statusTv.setText("getLyricInfo");
            resultTv.setText(result.toString());
            return null;
        });
    }


    void test5() {
        Music music = PlayManager.getPlayingMusic();
        if (music != null) {
            String type = music.getType();
            String mid = music.getMid();
//            searchApi.getComment(type, mid, songComment -> {
//                statusTv.setText("getComment");
//                resultTv.setText(songComment.toString());
//                return null;
//            });
        }
    }

    void test6() {
        Music music = PlayManager.getPlayingMusic();
        if (music != null) {
            String type = music.getType();
            String mid = music.getMid();
            searchApi.getSongUrl(type, mid, 128000, result -> {
                statusTv.setText("getSongUrl");
                resultTv.setText(result.toString());
                return null;
            }, null);
        }
    }

    void get() {
        searchApi.getBatchSongDetail("qq", new String[]{"001Qu4I30eVFYb"}, result -> {
            statusTv.setText("qq");
            resultTv.setText(result.toString());
            return null;
        });
    }

    void get1() {
        searchApi.getBatchSongDetail("netease", new String[]{"559647510", "437608504"}, result -> {
            statusTv.setText("netease[559647510,437608504]");
            resultTv.setText(result.toString());
            return null;
        });
    }

    @Override
    protected int getLayoutResID() {
        return R.layout.activity_test;
    }

    @Override
    protected void initView() {
        resultTv = findViewById(R.id.tv_show);
        statusTv = findViewById(R.id.tv_status);

        findViewById(R.id.btn_qq_topList).setOnClickListener(v -> {
            btn_qq_topList();
        });
        findViewById(R.id.btn_netease_topList).setOnClickListener(v -> {
            btn_netease_topList();
        });
        findViewById(R.id.btn_test1).setOnClickListener(v -> {
            test();
        });
        findViewById(R.id.btn_test2).setOnClickListener(v -> {
            test2();
        });
        findViewById(R.id.btn_test3).setOnClickListener(v -> {
            test3();
        });
        findViewById(R.id.btn_test4).setOnClickListener(v -> {
            test4();
        });
        findViewById(R.id.btn_test5).setOnClickListener(v -> {
            test5();
        });
        findViewById(R.id.btn_test6).setOnClickListener(v -> {
            test6();
        });
        findViewById(R.id.btn_playlist2).setOnClickListener(v -> {
            get();
        });
        findViewById(R.id.btn_playlist3).setOnClickListener(v -> {
            get1();
        });
    }


    @Override
    protected void initData() {
        searchApi = BaseApiImpl.INSTANCE;
    }

    @Override
    protected void initInjector() {

    }


}
