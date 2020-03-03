package com.lcg.mylibrary.net;

import android.content.pm.PackageInfo;
import android.os.Environment;
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
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.ConnectionPool;
import okhttp3.FormBody;
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
    private static HttpManager ourInstance = new HttpManager();
    private OkHttpClient client;
    private final HashMap<String, String> header = new HashMap<>();
    private Interceptor mInterceptor;

    public static HttpManager getInstance() {
        return ourInstance;
    }

    private HttpManager() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectionPool(new ConnectionPool(3, 5, TimeUnit.MINUTES));
        builder.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(@NonNull Chain chain) throws IOException {
                if (mInterceptor != null) {
                    Response intercept = mInterceptor.intercept(chain);
                    mInterceptor = null;
                    return intercept;
                } else {
                    return chain.proceed(chain.request());
                }
            }
        });
        client = builder.build();
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
     *
     * @return
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
            String start = url.contains("?") ? "&" : "?";
            url = url + start + tempParams.substring(0, tempParams.length() - 1);
        }
        Request request = addHeaders().url(url).build();
        return request(handler, request);
    }

    /**
     * post请求
     */
    public Call post(String url, String content, final ResponseHandler handler) {
        L.d("json{" + content + "}");
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), content);
        //
        Request request = addHeaders().url(url).post(requestBody).build();
        return request(handler, request);
    }

    /**
     * post请求
     */
    public Call post(String url, HashMap<String, String> paramsMap, final ResponseHandler handler) {
        RequestBody formBody = getRequestBody(paramsMap);
        //
        Request request = addHeaders().url(url).post(formBody).build();
        return request(handler, request);
    }

    /**
     * put请求
     */
    public Call put(String url, HashMap<String, String> paramsMap, final ResponseHandler handler) {
        RequestBody formBody = getRequestBody(paramsMap);
        //
        Request request = addHeaders().url(url).put(formBody).build();
        return request(handler, request);
    }

    /**
     * put请求
     */
    public Call put(String url, String content, final ResponseHandler handler) {
        L.d("json{" + content + "}");
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), content);
        //
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
        L.d("请求 json{" + content + "}");
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), content);
        Request request = addHeaders().url(url).delete(requestBody).build();
        return request(handler, request);
    }

    /**
     * 创建一个FormBody.Builder
     */
    private RequestBody getRequestBody(HashMap<String, String> paramsMap) {
        //创建一个FormBody.Builder
        FormBody.Builder builder = new FormBody.Builder();
        if (paramsMap != null) {
            L.d("请求 Form{" + paramsMap.toString() + "}");
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
        L.d("Request{method="
                + request.method()
                + ", url="
                + request.url()
                + ", header=("
                + request.headers().toString().replace("\n", ",")
                + ")}");
        Call call = client.newCall(request);
        handler.start(call);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                StringWriter info = new StringWriter();
                PrintWriter printWriter = new PrintWriter(info);
                e.printStackTrace(printWriter);
                printWriter.close();
                handler.netFinish();
                handler.fail(-1, info.toString());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String data = response.body().string();
                if (call.isCanceled())
                    return;
                handler.netFinish();
                if (response.isSuccessful()) {
                    handler.success(data);
                } else {
                    handler.fail(response.code(), data);
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
                .url(url).build();
        Call call = client.newCall(request);
        handler.start();
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                handler.netFinish();
                handler.fail(-1, e);
            }

            @Override
            public void onResponse(Call call, Response response) {
                if (response.isSuccessful()) {
                    if (call.isCanceled())
                        return;
                    ResponseBody body = response.body();
                    InputStream in = body.byteStream();
                    FileChannel channelOut = null;
                    // 随机访问文件，可以指定断点续传的起始位置
                    RandomAccessFile randomAccessFile = null;
                    try {
                        randomAccessFile = new RandomAccessFile(file, "rwd");
                        //Chanel NIO中的用法，由于RandomAccessFile没有使用缓存策略，直接使用会使得下载速度变慢，亲测缓存下载3.3
                        // 秒的文件，用普通的RandomAccessFile需要20多秒。
                        channelOut = randomAccessFile.getChannel();
                        // 内存映射，直接使用RandomAccessFile，是用其seek方法指定下载的起始位置，使用缓存下载，在这里指定下载位置。
                        long totalLength = body.contentLength();
                        MappedByteBuffer mappedBuffer = channelOut.map(FileChannel.MapMode
                                .READ_WRITE, startsPoint, totalLength);
                        byte[] buffer = new byte[1024];
                        int len;
                        long bytesWritten = startsPoint;
                        while ((len = in.read(buffer)) != -1 && !call.isCanceled()) {
                            mappedBuffer.put(buffer, 0, len);
                            bytesWritten += len;
                            handler.progress(bytesWritten, totalLength);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        handler.netFinish();
                        handler.fail(0, e);
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
                    if (call.isCanceled())
                        return;
                    handler.netFinish();
                    handler.success(file);
                } else {
                    handler.netFinish();
                    int code = response.code();
                    if (code == 416)
                        handler.success(file);
                    else
                        handler.fail(code, null);
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

    /**
     * 设置过滤器
     */
    public void setInterceptor(Interceptor mInterceptor) {
        this.mInterceptor = mInterceptor;
    }
}
