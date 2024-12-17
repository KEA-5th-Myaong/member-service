package myaong.popolog.memberservice.converter;

import myaong.popolog.memberservice.dto.request.AuthRequest;
import myaong.popolog.memberservice.dto.response.AuthResponse;
import myaong.popolog.memberservice.entity.Member;
import myaong.popolog.memberservice.enums.Permission;
import myaong.popolog.memberservice.enums.RequiredInfo;
import myaong.popolog.memberservice.enums.SocialType;
import myaong.popolog.memberservice.oauth2.dto.OAuth2Response;
import myaong.popolog.memberservice.oauth2.dto.OAuthUserDTO;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class AuthConverter {

    public static OAuthUserDTO toOAuthUserDTO(String name, Long memberId, String providerId, String permission, Boolean duplicateEmail) {
        return OAuthUserDTO.builder()
                .name(name)
                .memberId(memberId)
                .providerId(providerId)
                .permission(Permission.valueOfLower(permission))
                .duplicateEmail(duplicateEmail)
                .build();
    }

    // 소셜 로그인 회원
    // name , nickname, profilePicUrl은 MemberProfile에만 저장
    public static Member toOAuthMember(OAuth2Response oAuth2Response, RequiredInfo requiredInfo) {
        return Member.builder()
                        .providerId(oAuth2Response.getProviderId())
                        .username(oAuth2Response.getProviderId()) // username 초기 값은 중복되지 않도록 providerId로 설정
                        .password(null)
                        .socialType(SocialType.valueOfLower(oAuth2Response.getProvider()))
                        .email(oAuth2Response.getEmail())
                        .permission(Permission.MEMBER)
                        .countAttempt(0)
                        .unbanDate(LocalDate.now())
                        .requiredInfo(requiredInfo)
                        .build();
    }

    // 일반 회원가입
    public static Member toNormalMember(AuthRequest.SignUpDTO dto) {
        return Member.builder()
                .providerId(null)
                .username(dto.getUsername())
                .password(dto.getPassword())
                .socialType(SocialType.NORMAL)
                .email(dto.getEmail())
                .permission(Permission.MEMBER)
                .countAttempt(0)
                .unbanDate(LocalDate.now())
                .requiredInfo(RequiredInfo.COMPLETED)
                .build();

    }

    public static AuthResponse.LoginDTO toLoginDTO(String accessToken, RequiredInfo requiredInfo) {
        return AuthResponse.LoginDTO.builder()
                .accessToken(accessToken)
                .requiredInfo(requiredInfo)
                .build();
    }

    public static AuthResponse.TokenDTO toTokenDTO(String accessToken, String refreshToken) {
        return AuthResponse.TokenDTO.builder()
               .accessToken(accessToken)
               .refreshToken(refreshToken)
               .build();
    }

    public static AuthResponse.ReissueDTO toReissueDTO(String accessToken) {
        return AuthResponse.ReissueDTO.builder()
               .accessToken(accessToken)
               .build();
    }

}
