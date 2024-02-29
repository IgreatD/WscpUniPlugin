package com.siiri.uniplugin_calendar.models;

import com.alibaba.fastjson.JSONObject;

public class Result<T> {
    private int code;
    private String message;
    private T data;

    public Result(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static <T> Result<T> success(String message, T data) {
        return new Result<>(1, message, data);
    }

    public static <T> Result<T> success(String message) {
        return new Result<T>(1, message, null);
    }

    public static <T> Result<T> success() {
        return new Result<>(1, "success", null);
    }

    public static <T> Result<T> error(String message) {
        return new Result<>(-1, message, null);
    }

    public static <T> Result<T> error(String message, T data) {
        return new Result<T>(-1, message, data);
    }

    public static <T> Result<T> error() {
        return new Result<T>(-1, "error", null);
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public JSONObject toJson() {
        return JSONObject.parseObject(JSONObject.toJSONString(this));
    }

    public boolean isSuccess() {
        return code == 1;
    }
}
