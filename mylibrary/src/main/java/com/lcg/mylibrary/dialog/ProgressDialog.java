package com.lcg.mylibrary.dialog;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.databinding.DataBindingUtil;
import android.os.Build;
import android.text.TextUtils;
import android.view.LayoutInflater;

import com.lcg.mylibrary.R;
import com.lcg.mylibrary.databinding.DialogLoadingBinding;

import okhttp3.Call;

/**
 * 进度对话框
 *
 * @author lei.chuguang Email:475825657@qq.com
 * @version 1.0
 * @since 2016/10/21 11:45
 */

public class ProgressDialog extends Dialog {
    private DialogLoadingBinding mBinding;
    private Call mCall;

    public ProgressDialog(Context context) {
        super(context, R.style.dialog_style);
        if (context instanceof Activity)
            setOwnerActivity((Activity) context);
        mBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.dialog_loading, null, false);
        setContentView(mBinding.getRoot());
        setOnCancelListener(new OnCancelListener() {

            @Override
            public void onCancel(DialogInterface dialog) {
                if (mCall != null) {
                    mCall.cancel();
                }
            }
        });
    }

    @SuppressLint("NewApi")
    public void show(String msg) {
        mBinding.setMsg(msg);
        boolean b = true;
        if (Build.VERSION.SDK_INT >= 17) {
            b = getOwnerActivity() == null || !getOwnerActivity().isDestroyed();
        }
        if (!isShowing() && b)
            show();
    }

    public void dismiss(String msg) throws Exception {
        if (TextUtils.isEmpty(msg) || mBinding.tvMsg.getText().toString().equals(msg)) {
            dismiss();
        } else {
            throw new Exception("没有<" + msg + ">消息对话框实体");
        }
    }

    public void setCall(Call call) {
        mCall = call;
    }
}
