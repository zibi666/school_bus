package com.lm.school_bus.config;

import com.lm.school_bus.dto.ApiResponse;
import com.lm.school_bus.exception.BusinessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理类
 * 
 * 功能说明：
 * 使用 @RestControllerAdvice 注解实现全局异常处理，对所有 Controller 中抛出的异常进行统一拦截和处理
 * 
 * 处理流程：
 * 1. 业务异常（BusinessException）：
 *    - 检查异常中的 code 字段，映射到对应的 HTTP 状态码
 *    - 支持的状态码：400(Bad Request), 401(Unauthorized), 403(Forbidden), 404(Not Found), 500(Internal Error)
 *    - 返回 ApiResponse 格式的错误响应，包含错误码和错误消息
 * 
 * 2. 其他异常（Exception）：
 *    - 捕获所有非业务异常（如数据库异常、网络异常等）
 *    - 统一返回 500 Internal Server Error
 *    - 返回通用错误消息"服务器内部错误"
 * 
 * 异常码映射表：
 * - 400: 请求参数错误、业务规则违反（如订单状态错误、无法重复加入等）
 * - 401: 身份认证失败（如密码错误、账号不存在）
 * - 403: 权限不足（如普通学生无权审核订单、非申请人无权操作等）
 * - 404: 资源不存在（如订单不存在、学生不存在）
 * - 500: 服务器内部错误
 * 
 * 使用示例：
 * ```
 * // 在 Service 中抛出业务异常
 * throw new BusinessException(400, "订单状态错误，无法支付");
 * throw new BusinessException(401, "密码错误");
 * throw new BusinessException(403, "只有申请人才能退票");
 * throw new BusinessException(404, "订单不存在");
 * 
 * // 此处理器会自动捕获并返回对应的 HTTP 状态和 JSON 响应
 * ```
 * 
 * @author School Bus Team
 * @version 1.0
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 业务异常处理方法
     * 
     * 捕获业务层抛出的 BusinessException，将其 code 字段映射到对应的 HTTP 状态码
     * 这样客户端可以通过 HTTP 状态码快速识别错误类型
     * 
     * 异常到 HTTP 状态的映射关系：
     * - BusinessException(401, ...) → 401 Unauthorized
     * - BusinessException(403, ...) → 403 Forbidden
     * - BusinessException(404, ...) → 404 Not Found
     * - BusinessException(500, ...) → 500 Internal Server Error
     * - 其他（包括 400）→ 400 Bad Request（默认）
     * 
     * @param e 捕获的业务异常对象，包含 code（错误码）和 message（错误消息）
     * @return ResponseEntity 包含 HTTP 状态码和 ApiResponse 格式的错误响应体
     *         响应格式：{
     *            "code": 400,
     *            "message": "具体错误消息",
     *            "data": null
     *         }
     * 
     * @throws 不抛出异常，所有异常都被处理并返回错误响应
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Void>> handleBusinessException(BusinessException e) {
        Integer code = e.getCode();
        String message = e.getMessage();
        
        // 根据业务异常的 code 值，映射到对应的 HTTP 状态码
        HttpStatus status = HttpStatus.BAD_REQUEST; // 默认为 400 Bad Request
        
        if (code == 401) {
            // 401 Unauthorized: 身份认证失败
            // 常见场景：密码错误、账号不存在、token 过期
            status = HttpStatus.UNAUTHORIZED;
        } else if (code == 403) {
            // 403 Forbidden: 权限不足、无权操作资源
            // 常见场景：普通学生无权审核订单、加入者无权退票
            status = HttpStatus.FORBIDDEN;
        } else if (code == 404) {
            // 404 Not Found: 请求的资源不存在
            // 常见场景：订单不存在、学生不存在、车辆不存在
            status = HttpStatus.NOT_FOUND;
        } else if (code == 500) {
            // 500 Internal Server Error: 服务器内部错误
            status = HttpStatus.INTERNAL_SERVER_ERROR;
        }
        // 其他 code 值（如 400）使用默认的 BAD_REQUEST 状态
        
        // 构建和返回统一格式的错误响应
        return ResponseEntity
                .status(status)  // 设置 HTTP 状态码
                .body(ApiResponse.error(code, message));  // 设置响应体
    }

    /**
     * 通用异常处理方法
     * 
     * 捕获所有非业务异常，包括：
     * - 数据库异常（如 SQL 语法错误、连接异常）
     * - 网络异常
     * - IO 异常
     * - JSON 解析异常
     * - 其他运行时异常
     * 
     * 这些异常通常表示系统出现意外问题，应返回 500 状态码
     * 为了安全考虑，不会将具体的技术细节（如堆栈跟踪）返回给客户端
     * 而是返回通用的错误消息
     * 
     * @param e 捕获的异常对象（任何 Exception 的子类）
     * @return ResponseEntity 包含 HTTP 500 状态码和通用错误消息的响应
     *         响应格式：{
     *            "code": 500,
     *            "message": "服务器内部错误",
     *            "data": null
     *         }
     * 
     * @throws 不抛出异常，所有异常都被处理并返回 500 错误响应
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleException(Exception e) {
        // 记录异常日志（可选，实际应用中建议记录到日志系统）
        // log.error("未捕获的系统异常", e);
        
        // 返回 500 Internal Server Error 状态码和通用错误消息
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)  // HTTP 500
                .body(ApiResponse.error(500, "服务器内部错误"));  // 通用错误消息
    }
}
