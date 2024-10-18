package myaong.popolog.memberservice.service;

import myaong.popolog.memberservice.dto.response.MemberResponse;

public interface MemberQueryService {

    MemberResponse.BasicInfoDTO getMemberBasicInfo();
    MemberResponse.BasicInfoDTO getMemberBasicInfoByMemberId();
}
