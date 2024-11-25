package myaong.popolog.memberservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import myaong.popolog.memberservice.common.annotation.EmailFormat;
import myaong.popolog.memberservice.common.exception.ApiResponse;
import myaong.popolog.memberservice.dto.request.MemberRequest;
import myaong.popolog.memberservice.jwt.JwtUtil;
import myaong.popolog.memberservice.service.AuthService;
import myaong.popolog.memberservice.service.MemberQueryService;
import myaong.popolog.memberservice.service.RedisService;
import myaong.popolog.memberservice.util.CookieUtil;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
@Validated
public class AuthController {
    private static final String REFRESH_KEY_NAME = "refresh";
    private static final String AUTH_TYPE = "Bearer ";
    private static final String AUTHORIZATION_HEADER = "Authorization";

    private final CookieUtil cookieUtil;
    private final JwtUtil jwtUtil;
    private final RedisService redisService;
    private final MemberQueryService memberQueryService;
    private final AuthService authService;

     // 액세스 토큰 재발급 API, JwtUtil의 redirectReissueURI 메서드에서 여기로 매핑됨
    @GetMapping("/reissue")
    public void reissueToken(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession();
        String accessToken = (String) session.getAttribute("access");
        String refreshToken = (String) session.getAttribute("refresh");

        response.setHeader(AUTHORIZATION_HEADER, AUTH_TYPE + accessToken);
        response.addCookie(cookieUtil.createCookie(REFRESH_KEY_NAME, refreshToken));
        response.setStatus(HttpStatus.OK.value());
    }

    // TODO: 아이디 정규식 검증
    @Operation(summary = "API 명세서 v0.4 line 3", description = "로그인 아이디 중복 확인")
    @GetMapping("/check-duplicate/username")
    public ApiResponse checkDuplicateByUsername(@RequestParam("username") String username) {
        return ApiResponse.onSuccess(memberQueryService.checkDuplicateByUsername(username));
    }

    // TODO: 이메일 정규식 검증
    @Operation(summary = "API 명세서 v0.4 line 4", description = "이메일 중복 확인")
    @GetMapping("/check-duplicate/email")
    public ApiResponse checkDuplicateByEmail(@EmailFormat @RequestParam("email") String email) {
        return ApiResponse.onSuccess(memberQueryService.checkDuplicateByEmail(email));
    }

    // TODO: Access token도 받아서 만료시키기
    @Operation(summary = "API 명세서 v0.4 line 10", description = "로그아웃(refresh token 삭제)")
    @PostMapping("/logout")
    public void logout(HttpServletRequest request, HttpServletResponse response) {
        Cookie[] cookies = request.getCookies();
        if(cookies == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        Optional<Cookie> refreshCookie = Arrays.stream(cookies)
                .filter(cookie -> REFRESH_KEY_NAME.equals(cookie.getName()))
                .findFirst();

        if(!refreshCookie.isPresent()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        String refreshToken = refreshCookie.get().getValue();
        if(refreshToken == null || refreshToken.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        String key = jwtUtil.getProviderId(refreshToken);

        if(redisService.getValues(key) == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        redisService.deleteValues(key);

        Cookie cookie = new Cookie(REFRESH_KEY_NAME, null);
        cookie.setMaxAge(0);
        cookie.setPath("/");

        response.setStatus(HttpServletResponse.SC_OK);
        response.addCookie(cookie);
    }

    // TODO: 비밀번호가 규칙에 맞는지 검증(정규식)
    @Operation(summary = "API 명세서 v0.4 line 16", description = "개인정보 수정 시 비밀번호 일치 확인")
    @PostMapping("/check-password")
    public ApiResponse checkPassword(@RequestHeader("memberId") Long memberId, @RequestBody @Valid MemberRequest.CheckPasswordDTO request) {
        boolean matches = authService.checkPassword(memberId, request);
        return ApiResponse.onSuccess(matches);
    }

    // TODO: 비밀번호가 규칙에 맞는지 검증(정규식)
    @Operation(summary = "API 명세서 v0.4 line 17", description = "비밀번호 변경")
    @PutMapping("/password")
    public ApiResponse updatePassword(@RequestHeader("memberId") Long memberId, @RequestBody @Valid MemberRequest.UpdatePasswordDTO request ) {
        authService.updatePassword(memberId, request);
        return ApiResponse.onSuccess(null);
    }
}
