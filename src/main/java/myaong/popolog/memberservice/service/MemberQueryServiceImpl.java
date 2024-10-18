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
        Member findMember = memberRepository.findById(5L)
                .orElseThrow(() -> new ApiException(ApiCode.MEMBER_NOT_FOUND));

        return MemberConverter.toBasicInfoDTO(findMember);
    }

    @Override
    public MemberResponse.BasicInfoDTO getMemberBasicInfoByMemberId() {
        return null;
    }
}
