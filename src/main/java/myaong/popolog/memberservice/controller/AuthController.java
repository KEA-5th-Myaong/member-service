package myaong.popolog.memberservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import myaong.popolog.memberservice.common.annotation.EmailFormat;
import myaong.popolog.memberservice.common.annotation.LoginIdFormat;
import myaong.popolog.memberservice.common.exception.ApiCode;
import myaong.popolog.memberservice.common.exception.ApiResponse;
import myaong.popolog.memberservice.converter.AuthConverter;
import myaong.popolog.memberservice.dto.request.AuthRequest;
import myaong.popolog.memberservice.dto.response.AuthResponse;
import myaong.popolog.memberservice.dto.response.MemberResponse;
import myaong.popolog.memberservice.jwt.JwtUtil;
import myaong.popolog.memberservice.service.AuthService;
import myaong.popolog.memberservice.service.MemberQueryService;
import myaong.popolog.memberservice.service.RedisService;
import myaong.popolog.memberservice.util.CookieUtil;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


import static myaong.popolog.memberservice.common.Constants.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
@Validated
public class AuthController {
    private final CookieUtil cookieUtil;
    private final JwtUtil jwtUtil;
    private final RedisService redisService;
    private final MemberQueryService memberQueryService;
    private final AuthService authService;

    @Operation(summary = "", description = "토큰 재발급")
    @GetMapping("/reissue")
    public ApiResponse<AuthResponse.ReissueDTO> reissueToken(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = cookieUtil.getCookieValue(request.getCookies(), REFRESH_KEY_NAME);
        AuthResponse.TokenDTO tokenDTO = jwtUtil.reissueToken(refreshToken);

        response.addCookie(cookieUtil.createCookie(REFRESH_KEY_NAME, tokenDTO.getRefreshToken(), true));

        return ApiResponse.onSuccess(AuthConverter.toReissueDTO(AUTH_TYPE + tokenDTO.getAccessToken()));
    }



    @Operation(summary = "API 명세서 v0.4 line 3", description = "로그인 아이디 중복 확인")
    @GetMapping("/check-duplicate/username")
    public ApiResponse<MemberResponse.CheckDuplicateDTO> checkDuplicateByUsername(@LoginIdFormat @RequestParam("username") String username) {
        return ApiResponse.onSuccess(memberQueryService.checkDuplicateByUsername(username));
    }

    @Operation(summary = "API 명세서 v0.4 line 4", description = "이메일 중복 확인")
    @GetMapping("/check-duplicate/email")
    public ApiResponse<MemberResponse.CheckDuplicateDTO> checkDuplicateByEmail(@EmailFormat @RequestParam("email") String email) {
        return ApiResponse.onSuccess(memberQueryService.checkDuplicateByEmail(email));
    }

    @Operation(summary = "API 명세서 v0.4 line 7", description = "회원가입")
    @PostMapping("/sign-up")
    public ApiResponse signUp(@RequestBody @Valid AuthRequest.SignUpDTO request) {
        authService.signUp(request);

        return ApiResponse.onSuccess(null);
    }

    @Operation(summary = "API 명세서 v0.4 line 10", description = "로그아웃(refresh token 삭제)")
    @PostMapping("/logout")
    public ApiResponse logout(HttpServletRequest request) {
        String refreshToken = cookieUtil.getCookieValue(request.getCookies(), REFRESH_KEY_NAME);

        String key = String.valueOf(jwtUtil.getMemberId(refreshToken));

        if(redisService.getValues(key) == null) {
            log.error("Redis key '{}' does not exist.", key);
            return ApiResponse.onFailure(ApiCode.INVALID_TOKEN);
        }

        redisService.deleteValues(key);

        return ApiResponse.onSuccess(null);
    }

}
