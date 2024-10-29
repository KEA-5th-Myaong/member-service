package myaong.popolog.memberservice.service;

import myaong.popolog.memberservice.dto.request.MemberRequest;
import myaong.popolog.memberservice.dto.response.MemberResponse;
import myaong.popolog.memberservice.entity.Member;

public interface MemberCommandService {

    MemberResponse.FollowDTO followMember(Long memberId);
    void editBasicInfo(MemberRequest.editBasicInfoDTO request);
    Member saveMember(Member member);
}
