package com.lm.school_bus.exception;

/**
 * 业务异常类
 * 用于抛出业务逻辑错误，通常返回 4xx HTTP 状态码
 * 由 GlobalExceptionHandler 统一捕获并转换为 ApiResponse
 * 
 * 例如：
 * - 学号或密码错误 -> 401 Unauthorized
 * - 订单不存在 -> 404 Not Found
 * - 状态不正确 -> 400 Bad Request
 */
public class BusinessException extends RuntimeException {
    /** HTTP 状态码（默认 400 Bad Request） */
    private Integer code;

    /**
     * 创建业务异常（默认状态码 400）
     * @param message 错误信息
     */
    public BusinessException(String message) {
        super(message);
        this.code = 400;
    }

    /**
     * 创建业务异常（指定状态码）
     * @param code HTTP 状态码
     * @param message 错误信息
     */
    public BusinessException(Integer code, String message) {
        super(message);
        this.code = code;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }
}
