package com.lm.school_bus.config;

import com.lm.school_bus.dto.ApiResponse;
import com.lm.school_bus.exception.BusinessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 捕获业务异常，返回对应的 HTTP 状态码
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Void>> handleBusinessException(BusinessException e) {
        Integer code = e.getCode();
        String message = e.getMessage();
        
        HttpStatus status = HttpStatus.BAD_REQUEST; // 默认 400
        if (code == 401) {
            status = HttpStatus.UNAUTHORIZED;
        } else if (code == 403) {
            status = HttpStatus.FORBIDDEN;
        } else if (code == 404) {
            status = HttpStatus.NOT_FOUND;
        } else if (code == 500) {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
        }
        
        return ResponseEntity
                .status(status)
                .body(ApiResponse.error(code, message));
    }

    /**
     * 捕获其他异常，返回 500
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleException(Exception e) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(500, "服务器内部错误"));
    }
}
