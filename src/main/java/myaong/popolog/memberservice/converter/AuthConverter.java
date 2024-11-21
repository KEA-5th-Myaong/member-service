package myaong.popolog.memberservice.converter;

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

    public static OAuthUserDTO toOAuthUserDTO(String name, Long memberId, String providerId, String permission) {
        return OAuthUserDTO.builder()
                .name(name)
                .memberId(memberId)
                .providerId(providerId)
                .permission(Permission.valueOfLower(permission))
                .build();
    }

    // name , nickname, profilePicUrl은 MemberProfile에만 저장
    public static Member toMember(OAuth2Response oAuth2Response, RequiredInfo requiredInfo) {
        return Member.builder()
                        .providerId(oAuth2Response.getProviderId())
                        .username(oAuth2Response.getProviderId()) // username 초기 값은 중복되지 않도록 providerId로 설정
                        .password(null)
                        .socialType(SocialType.valueOfLower(oAuth2Response.getProvider()))
                        .name(oAuth2Response.getName())
                        .nickname(oAuth2Response.getName())
                        .email(oAuth2Response.getEmail())
                        .permission(Permission.MEMBER)
                        .countAttempt(0)
                        .unbanDate(LocalDate.now())
                        .requiredInfo(requiredInfo)
                        .build();
    }

}
