package com.lcg.mylibrary.bean;

import android.text.TextUtils;

/**
 *
 * @author lei.chuguang Email:475825657@qq.com
 * @since 2014-10-28
 * @version 1.0
 */
public class SimpleData {
	private int code = -1;
	private String detail;
	private String msg;
	private String message;
	public String getDetail() {
		if (TextUtils.isEmpty(detail)) {
			return getMessage();
		} else {
			return detail;
		}
	}

	public void setDetail(String detail) {
		this.detail = detail;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getMsg() {
		return getMessage();
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public String getMessage() {
		if (TextUtils.isEmpty(message)) {
			return msg;
		} else {
			return message;
		}
	}

	public void setMessage(String message) {
		this.message = message;
	}
}
