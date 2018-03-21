package com.lcg.mylibrary.net;

import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONType;
import com.alibaba.fastjson.parser.Feature;
import com.lcg.mylibrary.BaseApplication;
import com.lcg.mylibrary.bean.SimpleData;
import com.lcg.mylibrary.utils.L;
import com.lcg.mylibrary.utils.UIUtils;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * 基本数据处理器，主线程执行on方法。S为成功的数据类型，E为错误的数据类型。
 *
 * @author lei.chuguang Email:475825657@qq.com
 * @version 1.0
 * @since 2016/10/14 14:17
 */

public abstract class BaseDataHandler<S, E> implements DataHandler {
    @Override
    public void start() {
        UIUtils.runInMainThread(new Runnable() {
            @Override
            public void run() {
                onStart();
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
        if (code == 401) {
            BaseApplication.getInstance().gotoLoin(false);
        } else {
            final Object data = parseData(errorData, 1);
            UIUtils.runInMainThread(new Runnable() {
                @Override
                public void run() {
                    onFail(code, (E) data);
                }
            });
        }
    }

    @Override
    public void success(final int code, String successData) {
        L.d("NET", successData);
//        L.file("-------------\n" + successData);
        final Object data = parseData(successData, 0);
        if (data == null) fail(code, successData);
        else UIUtils.runInMainThread(new Runnable() {
            @Override
            public void run() {
                onSuccess(code, (S) data);
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
                Object obj = JSON.parseObject(successData, type2, new Feature[0]);
                Class<? extends Object> class2 = obj.getClass();
                JSONType annotation = class2.getAnnotation(JSONType.class);
                if (annotation != null) {
                    String[] orders = annotation.orders();
                    for (int i = 0; i < orders.length; i++) {
                        Field field2 = class2.getDeclaredField(orders[i]);
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
    public void onStart() {
    }

    /**
     * 网络执行完后调用
     */
    public abstract void onNetFinish();

    /**
     * 网络异常调用
     *
     * @param code > 0服务器返回异常，其他表示连接失败。
     * @param data
     */
    public void onFail(int code, E data) {
        if (code > 0) {
            if (data instanceof String) {
                SimpleData simpleData = null;
                try {
                    simpleData = JSON.parseObject((String) data, SimpleData.class);
                } catch (Exception e) {
                }
                if (simpleData == null) {
                    UIUtils.showToastSafe("服务器繁忙（" + code + "）！");
                } else if (!TextUtils.isEmpty(simpleData.getDetail())) {
                    UIUtils.showToastSafe(simpleData.getDetail());
                } else if (!TextUtils.isEmpty(simpleData.getMsg())) {
                    UIUtils.showToastSafe(simpleData.getMsg());
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
     * @param code
     * @param data
     */
    public abstract void onSuccess(int code, S data);
}
