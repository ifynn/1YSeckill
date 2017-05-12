package org.appu.model;

import java.io.Serializable;

/**
 * Created by Fynn on 2016/6/8.
 */
public class Result<T> implements Serializable {

    public static final String RESULT_OK = "-1";

    private T data;
    private String code = "0";
    private String message = "";
    private String detail;

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isOk() {
        return code.equals(RESULT_OK);
    }

    public boolean hasData() {
        return data != null;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }
}
