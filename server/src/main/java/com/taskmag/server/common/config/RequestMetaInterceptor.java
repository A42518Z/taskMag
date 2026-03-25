package com.taskmag.server.common.config;

import com.taskmag.server.common.context.RequestMeta;
import com.taskmag.server.common.context.RequestMetaContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class RequestMetaInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        RequestMetaContext.set(RequestMeta.builder()
                .formKey(request.getHeader("x-FormKey"))
                .stepId(request.getHeader("x-StepId"))
                .build());
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        RequestMetaContext.clear();
    }
}
