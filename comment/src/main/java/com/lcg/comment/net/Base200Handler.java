package com.lcg.comment.net;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.Feature;
import com.lcg.mylibrary.net.ResponseHandler;
import com.lcg.mylibrary.utils.L;
import com.lcg.mylibrary.utils.Token;
import com.lcg.mylibrary.utils.UIUtils;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import okhttp3.Call;

/**
 * 正常数据和错误都通过200放回的数据处理器，主线程执行on方法。
 *
 * @author lei.chuguang Email:475825657@qq.com
 * @version 1.0
 * @since 2016/10/14 14:17
 */

public abstract class Base200Handler<D> implements ResponseHandler {
    @Override
    public void start(Call call) {
        UIUtils.runInMainThread(() -> onStart(call));
    }

    @Override
    public void netFinish() {
        UIUtils.runInMainThread(this::onNetFinish);
    }

    @Override
    public void fail(final int code, String errorData) {
        L.w("code=" + code + " errorData=" + errorData + "");
        UIUtils.runInMainThread(() -> onFail(code + "", "服务器错误"));
    }

    @Override
    public void success(String successData) {
        L.d(successData);
        try {
            final JSONObject jsonObject = JSON.parseObject(successData);
            final String code1 = jsonObject.getString("code");
            if (!"0".equals(code1)) {
                UIUtils.runInMainThread(() -> onFail(code1, jsonObject.getString("message")));
            } else {
                Type type2 = getType();
                final Object data;
                if (!type2.equals(String.class)) {
                    String string = jsonObject.getString("data");
                    data = JSON.parseObject(string, type2, new Feature[0]);
                } else {
                    data = jsonObject.getString("data");
                }
                if (data == null)
                    UIUtils.runInMainThread(() -> onFail("0", successData));
                else
                    UIUtils.runInMainThread(() -> onSuccess((D) data));
            }
        } catch (Exception e) {
            e.printStackTrace();
            UIUtils.runInMainThread(() -> onFail("0", "服务器数据异常"));
        }
    }

    /**
     * 获取数据类型；多级泛型，拿不到需要重写。
     */
    protected Type getType() {
        ParameterizedType type = (ParameterizedType) this.getClass()
                .getGenericSuperclass();
        return type.getActualTypeArguments()[0];
    }

    /**
     * 网络连接之前调用
     */
    public void onStart(Call call) {
    }

    /**
     * 网络执行完后调用
     */
    public abstract void onNetFinish();

    /**
     * 网络异常调用
     *
     * @param data
     */
    public void onFail(String code, String data) {
        if ("-1".equals(code)) {
            UIUtils.showToastSafe("网络堵塞！");
        } else if ("E1000".equals(code)) {
            UIUtils.showToastSafe("登陆验证码错误");
        } else if ("E1001".equals(code) || "E1002".equals(code)) {
            Function1<Boolean, Unit> subscriber = Token.INSTANCE.getLoginSubscriber();
            if (subscriber != null)
                subscriber.invoke(false);
        } else {
            UIUtils.showToastSafe(data);
        }
    }

    /**
     * 服务器返回正确的数据时调用
     *
     * @param data 数据
     */
    public abstract void onSuccess(D data);
}
