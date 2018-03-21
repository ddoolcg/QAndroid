package com.lcg.mylibrary.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.text.Html;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.lcg.mylibrary.R;
import com.lcg.mylibrary.utils.UIUtils;

/**
 * 通知对话框
 * 
 * @since 2014-4-10
 * @author lei.chuguang
 */
public class TipDialog {
	private Dialog dialog;
	private TextView tv_head, tv_tip;
	private Button btn_positive_up, btn_positive_down, btn_negative;

	public TipDialog(Context context) {
		dialog = new Dialog(context, R.style.dialog_style);
		dialog.setCancelable(true);
		dialog.setCanceledOnTouchOutside(true);
		//
		dialog.setContentView(LayoutInflater.from(context).inflate(
				R.layout.dialog_tip, null));
		//
		Window dialogWindow = dialog.getWindow();
		WindowManager.LayoutParams lp = dialogWindow.getAttributes();
		dialogWindow.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM);
		lp.x = 0; // 新位置X坐标
		lp.y = 0; // 新位置Y坐标
		lp.width = UIUtils.getWidth(); // 宽度
		lp.height = WindowManager.LayoutParams.WRAP_CONTENT; // 高度
		dialogWindow.setAttributes(lp);
		dialogWindow.setBackgroundDrawable(new BitmapDrawable());
		//
		tv_head = (TextView) dialog.findViewById(R.id.tv_head);
		tv_tip = (TextView) dialog.findViewById(R.id.tv_tip);
		tv_tip.setMovementMethod(new ScrollingMovementMethod());
		btn_positive_up = (Button) dialog.findViewById(R.id.btn_positive_up);
		btn_positive_down = (Button) dialog
				.findViewById(R.id.btn_positive_down);
		btn_negative = (Button) dialog.findViewById(R.id.btn_negative);
		btn_positive_up.setVisibility(View.GONE);
		btn_positive_down.setVisibility(View.GONE);
		btn_negative.setVisibility(View.GONE);
	}

	public TipDialog setCancelable(boolean flag) {
		dialog.setCancelable(flag);
		return this;
	}

	public TipDialog setMessge(String head, String text) {
		if (TextUtils.isEmpty(head)) {
			tv_head.setVisibility(View.GONE);
		} else {
			tv_head.setVisibility(View.VISIBLE);
			tv_head.setText(head);
		}
		tv_tip.setText(Html.fromHtml(text));
		return this;
	}

	/** 设置提示消息是否居中 */
	public TipDialog setMessgeGravity(boolean isCenter) {
		if (isCenter) {
			tv_tip.setGravity(Gravity.CENTER);
		} else {
			tv_tip.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
		}
		return this;
	}

	public TipDialog setNegative() {
		return setNegative("取消", null, null);
	}

	public TipDialog setNegative(String text, OnClickListener listener) {
		return setNegative(text, listener, null);
	}

	public TipDialog setNegative(String text, OnClickListener listener,
			Object tag) {
		setButton(btn_negative, text, listener, tag);
		return this;
	}

	public TipDialog setPositiveDown(String text, OnClickListener listener) {
		return setPositiveDown(text, listener, null);
	}

	public TipDialog setPositiveDown(String text, OnClickListener listener,
			Object tag) {
		setButton(btn_positive_down, text, listener, tag);
		return this;
	}

	public TipDialog setPositiveUp(String text, OnClickListener listener) {
		return setPositiveUp(text, listener, null);
	}

	public TipDialog setPositiveUp(String text, OnClickListener listener,
			Object tag) {
		setButton(btn_positive_up, text, listener, tag);
		return this;
	}

	/** 设置按钮属性 */
	private void setButton(TextView button, String text,
			OnClickListener listener, Object tag) {
		if (TextUtils.isEmpty(text)) {
			button.setVisibility(View.GONE);
		} else {
			button.setVisibility(View.VISIBLE);
			button.setText(text);
			if (tag != null)
				button.setTag(tag);
			setListener(listener, button);
		}
	}

	/** 为BUTTON注册点击监听 */
	private void setListener(OnClickListener buttonListener, TextView btn) {
		if (buttonListener == null)
			btn.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					dismiss();
				}
			});
		else {
			btn.setOnClickListener(buttonListener);
		}
	}

	public void show() {
		dialog.show();
	}

	public void show(String head, String text) {
		setMessge(head, text);
		show();
	}

	public void dismiss() {
		if (dialog.isShowing())
			dialog.dismiss();
	}
}
