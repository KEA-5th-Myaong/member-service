package myaong.popolog.memberservice.service;

import lombok.RequiredArgsConstructor;
import myaong.popolog.memberservice.client.BlogServiceClient;
import myaong.popolog.memberservice.common.exception.ApiCode;
import myaong.popolog.memberservice.common.exception.ApiException;
import myaong.popolog.memberservice.converter.MemberConverter;
import myaong.popolog.memberservice.dto.request.MemberProfileRequest;
import myaong.popolog.memberservice.dto.request.MemberRequest;
import myaong.popolog.memberservice.entity.Member;
import myaong.popolog.memberservice.enums.RequiredInfo;
import myaong.popolog.memberservice.repository.MemberRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberCommandServiceImpl implements MemberCommandService {
    private final MemberQueryService memberQueryService;
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    private final BlogServiceClient blogServiceClient;

    @Override
    public void addAdditionalBasicInfo(Long memberId, MemberRequest.AdditionalBasicInfoDTO request) {
        // Member 조회
        Member findMember = memberQueryService.findMemberByMemberId(memberId);

        // username만 수정
        findMember.updateUsername(request.getUsername());

        // Blog Service로 나머지 데이터 수정 요청, MemberProfile 저장(POST /blog/profile 호출)
        MemberProfileRequest.CreateDTO memberProfileCreateDTO = MemberConverter.toMemberProfileCreateDTO(
                findMember.getId(),
                request.getUsername(),
                request.getName(),
                request.getNickname()
        );
        blogServiceClient.createMemberProfile(memberProfileCreateDTO);

        // MemberProfile 정보는 입력됐으므로 관심 직군 정보만 필요
        findMember.updateRequiredInfo(RequiredInfo.COMPLETED);
    }

    @Override
    public void editBasicInfo(Long memberId, MemberRequest.editBasicInfoDTO request) {
        Member findMember = memberQueryService.findMemberByMemberId(memberId);
        findMember.updateEmail(request.getEmail());
    }

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
    public Member saveMember(Member member) {
        return memberRepository.save(member);
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
