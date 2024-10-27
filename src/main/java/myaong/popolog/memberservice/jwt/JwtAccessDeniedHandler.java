package myaong.popolog.memberservice.jwt;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import myaong.popolog.memberservice.common.exception.ApiCode;
import myaong.popolog.memberservice.common.exception.ApiException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

// 권한 미달로 JwtFilter를 통과하지 못한 경우 동작
@Component
public class JwtAccessDeniedHandler implements AccessDeniedHandler {
 
    private static final String EXCEPTION_ACCESS_HANDLER = "/api/exception/access-denied";
 
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException {
        throw new ApiException(ApiCode.ACCESS_DENIED);
    }
}