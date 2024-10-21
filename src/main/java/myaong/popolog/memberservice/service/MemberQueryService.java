package myaong.popolog.memberservice.service;

import myaong.popolog.memberservice.dto.response.MemberResponse;
import myaong.popolog.memberservice.entity.Member;

public interface MemberQueryService {

    MemberResponse.BasicInfoDTO getMemberBasicInfo();
    MemberResponse.PartialInfoDTO getMemberPartialInfoByMemberId(Long memberId);
    MemberResponse.PartialInfoDTO getMemberPartialInfoByUsername(String username);
    MemberResponse.BlogInfoDTO getMemberBlogInfo(Long memberId);
    MemberResponse.FollowingListDTO getMemberFollowingList(Long memberId, Long lastId);
    MemberResponse.FollowedListDTO getMemberFollowedList(Long memberId, Long lastId);
    Member findMemberByMemberId(Long memberId);
    Member findMemberByUsername(String username);
}
