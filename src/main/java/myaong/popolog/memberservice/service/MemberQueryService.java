package myaong.popolog.memberservice.service;

import myaong.popolog.memberservice.dto.response.MemberResponse;
import myaong.popolog.memberservice.entity.Member;

public interface MemberQueryService {

    MemberResponse.BasicInfoDTO getMemberBasicInfo(Long memberId);
    Member findMemberByMemberId(Long memberId);
    Member findMemberByUsername(String username);
    Member findByProviderId(String providerId);
}
