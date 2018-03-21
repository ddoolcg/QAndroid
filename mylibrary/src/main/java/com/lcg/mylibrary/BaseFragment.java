package com.lcg.mylibrary;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import okhttp3.Call;

/**
 * 所有fragment的基类
 *
 * @author lei.chuguang Email:475825657@qq.com
 * @version 1.0
 * @since 2016/10/13 11:10
 */

public class BaseFragment extends Fragment {
    protected BaseActivity activity;

    @SuppressWarnings("deprecation")
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof BaseActivity)
            this.activity = (BaseActivity) activity;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        TextView textView = new TextView(container.getContext());
        textView.setText("开发中...");
        return textView;
    }

    /**
     * 显示进度对话框
     *
     * @param msg  你想要显示的消息
     * @param call 网络请求的call
     */
    public void showProgressDialog(String msg, Call call) {
        if (activity != null)
            activity.showProgressDialog(msg, call);
    }

    /**
     * 关闭进度对话框
     *
     * @param msg 如果不为空，则只会关闭与之匹配的进度对话框。
     */
    public void dismissProgressDialog(String msg) {
        if (activity != null)
            activity.dismissProgressDialog(msg);
    }

    /**
     * 替换当前Fragment
     *
     * @param fragment
     * @param isBack         是否可回到当前Fragment
     * @param enterAnimation 为0时无动画
     * @param exitAnimation  为0时无动画
     */
    protected void replaceFragment(Fragment fragment, boolean isBack,
                                   int enterAnimation, int exitAnimation) {
        FragmentTransaction transaction = getFragmentManager()
                .beginTransaction();
        transaction.setCustomAnimations(enterAnimation, exitAnimation,
                enterAnimation, exitAnimation);
        transaction.replace(this.getId(), fragment);
        if (isBack) {
            transaction.addToBackStack(null);
        }
        transaction.commit();
    }
}
