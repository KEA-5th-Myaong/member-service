package myaong.popolog.memberservice.service;

import myaong.popolog.memberservice.dto.request.MemberRequest;
import myaong.popolog.memberservice.dto.response.MemberResponse;
import myaong.popolog.memberservice.entity.Member;

public interface MemberCommandService {

    void addAdditionalBasicInfo(Long memberId, MemberRequest.AdditionalBasicInfoDTO request);
    boolean checkPassword(Long memberId, MemberRequest.CheckPasswordDTO request);
    void updatePassword(Long memberId, MemberRequest.UpdatePasswordDTO request);
    void editBasicInfo(Long memberId, MemberRequest.editBasicInfoDTO request);
    Member saveMember(Member member);
}
