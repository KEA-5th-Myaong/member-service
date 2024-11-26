package myaong.popolog.memberservice.service;

import lombok.RequiredArgsConstructor;
import myaong.popolog.memberservice.common.exception.ApiCode;
import myaong.popolog.memberservice.common.exception.ApiException;
import myaong.popolog.memberservice.converter.AuthConverter;
import myaong.popolog.memberservice.dto.request.AuthRequest;
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
    private final MemberCommandService memberCommandService;

    @Override
    public void signUp(AuthRequest.SignUpDTO request) {
        // 비밀번호 필드와 비밀번호 확인 필드 값이 일치하는지 검증
        if(!request.getPassword().equals(request.getConfirmPassword())) {
            throw new ApiException(ApiCode.UNMATCHED_PASSWORD);
        }

        // 이메일 사용 가능 여부 확인
        if (memberQueryService.existsMemberByEmail(request.getEmail())) {
            throw new ApiException(ApiCode.EMAIL_DUPLICATED);
        }

        // 로그인 아이디 사용 가능 여부 확인
        if (memberQueryService.existsMemberByUsername(request.getUsername())) {
            throw new ApiException(ApiCode.ID_DUPLICATED);
        }

        // 인코딩된 비밀번호로 변경
        String encodedPassword = passwordEncoder.encode(request.getPassword());
        request.setPassword(encodedPassword);

        Member normalMember = AuthConverter.toNormalMember(request);
        memberCommandService.saveMember(normalMember);
    }

}
