package com.lcg.mylibrary.net;

import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONType;
import com.alibaba.fastjson.parser.Feature;
import com.lcg.mylibrary.bean.SimpleData;
import com.lcg.mylibrary.utils.L;
import com.lcg.mylibrary.utils.Token;
import com.lcg.mylibrary.utils.UIUtils;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import okhttp3.Call;

/**
 * 基本数据处理器，主线程执行on方法。S为成功的数据类型，E为错误的数据类型。
 *
 * @author lei.chuguang Email:475825657@qq.com
 * @version 1.0
 * @since 2016/10/14 14:17
 */

public abstract class BaseResponseHandler<S, E> implements ResponseHandler {
    @Override
    public void start(final Call call) {
        UIUtils.runInMainThread(new Runnable() {
            @Override
            public void run() {
                onStart(call);
            }
        });
    }

    @Override
    public void netFinish() {
        UIUtils.runInMainThread(new Runnable() {
            @Override
            public void run() {
                onNetFinish();
            }
        });
    }

    @Override
    public void fail(final int code, String errorData) {
        L.w("NET", "code=" + code + " errorData=" + errorData + "");
        final Object data = parseData(errorData, 1);
        UIUtils.runInMainThread(new Runnable() {
            @Override
            public void run() {
                onFail(code, (E) data);
            }
        });
    }

    @Override
    public void success(String successData) {
        L.d("NET", successData);
//        L.file("-------------\n" + successData);
        final Object data = parseData(successData, 0);
        if (data == null) fail(200, successData);
        else UIUtils.runInMainThread(new Runnable() {
            @Override
            public void run() {
                onSuccess((S) data);
            }
        });
    }

    /**
     * 数据解析
     */
    private Object parseData(String successData, int position) {
        Type type2 = getType(position);
        if (!type2.equals(String.class)) {
            try {
                Object obj;
                if (type2.equals(JSONObject.class)) {
                    obj = JSON.parseObject(successData);
                } else if (type2.equals(JSONArray.class)) {
                    obj = JSON.parseArray(successData);
                } else {
                    obj = JSON.parseObject(successData, type2, new Feature[0]);
                }
                Class<?> class2 = obj.getClass();
                JSONType annotation = class2.getAnnotation(JSONType.class);
                if (annotation != null) {
                    String[] orders = annotation.orders();
                    for (String order : orders) {
                        Field field2 = class2.getDeclaredField(order);
                        field2.setAccessible(true);
                        Object object = field2.get(obj);
                        if (object == null) {
                            throw new Exception("goto Failure");
                        }
                    }
                }
                return obj;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        } else {
            return successData;
        }
    }

    /**
     * 0=成功的数据类型 1=失败的数据类型
     */
    protected Type getType(int position) {
        ParameterizedType type = (ParameterizedType) this.getClass()
                .getGenericSuperclass();
        return type.getActualTypeArguments()[position];
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
     * @param code > 0服务器返回异常，其他表示连接失败。
     * @param data bean对象
     */
    public void onFail(int code, E data) {
        if (code == 401) {
            Function1<Boolean, Unit> subscriber = Token.INSTANCE.getLoginSubscriber();
            if (subscriber != null) {
                subscriber.invoke(false);
                return;
            }
        }
        if (code > 0) {
            if (data instanceof String) {
                SimpleData simpleData = null;
                try {
                    simpleData = JSON.parseObject((String) data, SimpleData.class);
                } catch (Exception e) {
                }
                if (simpleData == null) {
                    UIUtils.showToastSafe("服务器繁忙（" + code + "）！");
                } else if (!TextUtils.isEmpty(simpleData.getMessage())) {
                    UIUtils.showToastSafe(simpleData.getMessage());
                } else if (!TextUtils.isEmpty(simpleData.getDetail())) {
                    UIUtils.showToastSafe(simpleData.getDetail());
                } else {
                    UIUtils.showToastSafe("服务器繁忙（" + code + "）！");
                }
            }
        } else {
            UIUtils.showToastSafe("网络堵塞！");
        }
    }

    /**
     * 服务器返回正确的数据时调用
     *
     * @param data
     */
    public abstract void onSuccess(S data);
}
