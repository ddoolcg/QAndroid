package com.lcg.expressbus

import android.os.Bundle
import com.lcg.comment.activity.auth.LoginActivity
import com.lcg.mylibrary.BaseActivity
import com.lcg.mylibrary.utils.UIUtils

class WelcomeActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)
        UIUtils.postDelayed(3000) {
            LoginActivity.start(this)
            finish()
        }
    }
}
