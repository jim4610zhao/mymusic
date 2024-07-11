package com.cyl.musiclake.ui.music.search

import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.MenuItem
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.view.inputmethod.EditorInfo
import androidx.recyclerview.widget.LinearLayoutManager
import com.cyl.musiclake.R
import com.cyl.musiclake.bean.Music
import com.cyl.musiclake.common.Constants
import com.cyl.musiclake.databinding.ActivityPlaylistSearchBinding
import com.cyl.musiclake.player.PlayManager
import com.cyl.musiclake.ui.base.BaseActivity
import com.cyl.musiclake.ui.base.BaseContract
import com.cyl.musiclake.ui.base.BasePresenter
import com.cyl.musiclake.ui.music.dialog.BottomDialogFragment
import com.cyl.musiclake.ui.music.local.adapter.SongAdapter
import com.cyl.musiclake.utils.Tools

/**
 * 作者：yonglong on 2016/9/15 12:32
 * 邮箱：643872807@qq.com
 * 版本：2.5
 */
class PlaylistSearchActivity : BaseActivity<BasePresenter<BaseContract.BaseView>>() {
    /**
     * 搜索信息
     */
    private var queryString: String = ""

    /**
     * 搜索结果
     */
    private val searchResults = mutableListOf<Music>()

    companion object {
        /**
         * 歌曲列表
         */
        var musicList = mutableListOf<Music>()
    }

    /**
     * 适配器
     */
    private var mAdapter: SongAdapter = SongAdapter(searchResults)

    private val viewBinding by lazy { ActivityPlaylistSearchBinding.inflate(layoutInflater) }

    override fun getViewBindingView(): View {
        return viewBinding.root
    }
    override fun getLayoutResID(): Int {
        return R.layout.activity_playlist_search
    }

    override fun initView() {
        showSearchOnStart()
    }

    private fun showSearchOnStart() {

        viewBinding.searchToolbarContainer.searchEditText.setText(queryString)
        viewBinding.searchToolbarContainer.searchEditText.setHint(R.string.search_playlist)
        if (TextUtils.isEmpty(queryString) || TextUtils.isEmpty(viewBinding.searchToolbarContainer.searchEditText.text)) {
            viewBinding.searchToolbarContainer.layoutSearch.translationX = 100f
            viewBinding.searchToolbarContainer.layoutSearch.alpha = 0f
            viewBinding.searchToolbarContainer.layoutSearch.visibility = View.VISIBLE
            viewBinding.searchToolbarContainer.layoutSearch.animate().translationX(0f).alpha(1f).setDuration(200).setInterpolator(DecelerateInterpolator()).start()
        } else {
            viewBinding.searchToolbarContainer.layoutSearch.translationX = 0f
            viewBinding.searchToolbarContainer.layoutSearch.alpha = 1f
            viewBinding.searchToolbarContainer.layoutSearch.visibility = View.VISIBLE
        }
    }

    override fun initData() {
        //初始化列表
        val layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this)
        layoutManager.orientation = androidx.recyclerview.widget.LinearLayoutManager.VERTICAL
        viewBinding.resultListRcv.layoutManager = layoutManager
        viewBinding.resultListRcv.adapter = mAdapter

    }

    override fun initInjector() {
    }

    /**
     * 监听事件
     */
    override fun listener() {
        viewBinding.searchToolbarContainer.clearSearchIv.setOnClickListener {
            queryString = ""
            viewBinding.searchToolbarContainer.searchEditText.setText("")
            viewBinding.searchToolbarContainer.clearSearchIv.visibility = View.GONE
        }
        viewBinding.searchToolbarContainer.searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable) {
                val newText = viewBinding.searchToolbarContainer.searchEditText.text.toString()
                viewBinding.searchToolbarContainer.clearSearchIv.visibility = View.VISIBLE
                searchLocal(newText)
                if (newText.isEmpty()) {
                    viewBinding.searchToolbarContainer.searchEditText.setHint(R.string.search_playlist)
                }
            }
        })

        viewBinding.searchToolbarContainer.searchEditText.setOnEditorActionListener { _, _, event ->
            if (event != null && (event.keyCode == KeyEvent.KEYCODE_ENTER || event.action == EditorInfo.IME_ACTION_SEARCH)) {
                searchLocal(queryString)
                return@setOnEditorActionListener true
            }
            false
        }

        mAdapter.setOnItemClickListener { _, view, position ->
            if (musicList.size <= position) return@setOnItemClickListener
            //播放歌单内搜索结果队列
            PlayManager.play(position, searchResults, queryString.hashCode().toString())
        }
        mAdapter.setOnItemChildClickListener { _, _, position ->
            val music = searchResults[position]
            BottomDialogFragment.newInstance(music, Constants.PLAYLIST_SEARCH_ID).show(this)
        }
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
        }
        return true
    }

    /**
     * 本地歌单搜索索
     *
     * @param query
     */
    private fun searchLocal(query: String?) {
        searchResults.clear()
        if (query != null && query.isNotEmpty()) {
            queryString = query.toLowerCase()
            musicList.forEach {
                if (it.title?.toLowerCase()?.contains(queryString) == true ||
                    it.artist?.toLowerCase()?.contains(queryString) == true ||
                    it.album?.toLowerCase()?.contains(queryString) == true
                ) {
                    searchResults.add(it)
                }
            }
        }
        mAdapter.setNewInstance(searchResults)
    }

    override fun onResume() {
        super.onResume()
        Tools.hideInputView(viewBinding.searchToolbarContainer.searchEditText)
    }
}
