package com.cyl.musiclake.ui.music.playpage

import android.graphics.Bitmap
import android.graphics.drawable.AnimationDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import com.cyl.musiclake.R
import com.cyl.musiclake.bean.Music
import com.cyl.musiclake.common.TransitionAnimationUtils
import com.cyl.musiclake.databinding.ActivityLockScreenBinding
import com.cyl.musiclake.databinding.FragPlaylistDetailBinding
import com.cyl.musiclake.player.PlayManager
import com.cyl.musiclake.ui.base.BaseActivity
import com.cyl.musiclake.utils.FormatUtil
import java.text.SimpleDateFormat
import java.util.*


/**
 * Des    : 播放界面
 * Author : master.
 * Date   : 2018/5/19 .
 */
class LockScreenPlayerActivity : BaseActivity<PlayPresenter>(), PlayContract.View {

    private val viewBinding by lazy { ActivityLockScreenBinding.inflate(layoutInflater) }

    override fun getViewBindingView(): View {
        return viewBinding.root
    }
    override fun setPlayingBitmap(albumArt: Bitmap?) {
    }

    override fun setPlayingBg(albumArt: Drawable?, isInit: Boolean?) {
        if (isInit != null && isInit) {
            viewBinding.playerBackgroundIv.setImageDrawable(albumArt)
        } else {
            //加载背景图过度
            TransitionAnimationUtils.startChangeAnimation(viewBinding.playerBackgroundIv, albumArt)
        }
    }

    override fun updatePlayStatus(isPlaying: Boolean) {
        viewBinding.playPauseIv.setImageResource(if (isPlaying) R.drawable.ic_play else R.drawable.ic_pause);
    }

    override fun initView() {
        viewBinding.prevIv.setOnClickListener {
            PlayManager.prev()
        }
        viewBinding.playPauseIv.setOnClickListener {
            PlayManager.playPause()
        }
        viewBinding.nextIv.setOnClickListener {
            PlayManager.next()
        }
    }

    override fun initInjector() {
        mActivityComponent.inject(this)
    }


    override fun updateProgress(progress: Long, max: Long) {
        //获取当前时间
        viewBinding.timeTv.text = simpleDateFormat?.format(Date(System.currentTimeMillis()))
    }

    override fun showNowPlaying(music: Music?) {
        viewBinding.songNameTv.text = music?.title
        viewBinding.singerTv.text = music?.artist
    }

    private var animationDrawable: AnimationDrawable? = null
    private var simpleDateFormat: SimpleDateFormat? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        //屏蔽系统的锁屏界面，将此activity设置为锁屏界面
        window.setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        window.setFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD, WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true)
        }
        super.onCreate(savedInstanceState)
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
    }

    /**
     * 禁用返回键
     */
    override fun onBackPressed() {
    }

    override fun initData() {
        //获取当前时间
        viewBinding.dateTv.text = FormatUtil.formatDate(Date(), "EEEE, dd MMMM").toString()
        simpleDateFormat = SimpleDateFormat("HH:mm")
        viewBinding.timeTv.text = simpleDateFormat?.format(Date(System.currentTimeMillis()))
    }

    override fun onPause() {
        super.onPause()
        animationDrawable?.stop()
    }

    override fun onResume() {
        super.onResume()
        animationDrawable?.start()
    }


}
