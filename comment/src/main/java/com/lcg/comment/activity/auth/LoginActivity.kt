package com.lcg.comment.activity.auth

import android.os.Bundle
import com.lcg.comment.R
import com.lcg.comment.databinding.ActivityLoginBinding
import com.lcg.comment.model.Login
import com.lcg.mylibrary.BaseActivity
import com.lcg.mylibrary.utils.setContentViewBinding

class LoginActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = setContentViewBinding<ActivityLoginBinding>(R.layout.activity_login)
        val login = Login(this).apply {
            titleText = "登陆"
            isShowBack = false
        }
        binding.login = login
    }

    companion object {
        fun start(activity: BaseActivity) {
            activity.startActivity(LoginActivity::class.java)
        }
    }
}
