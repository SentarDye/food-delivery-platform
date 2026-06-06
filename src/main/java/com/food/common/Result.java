// 技术栈：Lombok + Jackson (Spring Boot自带)
package com.food.common;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

/**
 * 统一响应体
 * 注意：data 可能为 null，调用方如需判空可使用 hasRealData() 或 getDataOrNull()
 */
@Data
public class Result<T> {

    private int code;
    private String message;
    private T data;

    private Result() {}

    // 成功工厂
    public static <T> Result<T> success(T data) {
        Result<T> r = new Result<>();
        r.code = 200;
        r.message = "success";
        r.data = data;              // 允许为 null
        return r;
    }

    public static <T> Result<T> success() {
        return success(null);
    }

    // 失败工厂
    public static <T> Result<T> fail(int code, String message) {
        Result<T> r = new Result<>();
        r.code = code;
        r.message = message;
        r.data = null;              // 失败时 data 为 null
        return r;
    }

    /**
     * 判断是否包含真实业务数据
     */
    @JsonIgnore
    public boolean hasRealData() {
        return data != null;
    }

    /**
     * 获取原始 data（可能为 null）
     */
    @JsonIgnore
    public T getDataOrNull() {
        return data;
    }
}