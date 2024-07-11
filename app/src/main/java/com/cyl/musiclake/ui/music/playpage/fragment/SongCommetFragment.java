package com.cyl.musiclake.ui.music.playpage.fragment;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cyl.musiclake.R;
import com.cyl.musiclake.ui.base.BaseFragment;


/**
 * Des    :
 * Author : master.
 * Date   : 2018/6/6 .
 */
public class SongCommetFragment extends BaseFragment {

    RecyclerView mCommentRsv;

    @Override
    public int getLayoutId() {
        return R.layout.frag_player_comment;
    }

    @Override
    public void initViews() {
        mCommentRsv = rootView.findViewById(R.id.rv_comment);
        mCommentRsv.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    @Override
    protected void initInjector() {

    }

}
