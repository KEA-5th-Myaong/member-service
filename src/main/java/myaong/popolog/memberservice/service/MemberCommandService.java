package myaong.popolog.memberservice.service;

import myaong.popolog.memberservice.dto.request.MemberRequest;
import myaong.popolog.memberservice.dto.response.MemberResponse;
import myaong.popolog.memberservice.entity.Member;

public interface MemberCommandService {

    void addAdditionalBasicInfo(Long memberId, MemberRequest.AdditionalBasicInfoDTO request);
    void editBasicInfo(Long memberId, MemberRequest.editBasicInfoDTO request);
    boolean checkPassword(Long memberId, MemberRequest.CheckPasswordDTO request);
    void updatePassword(Long memberId, MemberRequest.UpdatePasswordDTO request);
    Member saveMember(Member member);
    String encodePassword(String rawPassword);
    boolean matches(String rawPassword, String encodedPassword);
}
