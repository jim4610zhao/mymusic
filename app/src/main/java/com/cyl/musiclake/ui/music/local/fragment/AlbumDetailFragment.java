package com.cyl.musiclake.ui.music.local.fragment;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.PopupMenu;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cyl.musiclake.R;
import com.cyl.musiclake.bean.Music;
import com.cyl.musiclake.common.Extras;
import com.cyl.musiclake.player.PlayManager;
import com.cyl.musiclake.ui.base.BaseFragment;
import com.cyl.musiclake.ui.music.dialog.AddPlaylistDialog;
import com.cyl.musiclake.ui.music.dialog.ShowDetailDialog;
import com.cyl.musiclake.ui.music.local.adapter.SongAdapter;
import com.cyl.musiclake.ui.music.local.contract.AlbumDetailContract;
import com.cyl.musiclake.ui.music.local.presenter.AlbumDetailPresenter;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

/**
 * 作者：yonglong on 2016/8/15 19:54
 * 邮箱：643872807@qq.com
 * 版本：2.5
 * 专辑
 */
public class AlbumDetailFragment extends BaseFragment<AlbumDetailPresenter> implements AlbumDetailContract.View {


    RecyclerView mRecyclerView;

    CollapsingToolbarLayout collapsing_toolbar;
    ImageView album_art;
    FloatingActionButton mFab;

    String albumID;
    String transitionName;
    String title;

    private SongAdapter mAdapter;
    private List<Music> musicInfos = new ArrayList<>();

    public static AlbumDetailFragment newInstance(String id, String title, String transitionName) {
        Bundle args = new Bundle();
        args.putString(Extras.ALBUM_ID, id);
        args.putString(Extras.PLAYLIST_NAME, title);
        args.putString(Extras.TRANSITIONNAME, transitionName);
        AlbumDetailFragment fragment = new AlbumDetailFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void loadData() {
        showLoading();
        mPresenter.loadAlbumSongs(title);
    }

    @Override
    public int getLayoutId() {
        return R.layout.frag_playlist_detail;
    }

    @Override
    public void initViews() {

        mRecyclerView = rootView.findViewById(R.id.recyclerView);
        collapsing_toolbar = rootView.findViewById(R.id.collapsing_toolbar);
        album_art = rootView.findViewById(R.id.album_art);
        mFab = rootView.findViewById(R.id.fab);

        mFab.setOnClickListener(v -> {
            PlayManager.play(0, musicInfos, albumID);
        });

        albumID = getArguments().getString(Extras.ALBUM_ID);
        transitionName = getArguments().getString(Extras.TRANSITIONNAME);
        title = getArguments().getString(Extras.PLAYLIST_NAME);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (transitionName != null) {
                album_art.setTransitionName(transitionName);
                album_art.setHasTransientState(true);
            }
        }

        if (title != null)
            collapsing_toolbar.setTitle(title);

        setHasOptionsMenu(true);
        if (getActivity() != null) {
            AppCompatActivity appCompatActivity = (AppCompatActivity) getActivity();
            appCompatActivity.setSupportActionBar(mToolbar);
            appCompatActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        mAdapter = new SongAdapter(musicInfos);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    protected void initInjector() {
        mFragmentComponent.inject(this);
    }

    @Override
    protected void listener() {
        mAdapter.setOnItemClickListener((adapter, view, position) -> {
            if (view.getId() != R.id.iv_more) {
                PlayManager.play(position, musicInfos, albumID);
            }
        });
        mAdapter.setOnItemChildClickListener((adapter, view, position) -> {
            PopupMenu popupMenu = new PopupMenu(getContext(), view);
            popupMenu.setOnMenuItemClickListener(item -> {
                if (item.getItemId() == R.id.popup_song_play) {
                    PlayManager.play(position, musicInfos, albumID);
                } else if (item.getItemId() == R.id.popup_song_detail) {
                    ShowDetailDialog.newInstance((Music) adapter.getItem(position))
                            .show(getChildFragmentManager(), getTag());
                } else if (item.getItemId() == R.id.popup_song_addto_queue) {
                    AddPlaylistDialog.Companion.newInstance(musicInfos.get(position))
                            .show(getChildFragmentManager(), "ADD_PLAYLIST");
                }
                return false;
            });
            popupMenu.inflate(R.menu.popup_album);
            popupMenu.show();
        });
    }

    @Override
    public void showLoading() {
        super.showLoading();
    }

    @Override
    public void hideLoading() {
        super.hideLoading();
    }

    @Override
    public void showEmptyView() {
        mAdapter.setEmptyView(R.layout.view_song_empty);
    }

    @Override
    public void showAlbumSongs(List<Music> songList) {
        musicInfos = songList;
        mAdapter.setNewData(musicInfos);
        hideLoading();
    }


    @Override
    public void showAlbumArt(Drawable albumArt) {
        album_art.setImageDrawable(albumArt);
    }

    @Override
    public void showAlbumArt(Bitmap bitmap) {
        album_art.setImageBitmap(bitmap);
    }

}
