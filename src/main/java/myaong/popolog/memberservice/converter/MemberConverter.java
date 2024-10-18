package myaong.popolog.memberservice.converter;

import myaong.popolog.memberservice.dto.response.MemberResponse;
import myaong.popolog.memberservice.entity.Member;

public class MemberConverter {
    public static MemberResponse.BasicInfoDTO toBasicInfoDTO(Member member) {
        return MemberResponse.BasicInfoDTO.builder()
                .username(member.getUsername())
                .name(member.getName())
                .email(member.getEmail())
                .profilePicUrl(member.getProfilePicUrl())
                .permission(member.getPermission())
                .build();
    }
}
