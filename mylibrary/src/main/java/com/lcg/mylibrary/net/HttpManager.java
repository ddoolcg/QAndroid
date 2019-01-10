package com.lcg.mylibrary.net;

import android.content.pm.PackageInfo;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.lcg.mylibrary.utils.L;
import com.lcg.mylibrary.utils.MD5;
import com.lcg.mylibrary.utils.TokenUtilKt;
import com.lcg.mylibrary.utils.UIUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.ConnectionPool;
import okhttp3.FormBody;
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

    public static HttpManager getInstance() {
        return ourInstance;
    }

    private HttpManager() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectionPool(new ConnectionPool(3, 5, TimeUnit.MINUTES));
        client = builder.build();
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
            builder.addHeader("token", token);
            builder.addHeader("Authorization", token);
        }
        PackageInfo packageInfo = UIUtils.getPackageInfo();
        if (packageInfo != null)
            builder.addHeader("ver", packageInfo.versionCode + "");
        else
            builder.addHeader("ver", "-1");
        return builder;
    }

    /**
     * 无参get请求
     */
    public Call get(String url, DataHandler handler) {
        return get(url, null, handler);
    }

    /**
     * get请求
     */
    public Call get(String url, HashMap<String, String> paramsMap, final DataHandler handler) {
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
        Request request = addHeaders().url(url).build();
        return request(handler, request);
    }

    /**
     * post请求
     */
    public Call post(String url, String content, final DataHandler handler) {
        L.d("net_put", content);
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), content);
        //
        Request request = addHeaders().url(url).post(requestBody).build();
        return request(handler, request);
    }

    /**
     * post请求
     */
    public Call post(String url, HashMap<String, String> paramsMap, final DataHandler handler) {
        RequestBody formBody = getRequestBody(paramsMap);
        //
        Request request = addHeaders().url(url).post(formBody).build();
        return request(handler, request);
    }

    /**
     * put请求
     */
    public Call put(String url, HashMap<String, String> paramsMap, final DataHandler handler) {
        RequestBody formBody = getRequestBody(paramsMap);
        //
        Request request = addHeaders().url(url).put(formBody).build();
        return request(handler, request);
    }

    /**
     * put请求
     */
    public Call put(String url, String content, final DataHandler handler) {
        L.d("net_put", content);
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), content);
        //
        Request request = addHeaders().url(url).put(requestBody).build();
        return request(handler, request);
    }

    /**
     * 无参delete请求
     */
    public Call delete(String url, DataHandler handler) {
        return delete(url, (HashMap<String, String>) null, handler);
    }

    /**
     * delete请求
     */
    public Call delete(String url, HashMap<String, String> paramsMap, final DataHandler handler) {
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
    public Call delete(String url, String content, final DataHandler handler) {
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
        if (paramsMap != null)
            for (String key : paramsMap.keySet()) {
                //追加表单信息
                String s = paramsMap.get(key);
                if (s != null)
                    builder.add(key, s);
            }
        //生成表单实体对象
        return builder.build();
    }

    /**
     * 请求
     */
    private Call request(final DataHandler handler, Request request) {
        L.d(request.toString());
        Call call = client.newCall(request);
        handler.start();
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                handler.netFinish();
                handler.fail(-1, "");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String data = response.body().string();
                if (call.isCanceled())
                    return;
                handler.netFinish();
                if (response.isSuccessful()) {
                    handler.success(response.code(), data);
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
    public Call upload(String url, @NonNull HashMap<String, Object> paramsMap, final DataHandler
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
    public Call download(String url, FileDownloadHanler handler) {
        return download(url, getCacheFile(url), handler);
    }

    /**
     * 文件下载
     *
     * @param url      URL
     * @param saveFile 保存的文件
     * @param handler  回调处理
     * @return
     */
    public Call download(String url, final File saveFile, final FileDownloadHanler handler) {
        final long startsPoint = saveFile.length();
        final Request request = new Builder()
                .addHeader("RANGE", "bytes=" + startsPoint + "-").url(url).build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                handler.start();
                handler.netFinish();
                handler.fail(-1);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                handler.start();
                if (response.isSuccessful()) {
                    if (call.isCanceled())
                        return;
                    ResponseBody body = response.body();
                    InputStream in = body.byteStream();
                    FileChannel channelOut = null;
                    // 随机访问文件，可以指定断点续传的起始位置
                    RandomAccessFile randomAccessFile = null;
                    try {
                        randomAccessFile = new RandomAccessFile(saveFile, "rwd");
                        //Chanel NIO中的用法，由于RandomAccessFile没有使用缓存策略，直接使用会使得下载速度变慢，亲测缓存下载3.3
                        // 秒的文件，用普通的RandomAccessFile需要20多秒。
                        channelOut = randomAccessFile.getChannel();
                        // 内存映射，直接使用RandomAccessFile，是用其seek方法指定下载的起始位置，使用缓存下载，在这里指定下载位置。
                        long totalLenght = body.contentLength();
                        MappedByteBuffer mappedBuffer = channelOut.map(FileChannel.MapMode
                                .READ_WRITE, startsPoint, totalLenght);
                        byte[] buffer = new byte[1024];
                        int len;
                        long bytesWritten = startsPoint;
                        while ((len = in.read(buffer)) != -1 && !call.isCanceled()) {
                            mappedBuffer.put(buffer, 0, len);
                            bytesWritten += len;
                            handler.progress(bytesWritten, totalLenght, saveFile);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        handler.netFinish();
                        handler.fail(0);
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
                    handler.success(saveFile);
                } else {
                    handler.netFinish();
                    int code = response.code();
                    if (code == 416)
                        handler.success(saveFile);
                    else
                        handler.fail(code);
                }
            }
        });
        return call;
    }

    /**
     * 文件下载保存位置
     */
    private File getCacheFile(String url) {
        File esd = UIUtils.getContext().getCacheDir();
        String path = esd.getPath() + "/";
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
}
