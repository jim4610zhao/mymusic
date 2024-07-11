package com.cyl.musiclake.ui.music.local.fragment

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cyl.musiclake.R
import com.cyl.musiclake.bean.Music
import com.cyl.musiclake.common.Constants
import com.cyl.musiclake.player.PlayManager
import com.cyl.musiclake.ui.base.BaseLazyFragment
import com.cyl.musiclake.ui.music.dialog.BottomDialogFragment
import com.cyl.musiclake.ui.music.edit.EditSongListActivity
import com.cyl.musiclake.ui.music.local.adapter.SongAdapter
import com.cyl.musiclake.ui.music.local.contract.SongsContract
import com.cyl.musiclake.ui.music.local.presenter.SongsPresenter
import com.cyl.musiclake.utils.LogUtil
import org.jetbrains.anko.support.v4.startActivity
import java.util.*

/**
 * 功能：本地歌曲列表
 * 作者：yonglong on 2016/8/10 20:49
 * 邮箱：643872807@qq.com
 * 版本：2.5
 */
class SongsFragment : BaseLazyFragment<SongsPresenter>(), SongsContract.View {

    private var mAdapter: SongAdapter? = null
    private val musicList = ArrayList<Music>()
    private lateinit var iconIv:ImageView
    private lateinit var menuIv:ImageView
    private lateinit var songNumTv:TextView

    companion object {
        fun newInstance(): SongsFragment {
            val args = Bundle()
            val fragment = SongsFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.frag_local_song
    }

    override fun initViews() {
        var recyclerView = rootView.findViewById<RecyclerView>(R.id.recyclerView)
        iconIv = rootView.findViewById(R.id.iconIv)
        menuIv = rootView.findViewById(R.id.menuIv)
        songNumTv = rootView.findViewById(R.id.songNumTv)
        mAdapter = SongAdapter(musicList)
        recyclerView.layoutManager = LinearLayoutManager(activity)
        recyclerView.adapter = mAdapter
        initHeaderView()
        iconIv.setOnClickListener { v ->
            if (musicList.size == 0) return@setOnClickListener
            val id = Random().nextInt(musicList.size)
            PlayManager.play(id, musicList, Constants.PLAYLIST_LOCAL_ID)
        }
    }

    override fun initInjector() {
        mFragmentComponent.inject(this)
    }

    override fun listener() {
        mAdapter?.setOnItemClickListener { _, view, position ->
            if (view.id != R.id.iv_more) {
                PlayManager.play(position, musicList, Constants.PLAYLIST_LOCAL_ID)
            }
        }
        mAdapter?.setOnItemChildClickListener { adapter, _, position ->
            val music = adapter.getItem(position) as Music?
            BottomDialogFragment.newInstance(music, Constants.PLAYLIST_LOCAL_ID)
                    .apply {
                        removeSuccessListener = {
                            LogUtil.d("SongsFragment", "position = $position")
                            musicList.removeAt(position)
                            initHeaderView()
                            this@SongsFragment.mAdapter?.notifyItemRemoved(position)
                        }
                    }
                    .show(mFragmentComponent.activity as AppCompatActivity)
        }
        menuIv.setOnClickListener {
            EditSongListActivity.musicList = musicList
            startActivity<EditSongListActivity>()
        }
    }

    override fun onLazyLoad() {
        mPresenter?.loadSongs(true)
    }


    private fun initHeaderView() {
        songNumTv?.text = getString(R.string.random_play_num, musicList.size)
//        if (musicList.size == 0) {
//            setEmptyView()
//        }
    }

    override fun showSongs(songList: MutableList<Music>) {
        LogUtil.d("SongsFragment", "showSongs = ${songList.size}")
        musicList.clear()
        musicList.addAll(songList)
        mAdapter?.setNewData(musicList)
        songNumTv?.text = getString(R.string.random_play_num, musicList.size)
        hideLoading()
    }

    override fun showLoading() {
        super.showLoading()
    }

    override fun hideLoading() {
        super.hideLoading()
    }

    override fun setEmptyView() {
        mAdapter?.setEmptyView(R.layout.view_song_empty)
    }
}
