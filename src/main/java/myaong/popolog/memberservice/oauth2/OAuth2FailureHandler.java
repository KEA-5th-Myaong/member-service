package myaong.popolog.memberservice.oauth2;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import myaong.popolog.memberservice.common.exception.ApiCode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

//@Component
public class OAuth2FailureHandler implements AuthenticationFailureHandler {

    @Value("${redirect-url.login}")
    private String loginPageUrl;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException {
        String finalRedirectionUrl = UriComponentsBuilder.fromUriString(loginPageUrl)
                .queryParam("message", ApiCode.EMAIL_DUPLICATED.getMessage())
                .build().toUriString();

        response.sendRedirect(finalRedirectionUrl);
        response.setStatus(HttpServletResponse.SC_FOUND); // 302 상태 코드 설정
        response.getWriter().flush(); // 응답 종료
        response.getWriter().close(); // 추가적인 요청 처리를 방지

        // TODO: 이 이후로 필터가 동작하지 않아야함..
    }
}
