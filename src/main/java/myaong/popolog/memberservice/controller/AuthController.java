package myaong.popolog.memberservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import myaong.popolog.memberservice.common.exception.ApiResponse;
import myaong.popolog.memberservice.dto.response.TokenDTO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    // OAuth2SuccessHandler의 onAuthenticationSuccess에서 redirect되면 여기로 매핑됨
    @Operation(summary = "", description = "")
    @GetMapping("/login/kakao")
    public ApiResponse<TokenDTO> kakaoLogin(@RequestParam(name = "accessToken") String accessToken,
                                            @RequestParam(name = "refreshToken") String refreshToken) {
        return ApiResponse.onSuccess(TokenDTO.of(accessToken, refreshToken));
    }

     // 액세스 토큰 재발급 API, JwtFilter의 redirectReissueURI 메서드에서 여기로 매핑됨
    @GetMapping("/reissue")
    public ApiResponse<TokenDTO> reissueToken(HttpServletRequest request) {
        HttpSession session = request.getSession();
        String accessToken = (String) session.getAttribute("accessToken");
        String refreshToken = (String) session.getAttribute("refreshToken");

        return ApiResponse.onSuccess(TokenDTO.of(accessToken, refreshToken));
    }
}
