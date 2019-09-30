package com.lcg.mylibrary.utils

import java.util.regex.Pattern

/**
 * String扩展
 *
 * @author lei.chuguang Email:475825657@qq.com
 * @version 1.0
 * @since 2017/8/11 21:32
 */
/**为null时转到string*/
infix fun String?.emptyTo(string: String): String = if (this.isNullOrEmpty()) string else this!!

/**
 * 是否包含特殊字符
 */
fun String?.findHardChar(): Boolean {
    if (this.isNullOrEmpty())
        return false
    val regEx = "[`~!@#$%^&*\"()+=|{}':;',\\[\\]" +
            ".<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]|[\ud83c\udc00-\ud83c\udfff]|[\ud83d\udc00" +
            "-\ud83d\udfff]|[\u2600-\u27ff]"
    val p = Pattern.compile(regEx)
    val m = p.matcher(this)
    return m.find()
}

/**判别是否包含Emoji表情*/
fun String?.containsEmoji(): Boolean {
    if (this.isNullOrEmpty())
        return false
    val len = this!!.length
    for (i in 0 until len) {
        if (isEmojiCharacter(this[i])) {
            return true
        }
    }
    return false
}

private fun isEmojiCharacter(codePoint: Char): Boolean {
    return !(codePoint.toInt() == 0x0 ||
            codePoint.toInt() == 0x9 ||
            codePoint.toInt() == 0xA ||
            codePoint.toInt() == 0xD ||
            codePoint.toInt() in 0x20..0xD7FF ||
            codePoint.toInt() in 0xE000..0xFFFD ||
            codePoint.toInt() in 0x10000..0x10FFFF)
}

/**是否是电话*/
fun String?.isPhone(): Boolean {
    val expr = "^[1][3-9]\\d{9}$"
    if (this?.matches(expr.toRegex()) == true) {
        return true
    }
    return false
}