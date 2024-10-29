package myaong.popolog.memberservice.oauth2.service;

import lombok.RequiredArgsConstructor;
import myaong.popolog.memberservice.entity.Member;
import myaong.popolog.memberservice.enums.Permission;
import myaong.popolog.memberservice.enums.SocialType;
import myaong.popolog.memberservice.oauth2.CustomOAuth2User;
import myaong.popolog.memberservice.oauth2.dto.KakaoResponse;
import myaong.popolog.memberservice.oauth2.dto.OAuth2Response;
import myaong.popolog.memberservice.oauth2.dto.OAuthUserDTO;
import myaong.popolog.memberservice.service.MemberCommandService;
import myaong.popolog.memberservice.service.MemberQueryService;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


//@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
    private final MemberQueryService memberQueryService;
    private final MemberCommandService memberCommandService;

    // 여기서 리턴된 MemberDetails 객체는 사용자 인증 정보를 나타내기 위해 Authentication 객체에 담겨지고,
    // 이 Authentication 객체는 사용자의 인증 상태를 나타내며, SecurityContext에 저장된다고 보면됨
    @Transactional
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        // TODO: inNewMember가 true이면 최초 로그인이므로 로그인 처리와 토큰 발급 완료 후 개인 정보 설정 페이지로 redirect
        boolean isNewMember = false;

        String registrationId = userRequest.getClientRegistration().getRegistrationId();

        Member member = null;
        OAuth2Response oAuth2Response = null;

        if (registrationId.equals("kakao")) {
            oAuth2Response = new KakaoResponse(oAuth2User.getAttributes());
        } else if (registrationId.equals("google")) {
//            oAuth2Response = new GoogleResponse(oAuth2User.getAttributes());
        } else {
            return null;
        }

        //리소스 서버에서 발급 받은 정보로 사용자를 특정할 아이디값을 만듬
        String providerId = oAuth2Response.getProvider() + " " + oAuth2Response.getProviderId();
        Member findMember = memberQueryService.findByProviderId(providerId);

        if (findMember == null) {
            isNewMember = true;

            OAuthUserDTO oAuthUserDTO = toOAuthUserDTO(findMember.getId(), providerId, isNewMember);

            if (registrationId.equals("kakao")) {
                toMember(oAuth2Response);
            } else if (registrationId.equals("google")) {
                toMember(oAuth2Response);
            }

            return new CustomOAuth2User(oAuthUserDTO);
        } else {
            findMember.updateInfo(oAuth2Response.getEmail(), oAuth2Response.getName());
            memberCommandService.saveMember(findMember);

            OAuthUserDTO oAuthUserDTO = toOAuthUserDTO(findMember.getId(), providerId, isNewMember);

            return new CustomOAuth2User(oAuthUserDTO);
        }
    }

    private static OAuthUserDTO toOAuthUserDTO(Long memberId, String providerId, boolean isNewMember) {
        OAuthUserDTO oAuthUserDTO = OAuthUserDTO.builder()
                .memberId(memberId)
                .providerId(providerId)
                .permission(Permission.MEMBER)
                .isNewMember(isNewMember)
                .build();
        return oAuthUserDTO;
    }

    private Member toMember(OAuth2Response oAuth2Response) {
        return memberCommandService.saveMember(
                Member.builder()
                        .username(oAuth2Response.getName())
                        .password(null)
                        .socialType(SocialType.valueOfLower(oAuth2Response.getProvider()))
                        .name(oAuth2Response.getName())
                        .nickname(oAuth2Response.getNickname())
                        .email(oAuth2Response.getEmail())
                        .permission(Permission.MEMBER)
                        .build()
        );
    }
}