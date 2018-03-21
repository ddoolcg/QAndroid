package com.lcg.mylibrary;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.view.View;

import com.lcg.mylibrary.dialog.ProgressDialog;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;

import okhttp3.Call;

/**
 * 所有activity的基类
 *
 * @author lei.chuguang Email:475825657@qq.com
 * @version 1.0
 * @since 2016/10/13 11:03
 */

public class BaseActivity extends FragmentActivity {
    private ProgressDialog mProgressDialog;
    public static ArrayList<BaseActivity> activities = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activities.add(this);
    }

    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onDestroy() {
        dismissProgressDialog(null);
        activities.remove(this);
        super.onDestroy();
    }

    /**
     * 显示进度对话框
     *
     * @param msg  你想要显示的消息
     * @param call 网络请求的call
     */
    public void showProgressDialog(String msg, Call call) {
        showProgressDialog(msg, call, true);
    }

    /**
     * 显示进度对话框
     *
     * @param msg  你想要显示的消息
     * @param call 网络请求的call
     */
    public void showProgressDialog(String msg, Call call, boolean cancelable) {
        if (mProgressDialog == null)
            mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setCancelable(cancelable);
        mProgressDialog.setCall(call);
        mProgressDialog.show(msg);
    }

    /**
     * 关闭进度对话框
     *
     * @param msg 如果不为空，则只会关闭与之匹配的进度对话框。
     */
    public void dismissProgressDialog(String msg) {
        if (mProgressDialog != null) {
            try {
                mProgressDialog.dismiss(msg);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 去另外一个页面
     */
    public void startActivity(Class<? extends Activity> clazz) {
        startActivity(new Intent(this, clazz));
    }

    public void back(View v) {
        finish();
    }
}
