package com.cyl.musiclake.ui.music.edit

import android.view.View
import com.afollestad.materialdialogs.MaterialDialog
import com.cyl.musiclake.R
import com.cyl.musiclake.bean.Music
import com.cyl.musiclake.data.db.DaoLitepal
import com.cyl.musiclake.common.Extras
import com.cyl.musiclake.databinding.ActivityMusicEditBinding
import com.cyl.musiclake.ui.base.BaseActivity
import com.cyl.musiclake.ui.base.BaseContract
import com.cyl.musiclake.ui.base.BasePresenter
import com.cyl.musiclake.utils.Mp3Util
import com.cyl.musiclake.utils.ToastUtils

/**
 * Des    :
 * Author : master.
 * Date   : 2018/8/26 .
 */
class EditMusicActivity : BaseActivity<BasePresenter<BaseContract.BaseView>>() {

    var music: Music? = null
    private val viewBinding by lazy { ActivityMusicEditBinding.inflate(layoutInflater) }

    override fun getViewBindingView(): View {
        return viewBinding.root
    }

    override fun setToolbarTitle(): String {
        return getString(R.string.tag_title)
    }

    override fun initView() {
        viewBinding.saveTagBtn.setOnClickListener {

            MaterialDialog(this).show {
                title(R.string.warning)
                message(R.string.tag_edit_tips)
                positiveButton(R.string.sure) {
                    music?.title = viewBinding.titleInputView.editText?.text.toString()
                    music?.artist = viewBinding.artistInputView.editText?.text.toString()
                    music?.album = viewBinding.albumInputView.editText?.text.toString()
                    music?.let { it1 ->
                        if (updateTagInfo(it1)) {
                            ToastUtils.show(getString(R.string.tag_edit_success))
                        } else {
                            ToastUtils.show(getString(R.string.tag_edit_tips))
                        }
                        this@EditMusicActivity.finish()
                    }
                }
            }
        }
    }

    override fun initData() {
        music = intent.getParcelableExtra(Extras.SONG)
        viewBinding.titleInputView.editText?.setText(music?.title)
        viewBinding.artistInputView.editText?.setText(music?.artist)
        viewBinding.albumInputView.editText?.setText(music?.album)

        music?.uri?.let { Mp3Util.getTagInfo(it) }
    }

    override fun initInjector() {
    }

    private fun updateTagInfo(music: Music): Boolean {
        if (music.uri == null) return false
        val result = Mp3Util.updateTagInfo(music.uri!!, music)
        Mp3Util.getTagInfo(music.uri!!)
        if (result) {
            DaoLitepal.saveOrUpdateMusic(music)
        }
        return result
    }
}