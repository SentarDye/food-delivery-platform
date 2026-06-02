package com.food.common;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

// 统一响应结果，运用空对象模式确保 data 永不为 null
@Data
public class Result<T> {

    private static final Object EMPTY_DATA = new Object() {
        @Override
        public String toString() {
            return "empty";
        }
    };

    private int code;
    private String message;
    private T data;

    private Result() {}

    public static <T> Result<T> success(T data) {
        Result<T> r = new Result<>();
        r.code = 200;
        r.message = "success";
        r.data = data;
        return r;
    }

    /**
     * 成功（无数据）—— 直接使用空对象
     */
    @SuppressWarnings("unchecked")
    public static <T> Result<T> success() {
        Result<T> r = new Result<>();
        r.code = 200;
        r.message = "success";
        r.data = (T) EMPTY_DATA;
        return r;
    }

    /**
     * 失败
     */
    @SuppressWarnings("unchecked")
    public static <T> Result<T> fail(int code, String message) {
        Result<T> r = new Result<>();
        r.code = code;
        r.message = message;
        r.data = (T) EMPTY_DATA;
        return r;
    }

    /**
     * 判断是否包含真实数据
     * 可用在调用方需要区分“成功但无数据”与“成功且有数据”时
     */
    @JsonIgnore
    public boolean hasRealData() {
        return data != EMPTY_DATA;
    }
}
