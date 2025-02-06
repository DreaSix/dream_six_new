package com.dream.six.config;


import com.dream.six.constants.Constants;
import com.dream.six.entity.UserInfoEntity;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.servlet.HandlerInterceptor;

@Slf4j
public class RequestInterceptor implements HandlerInterceptor {

    private final String[] whiteListUrls;

    public RequestInterceptor(String[] whiteListUrls) {
        this.whiteListUrls = whiteListUrls;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String requestURI = request.getRequestURI();
        if (isWhiteListed(requestURI)) {
            return true;
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserInfoEntity userInfoEntity) {
            log.info("Creating by user: {}", userInfoEntity.getPhoneNumber());

            if (userInfoEntity.getPhoneNumber() != null) {
                MDC.put(Constants.USERNAME_ATTRIBUTE, userInfoEntity.getPhoneNumber());
                MDC.put(Constants.USER_UUID_ATTRIBUTE, String.valueOf(userInfoEntity.getId()));
            }
        }
        return true;
    }


    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        // Clear the MDC after completing the request
        MDC.remove(Constants.USERNAME_ATTRIBUTE);
        MDC.remove(Constants.USER_UUID_ATTRIBUTE);
    }

    private boolean isWhiteListed(String url) {
        for (String whiteListUrl : whiteListUrls) {
            if (url.equals(whiteListUrl)) {
                return true;
            }
        }
        return false;
    }
}
