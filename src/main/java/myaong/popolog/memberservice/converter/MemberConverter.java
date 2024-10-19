package myaong.popolog.memberservice.converter;

import myaong.popolog.memberservice.dto.response.MemberResponse;
import myaong.popolog.memberservice.entity.Member;
import org.springframework.stereotype.Component;

@Component
public class MemberConverter {
    public static MemberResponse.BasicInfoDTO toBasicInfoDTO(Member member) {
        return MemberResponse.BasicInfoDTO.builder()
                .username(member.getUsername())
                .name(member.getName())
                .nickname(member.getNickname())
                .email(member.getEmail())
                .profilePicUrl(member.getProfilePicUrl())
                .permission(member.getPermission())
                .build();
    }

    public static MemberResponse.PartialInfoDTO toPartialInfoDTO(Member member) {
        return MemberResponse.PartialInfoDTO.builder()
                .memberId(member.getId())
                .username(member.getUsername())
                .nickname(member.getNickname())
                .build();

    }
}
