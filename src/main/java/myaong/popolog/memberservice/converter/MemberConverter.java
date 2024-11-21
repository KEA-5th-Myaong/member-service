package myaong.popolog.memberservice.converter;

import myaong.popolog.memberservice.dto.request.MemberProfileRequest;
import myaong.popolog.memberservice.dto.request.MemberRequest;
import myaong.popolog.memberservice.dto.response.MemberResponse;
import myaong.popolog.memberservice.entity.Follow;
import myaong.popolog.memberservice.entity.Member;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class MemberConverter {
    public static MemberResponse.BasicInfoDTO toBasicInfoDTO(Member member) {
        return MemberResponse.BasicInfoDTO.builder()
                .memberId(member.getId())
                .username(member.getUsername())
                .name(member.getName())
                .nickname(member.getNickname())
                .email(member.getEmail())
                .profilePicUrl(member.getProfilePicUrl())
                .build();
    }

    public static MemberProfileRequest.CreateDTO toMemberProfileCreateDTO(Long memberId, MemberRequest.AdditionalBasicInfoDTO dto) {
        return MemberProfileRequest.CreateDTO.builder()
                .memberId(memberId)
                .username(dto.getName())
                .name(dto.getName())
                .nickname(dto.getNickname())
                .build();
    }
}
