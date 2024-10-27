package myaong.popolog.memberservice.oauth2.service;

import lombok.RequiredArgsConstructor;
import myaong.popolog.memberservice.entity.Member;
import myaong.popolog.memberservice.enums.Permission;
import myaong.popolog.memberservice.enums.SocialType;
import myaong.popolog.memberservice.oauth2.KakaoMemberDetails;
import myaong.popolog.memberservice.oauth2.KakaoUserInfo;
import myaong.popolog.memberservice.repository.MemberRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class KakaoMemberDetailsService extends DefaultOAuth2UserService {
    private static final String PREFIX = "낯선 ";
 
    private final MemberRepository memberRepository;
 
    @Transactional
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        // TODO: inNewMember가 true이면 최초 로그인이므로 로그인 처리와 토큰 발급 완료 후 개인 정보 설정 페이지로 redirect
        boolean isNewMember = false;
        String registrationId = userRequest.getClientRegistration().getRegistrationId();

        Member member = null;
        if (registrationId.equals("kakao")) {
            KakaoUserInfo kakaoUserInfo = new KakaoUserInfo(oAuth2User.getAttributes());
            member = findMemberByEmail(SocialType.KAKAO, kakaoUserInfo.getEmail());
        } else if (registrationId.equals("google")) {
        }

        SimpleGrantedAuthority authority = new SimpleGrantedAuthority(member.getPermission().name());

        // 여기서 리턴된 MemberDetails 객체는 사용자 인증 정보를 나타내기 위해 Authentication 객체에 담겨지고,
        // 이 Authentication 객체는 사용자의 인증 상태를 나타내며, SecurityContext에 저장된다고 보면됨
        return new KakaoMemberDetails(String.valueOf(member.getEmail()),
                Collections.singletonList(authority),
                oAuth2User.getAttributes());
    }

    // 회원이 없는 경우에는 email 필드만 카카오 로그인 계정으로 등록, 나머지 필드들은 요구사항에 맞게 기본적인 정보들을 Builder로 생성하여 리포지토리에 저장
    private Member findMemberByEmail(SocialType socialType, String email) {
        return memberRepository.findByEmail(email)
                .orElseGet(() ->
                        memberRepository.save(
                                Member.builder()
                                        .socialType(socialType)
                                        .nickname(PREFIX)
                                        .email(email)
                                        .permission(Permission.MEMBER)
//                                        .gender(Gender.NONE)
//                                        .updateAgeCount(0)
//                                        .updateGenderCount(0)
                                        .build()
                        ));
    }
}