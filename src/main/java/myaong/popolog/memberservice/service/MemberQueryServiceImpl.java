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

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberQueryServiceImpl implements MemberQueryService {
    private final MemberRepository memberRepository;

    @Override
    public MemberResponse.BasicInfoDTO getMemberBasicInfo(Long memberId) {
        Member findMember = findMemberByMemberId(memberId);
        return MemberConverter.toBasicInfoDTO(findMember);
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
        Member findMember = memberRepository.findByProviderId(providerId);

        return findMember;
    }
}
