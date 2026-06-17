package com.xiaomai.config;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/**
 * 统一返回结果工具类
 */
@NoArgsConstructor
@AllArgsConstructor
public class Result<T> {
    private Integer code;
    private String msg;
    private Object data;

    // ========== 静态成功方法：单独声明静态泛型 <R> ==========
    // 成功 - 带数据
    public static <R> Result<R> success(Object data) {
        return new Result<>(200, "操作成功", data);
    }

    // 成功 - 不带数据
    public static <R> Result<R> success() {
        return new Result<>(200, "操作成功", null);
    }

    // ========== 静态失败方法：单独声明静态泛型 <R> ==========
    public static <R> Result<R> fail(String msg) {
        return new Result<>(500, msg, null);
    }

    // 可选：自定义状态码失败
    public static <R> Result<R> fail(Integer code, String msg) {
        return new Result<>(code, msg, null);
    }

    // Getter & Setter（lombok 自动生成，无则手动补充）
    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}