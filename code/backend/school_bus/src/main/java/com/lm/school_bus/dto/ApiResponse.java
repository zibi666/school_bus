package com.lm.school_bus.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * API 统一响应格式
 * 所有接口返回的响应都遵循此格式
 * @param <T> 响应数据的泛型类型
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApiResponse<T> {
    /** HTTP 状态码 (200: 成功, 4xx: 客户端错误, 5xx: 服务器错误) */
    private Integer code;
    
    /** 响应消息文本 */
    private String message;
    
    /** 响应数据内容（成功时返回实际数据，失败时为 null） */
    private T data;

    /**
     * 返回成功响应（仅含数据）
     * @param data 响应数据
     * @return ApiResponse 对象
     */
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(200, "成功", data);
    }
    
    /**
     * 返回成功响应（含自定义消息）
     * @param message 自定义成功消息
     * @param data 响应数据
     * @return ApiResponse 对象
     */
    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(200, message, data);
    }

    /**
     * 返回错误响应
     * @param code HTTP 状态码
     * @param message 错误消息
     * @return ApiResponse 对象，data 为 null
     */
    public static <T> ApiResponse<T> error(Integer code, String message) {
        return new ApiResponse<>(code, message, null);
    }
}
