package br.gov.sp.attus.backend.loggings;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class LoggingInterceptor implements HandlerInterceptor {

    private static final Logger log = LoggerFactory.getLogger(
        LoggingInterceptor.class
    );

    @Override
    public boolean preHandle(
        HttpServletRequest request,
        HttpServletResponse response,
        Object handler
    ) {
        request.setAttribute("startTime", System.currentTimeMillis());

        log.info(
            "Incoming request → {} {} | IP: {}",
            request.getMethod(),
            request.getRequestURI(),
            request.getRemoteAddr()
        );

        return true;
    }

    @Override
    public void afterCompletion(
        HttpServletRequest request,
        HttpServletResponse response,
        Object handler,
        Exception ex
    ) {
        long startTime = (Long) request.getAttribute("startTime");
        long duration = System.currentTimeMillis() - startTime;

        log.info(
            "Completed request → {} {} | Status: {} | Duration: {}ms",
            request.getMethod(),
            request.getRequestURI(),
            response.getStatus(),
            duration
        );

        if (ex != null) {
            log.error("Request failed with exception: {}", ex.getMessage());
        }
    }
}
