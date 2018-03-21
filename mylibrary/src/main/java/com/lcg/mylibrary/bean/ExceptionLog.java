package com.lcg.mylibrary.bean;

/**
 * 异常
 *
 * @author lei.chuguang Email:475825657@qq.com
 * @version 1.0
 * @since 2015-1-4 下午4:13:24
 */
public class ExceptionLog {
    private String exceptionname, version, exception, devicename, content;

    public String getExceptionname() {
        return exceptionname;
    }

    public void setExceptionname(String exceptionname) {
        this.exceptionname = exceptionname;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getException() {
        return exception;
    }

    public void setException(String exception) {
        this.exception = exception;
    }

    public String getDevicename() {
        return devicename;
    }

    public void setDevicename(String devicename) {
        this.devicename = devicename;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
