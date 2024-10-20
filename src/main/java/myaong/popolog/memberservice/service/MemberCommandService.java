package myaong.popolog.memberservice.service;

import myaong.popolog.memberservice.dto.response.MemberResponse;

public interface MemberCommandService {

    MemberResponse.FollowDTO followMember(Long memberId);
}
