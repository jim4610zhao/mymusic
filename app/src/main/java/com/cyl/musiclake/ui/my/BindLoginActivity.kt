package com.cyl.musiclake.ui.my

import android.content.Intent
import android.view.View
import com.cyl.musicapi.netease.LoginInfo
import com.cyl.musiclake.R
import com.cyl.musiclake.common.Constants
import com.cyl.musiclake.databinding.ActivityBindLoginBinding
import com.cyl.musiclake.databinding.ActivityPlayerBinding
import com.cyl.musiclake.ui.base.BaseActivity
import com.cyl.musiclake.ui.my.user.User
import com.cyl.musiclake.utils.SPUtils
import com.cyl.musiclake.utils.ToastUtils
import java.util.*

/**
 * 作者：yonglong on 2016/8/11 18:17
 * 邮箱：643872807@qq.com
 * 版本：2.5
 */
class BindLoginActivity : BaseActivity<LoginPresenter>(), LoginContract.View {

    private var username = ""
    private var password = ""
    private var isBinding = false

    private val viewBinding by lazy { ActivityBindLoginBinding.inflate(layoutInflater) }

    override fun getViewBindingView(): View {
        return viewBinding.root
    }
    override fun initView() {
        viewBinding.usernameWrapper.editText!!.setText(SPUtils.getAnyByKey(SPUtils.SP_KEY_USER_NAME, username))
        viewBinding.passwordWrapper.editText!!.setText(SPUtils.getAnyByKey(SPUtils.SP_KEY_PASSWORD, password))
        viewBinding.bindBtn.setOnClickListener {
            loginTo()
        }
    }

    override fun initData() {
    }

    override fun initInjector() {
        mActivityComponent.inject(this)
    }

    //判断密码是否合法
    private fun validatePassword(password: String): Boolean {
        return password.length in 5..18
    }


    /**
     * 点击登录
     */
    private fun loginTo() {
        if (isBinding) return

        username = viewBinding.usernameWrapper.editText!!.text.toString()
        password = viewBinding.passwordWrapper.editText!!.text.toString()
        // TODO: 检查　
        if (!validatePassword(username)) {
            viewBinding.usernameWrapper.isErrorEnabled = false
            viewBinding.passwordWrapper.isErrorEnabled = false
            viewBinding.usernameWrapper.error = "网易云绑定的手机号"
        } else if (!validatePassword(password)) {
            viewBinding.usernameWrapper.isErrorEnabled = false
            viewBinding.passwordWrapper.isErrorEnabled = false

            viewBinding.passwordWrapper.error = "密码需为5~18位的数字或字母"
        } else {
            viewBinding.usernameWrapper.isErrorEnabled = false
            viewBinding.passwordWrapper.isErrorEnabled = false
            //TODO:登录
            viewBinding.progressBar.visibility = View.VISIBLE

            val params = HashMap<String, String>()
            params[Constants.USER_EMAIL] = username
            params[Constants.PASSWORD] = password
            mPresenter!!.bindNetease(username, password)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (mPresenter != null) {
            mPresenter?.onActivityResult(requestCode, resultCode, data)
        }
        super.onActivityResult(requestCode, resultCode, data)
    }


    override fun showLoading() {
        viewBinding.progressBar.visibility = View.VISIBLE
    }

    override fun hideLoading() {
        viewBinding.progressBar.visibility = View.GONE
    }

    override fun showErrorInfo(msg: String) {
        isBinding = false
        ToastUtils.show(msg)
    }

    override fun success(user: User) {
    }

    /**
     * 保存绑定的账号密码
     */
    override fun bindSuccess(loginInfo: LoginInfo?) {
        isBinding = true
        SPUtils.putAnyCommit(SPUtils.SP_KEY_NETEASE_UID, loginInfo?.profile?.userId.toString() + "")
        SPUtils.putAnyCommit(SPUtils.SP_KEY_USER_NAME, username)
        SPUtils.putAnyCommit(SPUtils.SP_KEY_PASSWORD, password)
        ToastUtils.show("登录成功")
        finish()
    }

}
