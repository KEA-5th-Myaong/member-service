package myaong.popolog.memberservice.converter;

import myaong.popolog.memberservice.dto.request.MemberProfileRequest;
import myaong.popolog.memberservice.dto.request.MemberRequest;
import myaong.popolog.memberservice.dto.response.MemberProfileResponse;
import myaong.popolog.memberservice.dto.response.MemberResponse;
import myaong.popolog.memberservice.entity.Member;
import org.springframework.stereotype.Component;

@Component
public class MemberConverter {
    public static MemberResponse.BasicInfoDTO toBasicInfoDTO(Member member, MemberProfileResponse.ProfileInfoDTO dto) {
        return MemberResponse.BasicInfoDTO.builder()
                .memberId(member.getId())
                .username(member.getUsername())
                .name(dto.getName())
                .nickname(dto.getNickname())
                .email(member.getEmail())
                .profilePicUrl(dto.getProfilePicUrl())
                .build();
    }

    public static MemberProfileRequest.CreateDTO toMemberProfileCreateDTO(Long memberId, MemberRequest.AdditionalBasicInfoDTO dto) {
        return MemberProfileRequest.CreateDTO.builder()
                .memberId(memberId)
                .username(dto.getUsername())
                .name(dto.getName())
                .nickname(dto.getNickname())
                .build();
    }

    public static MemberResponse.CheckDuplicateDTO toCheckDuplicateDTO(boolean usable) {
        return MemberResponse.CheckDuplicateDTO.builder()
               .usable(usable)
               .build();
    }
}
