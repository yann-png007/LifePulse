package com.lifepulse.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lifepulse.annotation.AdminRequired;
import com.lifepulse.common.Result;
import com.lifepulse.common.ResultCode;
import com.lifepulse.constant.UserRoleConstant;
import com.lifepulse.dto.JwtValidationResult;
import com.lifepulse.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Method;

@Component
public class AdminInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 1. 判断请求的是否是 Controller 的方法
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

        HandlerMethod handlerMethod = (HandlerMethod) handler;
        Method method = handlerMethod.getMethod();

        // 2. 判断方法上是否有 @AdminRequired 注解
        if (!method.isAnnotationPresent(AdminRequired.class)) {
            return true;
        }

        // 3. 如果有注解，则开始校验权限
        String token = request.getHeader("Authorization");
        JwtValidationResult validationResult = jwtUtil.validateToken(token);

        // 4. 校验Token本身是否有效
        if (!validationResult.isValid()) {
            Result<Void> errorResult = Result.error(ResultCode.NOT_LOGIN);
            responseJson(response, errorResult);
            return false;
        }

        // 5. 校验角色是否为管理员
        if (!UserRoleConstant.ADMIN.equals(validationResult.getRole())) {
            Result<Void> errorResult = Result.error(ResultCode.NO_PERMISSION);
            responseJson(response, errorResult);
            return false;
        }

        // 6. 权限校验通过
        return true;
    }

    private void responseJson(HttpServletResponse response, Result<?> result) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(result));
    }
}