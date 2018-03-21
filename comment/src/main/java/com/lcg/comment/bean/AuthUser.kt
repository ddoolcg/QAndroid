package com.lcg.comment.bean

import com.alibaba.fastjson.annotation.JSONField
import java.io.Serializable

/**
 * 认证用户
 *
 * @author lei.chuguang Email:475825657@qq.com
 * @version 1.0
 * @since 2018/3/21 15:35
 */
data class AuthUser(var id: Long? = 0,
                    @JSONField(name = "user")
                    var userName: String? = null,
                    var token: String? = null,
                    var headImage: String? = null) : Serializable
