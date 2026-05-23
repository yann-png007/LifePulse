package com.lifepulse.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lifepulse.common.Result;
import com.lifepulse.common.ResultCode;
import com.lifepulse.dto.JwtValidationResult;
import com.lifepulse.util.JwtUtil;
import com.lifepulse.util.UserContext;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;
import java.io.PrintWriter;


/**
 * 登录鉴权拦截器
 * 未登录禁止访问业务接口
 */
@Component
public class LoginInterceptor implements HandlerInterceptor {
    private final JwtUtil jwtUtil;
    private final ObjectMapper objectMapper;

    public LoginInterceptor(JwtUtil jwtUtil, ObjectMapper objectMapper) {
        this.jwtUtil = jwtUtil;
        this.objectMapper = objectMapper;
    }

    /**
     * 在请求处理之前进行调用（Controller方法调用之前）
     *
     * @param request  current HTTP request
     * @param response current HTTP response
     * @param handler  chosen handler to execute, for type and/or instance evaluation
     * @return {@code true} if the execution chain should proceed with the
     * next interceptor or the handler itself. Else, DispatcherServlet assumes
     * that this interceptor has already dealt with the response itself.
     * @throws IOException in case of I/O errors when writing the response
     */
    @Override
    public boolean preHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler) throws IOException {
        String token = request.getHeader("token");

        // 使用新的校验方法
        JwtValidationResult validationResult = jwtUtil.validateToken(token);

        if (validationResult.isValid()) {
            // 校验成功，将用户ID存入上下文
            UserContext.setUserId(validationResult.getUserId());
            return true;
        } else {
            // 校验失败，根据不同错误类型可以做不同处理
            sendErrorResponse(response, validationResult.getErrorType().getResultCode());
            return false;
        }
    }

    /**
     * 向客户端发送统一格式的错误响应
     * @param response HttpServletResponse
     * @param resultCode 错误码
     * @throws IOException
     */
    private void sendErrorResponse(HttpServletResponse response, ResultCode resultCode) throws IOException {
        response.setStatus(resultCode.getCode());
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter writer = response.getWriter();
        // 使用 ObjectMapper 将 Result 对象转为 JSON 字符串
        String jsonResponse = objectMapper.writeValueAsString(Result.error(resultCode));
        writer.write(jsonResponse);
        writer.flush();
        writer.close();
    }

    @Override
    public void afterCompletion(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler, Exception ex) {
        UserContext.clear();
    }
}