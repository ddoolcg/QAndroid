package com.lcg.mylibrary.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.lcg.mylibrary.ProgressDialogInterface;
import com.lcg.mylibrary.R;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import okhttp3.Call;

/**
 * 进度对话框
 *
 * @author lei.chuguang Email:475825657@qq.com
 * @version 1.0
 * @since 2016/10/21 11:45
 */
public class ProgressDialog extends Dialog implements ProgressDialogInterface {
    private Call mCall;
    private final TextView tvMsg;

    public ProgressDialog(Context context) {
        super(context, R.style.dialog_style);
        if (context instanceof Activity)
            setOwnerActivity((Activity) context);
        View view = LayoutInflater.from(context).inflate(ProgressDialogInterface.Companion.getLayout$mylibrary_debug(), null);
        tvMsg = view.findViewById(R.id.tv_msg);
        setContentView(view);
        setOnCancelListener(dialog -> {
            if (mCall != null) {
                mCall.cancel();
            }
        });
    }

    public void show(String msg) {
        if (tvMsg != null) {
            if (TextUtils.isEmpty(msg)) {
                tvMsg.setVisibility(View.GONE);
            } else {
                tvMsg.setVisibility(View.VISIBLE);
                tvMsg.setText(msg);
            }
        }
        boolean b = true;
        if (Build.VERSION.SDK_INT >= 17) {
            b = getOwnerActivity() == null || !getOwnerActivity().isDestroyed();
        }
        if (!isShowing() && b)
            show();
    }

    public void dismiss(String msg) throws Exception {
        if (tvMsg == null || TextUtils.isEmpty(msg) || tvMsg.getText().toString().equals(msg)) {
            dismiss();
        } else {
            throw new Exception("没有<" + msg + ">消息对话框实体");
        }
    }

    public void setCall(Call call) {
        mCall = call;
    }

    @Override
    public void showProgressDialog(@NotNull String msg, @Nullable Call call, boolean cancelable, boolean canceledOnTouchOutside) {
        setCancelable(cancelable);
        setCanceledOnTouchOutside(canceledOnTouchOutside);
        setCall(call);
        show(msg);
    }

    @Override
    public void dismissProgressDialog(@Nullable String msg) {
        try {
            dismiss(msg);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
