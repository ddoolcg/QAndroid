package com.lcg.comment.activity.auth

import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.lcg.comment.R
import com.lcg.comment.databinding.ActivityRegisterBinding
import com.lcg.comment.model.Register
import com.lcg.mylibrary.BaseActivity

class RegisterActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = DataBindingUtil.setContentView<ActivityRegisterBinding>(this, R.layout.activity_register)
        binding.register = Register(this).apply { titleText = "注册" }
    }

    companion object {
        fun start(activity: BaseActivity) {
            activity.startActivity(RegisterActivity::class.java)
        }
    }
}
