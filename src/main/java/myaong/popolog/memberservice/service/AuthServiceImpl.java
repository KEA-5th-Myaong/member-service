package myaong.popolog.memberservice.service;

import lombok.RequiredArgsConstructor;
import myaong.popolog.memberservice.common.exception.ApiCode;
import myaong.popolog.memberservice.common.exception.ApiException;
import myaong.popolog.memberservice.dto.request.MemberRequest;
import myaong.popolog.memberservice.entity.Member;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthServiceImpl implements AuthService {
    // WARN: AuthServiceImpl에서 MemberCommandService를 주입받거나,
    // MemberCommandService에서 AuthServiceImpl를 주입받거나
    // CustomOAuth2UserService에서 AuthServiceImpl를 주입받는다면 에러가 발생할 수 있음
    private final MemberQueryService memberQueryService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public boolean checkPassword(Long memberId, MemberRequest.CheckPasswordDTO request) {
        String findPassword = memberQueryService.findPasswordById(memberId);
        validPassword(request.getPassword(), findPassword);
        return true;
    }

    @Override
    public void updatePassword(Long memberId, MemberRequest.UpdatePasswordDTO request) {
        Member findMember = memberQueryService.findMemberByMemberId(memberId);
        validPassword(request.getOriginPassword(), findMember.getPassword());
        findMember.updatePassword(encodePassword(request.getNewPassword()));
    }

    @Override
    public String encodePassword(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }

    @Override
    public boolean matches(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }

    // 요청 값으로 들어온 비밀번호가 기존의 인코딩된 비밀번호와 일치하는지 확인
    private void validPassword(String rawPassword, String encodedPassword) {
        boolean matches = matches(rawPassword, encodedPassword);

        if (matches == false) {
            throw new ApiException(ApiCode.RE_AUTHENTICATION_FAILURE);
        }
    }
}
