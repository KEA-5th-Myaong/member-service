package myaong.popolog.memberservice.service;

import lombok.RequiredArgsConstructor;
import myaong.popolog.memberservice.client.BlogServiceClient;
import myaong.popolog.memberservice.common.exception.ApiCode;
import myaong.popolog.memberservice.common.exception.ApiException;
import myaong.popolog.memberservice.converter.MemberConverter;
import myaong.popolog.memberservice.dto.response.MemberProfileResponse;
import myaong.popolog.memberservice.dto.response.MemberResponse;
import myaong.popolog.memberservice.entity.Member;
import myaong.popolog.memberservice.repository.MemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberQueryServiceImpl implements MemberQueryService {
    private final MemberRepository memberRepository;

    private final BlogServiceClient blogServiceClient;

    @Override
    public MemberResponse.CheckDuplicateDTO checkDuplicateByUsername(String username) {
        boolean isExist = memberRepository.existsByUsername(username);
        return isExist ? MemberConverter.toCheckDuplicateDTO(false) : MemberConverter.toCheckDuplicateDTO(true);
    }

    @Override
    public MemberResponse.CheckDuplicateDTO checkDuplicateByEmail(String email) {
        boolean isExist = memberRepository.existsByEmail(email);
        return isExist ? MemberConverter.toCheckDuplicateDTO(false) : MemberConverter.toCheckDuplicateDTO(true);
    }

    @Override
    public MemberResponse.BasicInfoDTO getMemberBasicInfo(Long memberId) {
        Member findMember = findMemberByMemberId(memberId);

        // Profile 정보 호출
        MemberProfileResponse.ProfileInfoDTO profileInfoDTO = blogServiceClient.getProfileInfo(memberId);

        return MemberConverter.toBasicInfoDTO(findMember, profileInfoDTO);
    }

    @Override
    public Member findMemberByMemberId(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new ApiException(ApiCode.MEMBER_NOT_FOUND));
    }

    @Override
    public Member findMemberByUsername(String username) {
        Member findMember = memberRepository.findByUsername(username);
        if (findMember == null) {
            throw new ApiException(ApiCode.MEMBER_NOT_FOUND);
        }
        return findMember;
    }

    @Override
    public Member findByProviderId(String providerId) {
        // 예외 처리 다른 곳에서 하므로 이 메서드에서는 할 수 없음
        Member findMember = memberRepository.findByProviderId(providerId);

        return findMember;
    }

    @Override
    public String findPasswordById(Long memberId) {
        return memberRepository.findPasswordById(memberId)
                .orElseThrow(() -> new ApiException(ApiCode.MEMBER_NOT_FOUND));
    }

    @Override
    public boolean existsMemberByUsername(String username) {
        return memberRepository.existsByUsername(username);
    }

    @Override
    public boolean existsMemberByEmail(String email) {
        return memberRepository.existsByEmail(email);
    }
}
