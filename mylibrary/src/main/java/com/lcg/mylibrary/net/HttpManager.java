package com.lcg.mylibrary.net;

import android.content.pm.PackageInfo;
import android.os.Environment;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.lcg.mylibrary.utils.L;
import com.lcg.mylibrary.utils.MD5;
import com.lcg.mylibrary.utils.Token;
import com.lcg.mylibrary.utils.TokenUtilKt;
import com.lcg.mylibrary.utils.UIUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

import static okhttp3.Request.Builder;

/**
 * http请求
 *
 * @author lei.chuguang Email:475825657@qq.com
 * @version 1.0
 * @since 2016/10/13 17:48
 */
public class HttpManager {
    /**
     * 输出日志
     */
    public static boolean logcat = false;
    private volatile static HttpManager instance;
    private OkHttpClient client;
    private final HashMap<String, String> header = new HashMap<>();
    private Interceptor mInterceptor;
    /**
     * 服务器时间和boot时间差
     */
    private long timeDifference = 0;

    public static HttpManager getInstance() {
        if (instance == null) {
            HttpManager manager = new HttpManager();
            if (instance == null) {
                instance = manager;
            }
        }
        return instance;
    }

    private HttpManager() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        addDefaultInterceptor(builder);
        client = builder.build();
    }

    /**
     * 添加默认Interceptor
     */
    private void addDefaultInterceptor(OkHttpClient.Builder builder) {
        builder.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(@NonNull Chain chain) throws IOException {
                if (mInterceptor != null) {
                    return mInterceptor.intercept(chain);
                } else {
                    return chain.proceed(chain.request());
                }
            }
        });
    }

    /**
     * 初始化一个默认header
     */
    public void initDefaultHeaders(HashMap<String, String> header) {
        this.header.clear();
        if (header != null)
            this.header.putAll(header);
    }

    /**
     * 统一为请求添加头信息
     */
    private Builder addHeaders() {
        Builder builder = new Builder()
                .addHeader("Content-Type", "application/json")
                .addHeader("os", "android");
        String token = TokenUtilKt.getToken();
        if (!TextUtils.isEmpty(token)) {
            builder.addHeader(Token.INSTANCE.getTOKEN(), token);
        }
        PackageInfo packageInfo = UIUtils.getPackageInfo();
        if (packageInfo != null)
            builder.addHeader("ver", packageInfo.versionCode + "");
        else
            builder.addHeader("ver", "-1");
        for (Map.Entry<String, String> entry : header.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            if (!TextUtils.isEmpty(key))
                if (TextUtils.isEmpty(value)) {
                    builder.removeHeader(key);
                } else {
                    builder.addHeader(key, value);
                }
        }
        return builder;
    }

    /**
     * 无参get请求
     */
    public Call get(String url, ResponseHandler handler) {
        return get(url, null, handler);
    }

    /**
     * get请求
     */
    public Call get(String url, HashMap<String, String> paramsMap, final ResponseHandler handler) {
        if (paramsMap != null) {
            //处理参数
            StringBuilder tempParams = new StringBuilder();
            for (String key : paramsMap.keySet()) {
                String s = paramsMap.get(key);
                if (s != null)
                    try {
                        tempParams.append(String.format("%s=%s", key, URLEncoder.encode(s,
                                "utf-8")));
                        tempParams.append("&");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
            }
            if (tempParams.length() > 0) {
                String start = url.contains("?") ? "&" : "?";
                url = url + start + tempParams.substring(0, tempParams.length() - 1);
            }
        }
        Request request = addHeaders().url(url).build();
        return request(handler, request);
    }

    /**
     * post请求
     */
    public Call post(String url, String content, final ResponseHandler handler) {
        RequestBody requestBody = getRequestBody(url, content);
        Request request = addHeaders().url(url).post(requestBody).build();
        return request(handler, request);
    }

    /**
     * post请求
     */
    public Call post(String url, HashMap<String, String> paramsMap, final ResponseHandler handler) {
        RequestBody formBody = getRequestBody(paramsMap);
        Request request = addHeaders().url(url).post(formBody).build();
        return request(handler, request);
    }

    /**
     * put请求
     */
    public Call put(String url, HashMap<String, String> paramsMap, final ResponseHandler handler) {
        RequestBody formBody = getRequestBody(paramsMap);
        Request request = addHeaders().url(url).put(formBody).build();
        return request(handler, request);
    }

    /**
     * put请求
     */
    public Call put(String url, String content, final ResponseHandler handler) {
        RequestBody requestBody = getRequestBody(url, content);
        Request request = addHeaders().url(url).put(requestBody).build();
        return request(handler, request);
    }

    /**
     * 无参delete请求
     */
    public Call delete(String url, ResponseHandler handler) {
        return delete(url, (HashMap<String, String>) null, handler);
    }

    /**
     * delete请求
     */
    public Call delete(String url, HashMap<String, String> paramsMap, final ResponseHandler handler) {
        if (paramsMap != null) {
            //处理参数
            StringBuilder tempParams = new StringBuilder();
            for (String key : paramsMap.keySet()) {
                String s = paramsMap.get(key);
                if (s == null)
                    s = "";
                try {
                    tempParams.append(String.format("%s=%s", key, URLEncoder.encode(s,
                            "utf-8")));
                    tempParams.append("&");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
            String start = url.contains("?") ? "&" : "?";
            url = url + start + tempParams.substring(0, tempParams.length() - 1);
        }
        Request request = addHeaders().url(url).delete().build();
        return request(handler, request);
    }

    /**
     * delete请求
     */
    public Call delete(String url, String content, final ResponseHandler handler) {
        RequestBody requestBody = getRequestBody(url, content);
        Request request = addHeaders().url(url).delete(requestBody).build();
        return request(handler, request);
    }

    /**
     * 创建一个json RequestBody
     */
    private RequestBody getRequestBody(String url, String content) {
        if (logcat)
            L.d("NET[" + url.hashCode() + "]", "json=" + content);
        return RequestBody.create(MediaType.parse("application/json"), content);
    }

    /**
     * 创建一个FormBody
     */
    private RequestBody getRequestBody(HashMap<String, String> paramsMap) {
        //创建一个FormBody.Builder
        FormBody.Builder builder = new FormBody.Builder();
        if (paramsMap != null) {
            for (String key : paramsMap.keySet()) {
                //追加表单信息
                String s = paramsMap.get(key);
                if (s != null)
                    builder.add(key, s);
            }
        }
        //生成表单实体对象
        return builder.build();
    }

    /**
     * 请求
     */
    private Call request(final ResponseHandler handler, Request request) {
        HttpUrl url = request.url();
        final int id = url.hashCode();
        if (logcat) {
            RequestBody body = request.body();
            String params = "";
            if (body instanceof FormBody) {
                StringBuilder sb = new StringBuilder();
                FormBody form = (FormBody) body;
                int size = form.size();
                for (int i = 0; i < size; i++) {
                    sb.append(form.name(i)).append("=").append(form.value(i)).append(",");
                }
                if (sb.length() > 0) {
                    sb.deleteCharAt(sb.length() - 1);
                    params = "Form{" + sb.toString() + "}";
                }
            }
            L.d("NET[" + id + "]", request.method() + "->" + url
                    + " " + Token.INSTANCE.getTOKEN() + "=" + TokenUtilKt.getToken()
                    + " " + params);
        }
        //
        Call call = client.newCall(request);
        handler.start(call);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if (!call.isCanceled()) {
                    StringWriter info = new StringWriter();
                    PrintWriter printWriter = new PrintWriter(info);
                    e.printStackTrace(printWriter);
                    printWriter.close();
                    String errorData = info.toString();
                    handler.netFinish();
                    handler.fail(-1, errorData);
                    //
                    if (logcat) {
                        L.w("NET[" + id + "]", "IOException->");
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                //获取服务器时间
                long l;
                try {
                    String date = response.header("date");
                    long serverTime = new Date(date).getTime();
                    l = serverTime - SystemClock.elapsedRealtime();
                } catch (Exception e) {
                    l = timeDifference;
                }
                if (l > timeDifference) timeDifference = l;
                //数据
                String data = response.body().string();
                if (call.isCanceled())
                    return;
                handler.netFinish();
                if (response.isSuccessful()) {
                    handler.success(data);
                    //日志输出
                    if (logcat) {
                        L.i("NET[" + id + "]", data);
                    }
                } else {
                    int code = response.code();
                    handler.fail(code, data);
                    //日志输出
                    if (logcat) {
                        L.w("NET[" + id + "]", code + "->" + data);
                    }
                }
            }
        });
        return call;
    }

    /**
     * 文件上传
     */
    public Call upload(String url, @NonNull HashMap<String, Object> paramsMap, final ResponseHandler
            handler) {
        MultipartBody.Builder builder = new MultipartBody.Builder();
        //设置类型
        builder.setType(MultipartBody.FORM);
        //追加参数
        for (String key : paramsMap.keySet()) {
            Object object = paramsMap.get(key);
            if (!(object instanceof File)) {
                builder.addFormDataPart(key, object.toString());
            } else {
                File file = (File) object;
                builder.addFormDataPart(key, file.getName(), RequestBody.create(null, file));
            }
        }
        //创建RequestBody
        RequestBody body = builder.build();
        //
        Request request = addHeaders().url(url).post(body).build();
        return request(handler, request);
    }

    /**
     * 文件下载，文件保存在CacheDir
     */
    public Call download(String url, FileDownloadHandler handler) {
        return download(url, getCacheFile(url), handler);
    }

    /**
     * 文件下载
     */
    public Call download(String url, final File file, final FileDownloadHandler handler) {
        final long startsPoint = file.length();
        final Request request = new Builder()
                .addHeader("RANGE", "bytes=" + startsPoint + "-")
                .addHeader("Connection", "keep-alive")
                .url(url).build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            private void fail(Call call, int code, IOException e) {
                if (!call.isCanceled()) {
                    handler.netFinish();
                    handler.fail(code, e);
                }
            }

            private void success(Call call) {
                if (!call.isCanceled()) {
                    handler.netFinish();
                    handler.success(file);
                }
            }

            @Override
            public void onFailure(Call call, IOException e) {
                fail(call, -1, e);
            }

            @Override
            public void onResponse(Call call, Response response) {
                if (call.isCanceled()) return;
                //处理Response
                int code = response.code();
                ResponseBody body = response.body();
                if (code >= 200 && code < 300 && body != null) {
                    long contentLength = body.contentLength();
                    if (code != 200 || startsPoint != contentLength) {//还有未下载的数据
                        InputStream in = body.byteStream();
                        FileChannel channelOut = null;
                        RandomAccessFile randomAccessFile = null;
                        try {
                            randomAccessFile = new RandomAccessFile(file, "rwd");
                            channelOut = randomAccessFile.getChannel();
                            int len;
                            long bytesWritten = code == 200 ? 0 : startsPoint;
                            long totalLength = code == 200 ? contentLength : startsPoint + contentLength;
                            ByteBuffer buffer = ByteBuffer.allocate(5120);
                            byte[] array = buffer.array();
                            while ((len = in.read(array)) != -1 && !call.isCanceled()) {
                                buffer.position(0);
                                buffer.limit(len);
                                channelOut.write(buffer, bytesWritten);
                                bytesWritten += len;
                                handler.progress(bytesWritten, totalLength);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                            fail(call, -1, e);
                            call.cancel();
                        } finally {
                            try {
                                in.close();
                                if (channelOut != null) {
                                    channelOut.close();
                                }
                                if (randomAccessFile != null) {
                                    randomAccessFile.close();
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    success(call);
                } else if (code == 416) {//已经下载完了
                    success(call);
                } else {
                    fail(call, code, null);
                }
            }
        });
        return call;
    }

    private static final String sPath = "/" + UIUtils.getPackageInfo().packageName + "/.temp/";

    /**
     * 文件下载保存位置
     */
    public File getCacheFile(String url) {
        File esd = Environment.getExternalStorageDirectory();
        String path = esd.getPath() + sPath;
        File f;
        if (url.endsWith(".apk")) {
            String[] split = url.split("/");
            f = new File(path + split[split.length - 1]);
        } else {
            f = new File(path + MD5.GetMD5Code(url));
        }
        f.getParentFile().mkdirs();
        return f;
    }

    public OkHttpClient getClient() {
        return client;
    }

    public void setClient(OkHttpClient client) {
        OkHttpClient.Builder builder = client.newBuilder();
        addDefaultInterceptor(builder);
        this.client = builder.build();
    }

    /**
     * 设置过滤器
     */
    public void setInterceptor(Interceptor mInterceptor) {
        this.mInterceptor = mInterceptor;
    }

    /**
     * 获取服务器时间
     */
    public Date getServerDate() {
        if (timeDifference == 0)
            return new Date();
        else
            return new Date(timeDifference + SystemClock.elapsedRealtime());
    }
}
