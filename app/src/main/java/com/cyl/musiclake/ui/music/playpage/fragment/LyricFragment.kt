package com.cyl.musiclake.ui.music.playpage.fragment

import com.cyl.musiclake.R
import com.cyl.musiclake.player.FloatLyricViewManager
import com.cyl.musiclake.player.PlayManager
import com.cyl.musiclake.ui.base.BaseContract
import com.cyl.musiclake.ui.base.BaseFragment
import com.cyl.musiclake.ui.base.BaseLazyFragment
import com.cyl.musiclake.ui.base.BasePresenter
import com.cyl.musiclake.ui.widget.LyricView
import com.cyl.musiclake.utils.SPUtils

class LyricFragment : BaseLazyFragment<BasePresenter<BaseContract.BaseView>>() {

    val lyricTv by lazy { rootView?.findViewById<LyricView>(R.id.lyricShow) }

    override fun getLayoutId(): Int {
        return R.layout.frag_player_lrcview
    }

    override fun initInjector() {
    }
    /**
     *显示歌词
     */
    fun showLyric(lyric: String?, init: Boolean) {
        if (init) {
            //初始化歌词配置
            lyricTv?.setTextSize(SPUtils.getFontSize())
            lyricTv?.setHighLightTextColor(SPUtils.getFontColor())
            lyricTv?.setTouchable(true)
            lyricTv?.setOnPlayerClickListener { progress, _ ->
                PlayManager.seekTo(progress.toInt())
                if (!PlayManager.isPlaying()) {
                    PlayManager.playPause()
                }
            }
        }
        lyricTv?.setLyricContent(lyric)
    }

    fun setCurrentTimeMillis(current: Long = 0) {
        lyricTv?.setCurrentTimeMillis(current)
    }

    override fun onLazyLoad() {
        showLyric(FloatLyricViewManager.lyricInfo, true)
    }

    override fun initViews() {

    }
}