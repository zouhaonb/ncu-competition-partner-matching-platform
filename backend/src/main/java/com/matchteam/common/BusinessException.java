package com.matchteam.common;

/**
 * 业务异常类
 * 用于在Service层抛出可预见的业务逻辑错误，
 * 由GlobalExceptionHandler统一捕获并返回友好提示
 */
public class BusinessException extends RuntimeException {

    /** 业务错误码 */
    private Integer code;

    public BusinessException(Integer code, String message) {
        super(message);
        this.code = code;
    }

    public BusinessException(String message) {
        super(message);
        this.code = 400;
    }

    public Integer getCode() {
        return code;
    }
}
