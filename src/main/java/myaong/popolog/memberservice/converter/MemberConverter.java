package myaong.popolog.memberservice.converter;

import myaong.popolog.memberservice.dto.response.MemberResponse;
import myaong.popolog.memberservice.entity.Member;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

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

    public static MemberResponse.BlogInfoDTO toBlogInfoDTO(Member member) {
        return MemberResponse.BlogInfoDTO.builder()
                .memberId(member.getId())
                .nickname(member.getNickname())
                .followingCount(10)
                .followerCount(15)
                .profilePicUrl("https://ibb.co/6vnYLfR")
                .build();

    }

    public static MemberResponse.FollowingDTO toFollowingDTO(Member member) {
        return MemberResponse.FollowingDTO.builder()
                .memberId(member.getId())
                .nickname(member.getNickname())
                .profilePicUrl(member.getProfilePicUrl())
                .isFollowed(true)
                .build();
    }

    public static MemberResponse.FollowingListDTO toFollowingListDTO(List<Member> memberList) {
        List<MemberResponse.FollowingDTO> followingDTOList = memberList.stream()
                .map(member -> MemberConverter.toFollowingDTO(member))
                .collect(Collectors.toList());

        return MemberResponse.FollowingListDTO.builder()
                .lastId(0L)
                .followingDTOList(followingDTOList)
                .build();
    }

    public static MemberResponse.FollowedDTO toFollowedDTO(Member member) {
        return MemberResponse.FollowedDTO.builder()
               .memberId(member.getId())
               .nickname(member.getNickname())
               .profilePicUrl(member.getProfilePicUrl())
               .isFollowed(false)
               .build();
    }

    public static MemberResponse.FollowedListDTO toFollowedListDTO(List<Member> memberList) {
        List<MemberResponse.FollowedDTO> followedDTOList = memberList.stream()
                .map(member -> MemberConverter.toFollowedDTO(member))
                .collect(Collectors.toList());

        return MemberResponse.FollowedListDTO.builder()
                .lastId(0L)
                .followedDTOList(followedDTOList)
                .build();
    }
}
