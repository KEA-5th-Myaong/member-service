package myaong.popolog.memberservice.service;

import lombok.RequiredArgsConstructor;
import myaong.popolog.memberservice.common.exception.ApiCode;
import myaong.popolog.memberservice.common.exception.ApiException;
import myaong.popolog.memberservice.converter.MemberConverter;
import myaong.popolog.memberservice.dto.response.MemberResponse;
import myaong.popolog.memberservice.entity.Member;
import myaong.popolog.memberservice.repository.MemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberQueryServiceImpl implements MemberQueryService {
    private final MemberRepository memberRepository;

    @Override
    public MemberResponse.BasicInfoDTO getMemberBasicInfo() {
        Member findMember = findMemberByMemberId(5L);
        return MemberConverter.toBasicInfoDTO(findMember);
    }

    @Override
    public MemberResponse.PartialInfoDTO getMemberPartialInfoByMemberId(Long memberId) {
        Member findMember = findMemberByMemberId(5L);
        return MemberConverter.toPartialInfoDTO(findMember);
    }

    @Override
    public MemberResponse.PartialInfoDTO getMemberPartialInfoByUsername(String username) {
        Member findMember = findMemberByUsername(username);
        return MemberConverter.toPartialInfoDTO(findMember);
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
}
