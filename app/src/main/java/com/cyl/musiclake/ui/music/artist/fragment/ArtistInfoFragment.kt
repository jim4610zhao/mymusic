package com.cyl.musiclake.ui.music.artist.fragment

import android.os.Bundle
import android.widget.TextView
import com.cyl.musiclake.R
import com.cyl.musiclake.ui.base.BaseFragment
import com.cyl.musiclake.ui.music.artist.contract.ArtistInfoContract
import com.cyl.musiclake.ui.music.artist.presenter.ArtistInfoPresenter

/**
 * Created by yonglong on 2016/11/30.
 */

class ArtistInfoFragment : BaseFragment<ArtistInfoPresenter>(), ArtistInfoContract.View {

    private lateinit var tvDesc : TextView

    override fun getLayoutId(): Int {
        return R.layout.frag_artist_info
    }

    override fun initViews() {
        super.initViews()
        tvDesc = rootView.findViewById(R.id.tv_desc)
    }

    override fun initInjector() {
        mFragmentComponent.inject(this)
    }

    /**
     * 更新歌手信息
     */
    fun updateArtistDesc(info: String?) {
        tvDesc.text = info ?: ""
    }

    companion object {
        private val TAG = "ArtistInfoFragment"

        fun newInstance(tag: String): ArtistInfoFragment {
            val args = Bundle()
            val fragment = ArtistInfoFragment()
            args.putString("Tag", tag)
            fragment.arguments = args
            return fragment
        }
    }
}
