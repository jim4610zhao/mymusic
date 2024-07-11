package com.cyl.musiclake.ui.chat

import android.view.View
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.cyl.musiclake.MusicApp
import com.cyl.musiclake.R
import com.cyl.musiclake.databinding.ActivityChatDetailBinding
import com.cyl.musiclake.socket.SocketManager
import com.cyl.musiclake.ui.base.BaseActivity


/**
 * 消息中心，收发消息
 */
class ChatDetailActivity : BaseActivity<ChatPresenter>() {


    private var mUserAdapter: OnlineUserListAdapter? = null
    private lateinit var viewBinding : ActivityChatDetailBinding

    override fun setToolbarTitle(): String {
        return getString(R.string.chat_detail)
    }

    override fun getViewBindingView(): View {
        viewBinding = ActivityChatDetailBinding.inflate(layoutInflater)
        return viewBinding.root;
    }

    override fun initView() {
        //用户头像列表
        mUserAdapter = OnlineUserListAdapter(SocketManager.onlineUsers)

        viewBinding.cardChatDetail.usersRsv.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        viewBinding.cardChatDetail.usersRsv.adapter = mUserAdapter
        viewBinding.cardChatDetail.usersRsv.isNestedScrollingEnabled = false
        viewBinding.cardChatDetail.usersRsv.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
        viewBinding.cardChatDetail.onlineUserTv.text = getString(R.string.online_users,SocketManager.onlineUsers.size)
    }

    override fun initData() {

    }

    override fun initInjector() {
        mActivityComponent.inject(this)
    }

}
