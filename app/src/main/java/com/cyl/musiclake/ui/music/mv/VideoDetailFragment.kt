package com.cyl.musiclake.ui.music.mv

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.cyl.musicapi.netease.CommentsItemInfo
import com.cyl.musiclake.R
import com.cyl.musiclake.bean.MvInfoBean
import com.cyl.musiclake.bean.VideoInfoBean
import com.cyl.musiclake.common.Extras
import com.cyl.musiclake.common.NavigationHelper.navigateToArtist
import com.cyl.musiclake.ui.base.BaseFragment
import com.cyl.musiclake.utils.CoverLoader

/**
 * 作者：yonglong
 * 邮箱：643872807@qq.com
 * 版本：2.5
 * 视频播放详情fragment
 */
class VideoDetailFragment : BaseFragment<VideoDetailPresenter>(), VideoDetailContract.View {

    private var mAdapter: SimiMvListAdapter? = null
    private lateinit var videoPlayCountTv:TextView
    private lateinit var videoLikeCountTv:TextView
    private lateinit var shareCountTv:TextView
    private lateinit var videoCollectCountTv:TextView
    private lateinit var commentCountTv:TextView
    private lateinit var videoNameTv:TextView
    private lateinit var videoCreatorTv:TextView
    private lateinit var videoDescTv:TextView
    private lateinit var llView:LinearLayout
    private lateinit var singerView:LinearLayout
    private lateinit var videoCreatorIv:ImageView
    private lateinit var toggleDescIv:ImageView

    override fun loadData() {
        val mVid: String? = arguments?.getString(Extras.VIDEO_VID)
        val type: Int = arguments?.getInt(Extras.VIDEO_TYPE, 1) ?: 1
        mPresenter?.loadMvDetail(mVid, type)
        mPresenter?.loadSimilarMv(mVid, type)
    }

    override fun getLayoutId(): Int {
        return R.layout.frag_mv_detail
    }

    public override fun initViews() {
        videoPlayCountTv = rootView.findViewById(R.id.videoPlayCountTv)
        videoLikeCountTv = rootView.findViewById(R.id.videoLikeCountTv)
        shareCountTv = rootView.findViewById(R.id.shareCountTv)
        videoCollectCountTv = rootView.findViewById(R.id.videoCollectCountTv)
        commentCountTv = rootView.findViewById(R.id.commentCountTv)
        videoNameTv = rootView.findViewById(R.id.videoNameTv)
        videoCreatorTv = rootView.findViewById(R.id.videoCreatorTv)
        llView = rootView.findViewById(R.id.llView)
        videoDescTv = rootView.findViewById(R.id.llView)
        videoCreatorIv = rootView.findViewById(R.id.videoCreatorIv)
        toggleDescIv = rootView.findViewById(R.id.toggleDescIv)
        singerView = rootView.findViewById(R.id.singerView)

        var similarVideoRcv = rootView.findViewById<RecyclerView>(R.id.similarVideoRcv)
        mAdapter = SimiMvListAdapter(mutableListOf())
        val layoutManager = LinearLayoutManager(activity)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        similarVideoRcv.layoutManager = layoutManager
        similarVideoRcv.adapter = mAdapter
        similarVideoRcv.isNestedScrollingEnabled = false
    }

    override fun initInjector() {
        super.initInjector()
        mFragmentComponent.inject(this)
    }

    override fun listener() {
    }

    companion object {
        fun newInstance(vid: String, type: Int = 1): VideoDetailFragment {
            val args = Bundle()
            val fragment = VideoDetailFragment()
            args.putInt(Extras.VIDEO_TYPE, type)
            args.putString(Extras.VIDEO_VID, vid)
            fragment.arguments = args
            return fragment
        }
    }

    override fun showMvComment(mvCommentInfo: List<CommentsItemInfo>) {
    }

    override fun showMvUrlInfo(mvUrl: String?) {
    }

    override fun showMvDetailInfo(mvInfoDetailInfo: VideoInfoBean?) {
        mvInfoDetailInfo?.let { updateMvInfo(it) }
    }

    override fun showVideoInfoList(mvList: MutableList<VideoInfoBean>) {
        mAdapter?.setNewInstance(mvList)
        mAdapter?.setOnItemClickListener { _: BaseQuickAdapter<*, *>?, view: View?, position: Int ->
            val intent = Intent(activity, VideoDetailActivity::class.java)
            intent.putExtra(Extras.VIDEO_VID, mvList[position].vid)
            intent.putExtra(Extras.VIDEO_TYPE, mvList[position].type)
            startActivity(intent)
        }
    }

    override fun showBaiduMvDetailInfo(mvInfoBean: MvInfoBean?) {
    }

    override fun showMvHotComment(mvHotCommentInfo: List<CommentsItemInfo>) {
    }

    private fun updateMvInfo(info: VideoInfoBean) {
        videoPlayCountTv.text = getString(R.string.play_count, info.playCount)
        videoLikeCountTv.text = info.commentCount.toString()
        shareCountTv.text = info.shareCount.toString()
        videoCollectCountTv.text = info.commentCount.toString()
        commentCountTv.text = info.commentCount.toString()
        videoNameTv.text = info.title
        llView.visibility = View.VISIBLE
        if (info.artist.size > 0) {
            videoCreatorTv.text = info.artist[0].name
        }
        CoverLoader.loadImageView(activity, info.coverUrl, videoCreatorIv)
        videoDescTv.text = info.description
//        videoPublishTimeTv.text = getString(R.string.publish_time, info.publishTime)
        singerView.setOnClickListener {
            val artist = info.artist[0]
            activity?.let { it1 -> navigateToArtist(it1, artist, null) }
        }
        //显示
        toggleDescIv?.setOnClickListener {
            if (videoDescTv.visibility == View.VISIBLE) {
                videoDescTv.visibility = View.GONE
                toggleDescIv.setImageResource(R.drawable.ic_arrow_down)
            } else {
                videoDescTv.visibility = View.VISIBLE
                toggleDescIv.setImageResource(R.drawable.ic_arrow_up)
            }
        }
    }


}
