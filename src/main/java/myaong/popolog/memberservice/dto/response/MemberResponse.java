package myaong.popolog.memberservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import myaong.popolog.memberservice.enums.Permission;

import java.util.ArrayList;
import java.util.List;

public class MemberResponse {
    // 기본 회원 정보
    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BasicInfoDTO {
        private Long memberId;
        private String username;
        private String name;
        private String nickname;
        private String email;
        private String profilePicUrl;
        private Permission permission;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PartialInfoDTO {
        private Long memberId;
        private String username;
        private String nickname;
    }

    // 블로그 접속 시 응답
    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BlogInfoDTO {
        private Long memberId;
        private String nickname;
        private int followingCount;
        private int followerCount;
        private String profilePicUrl;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FollowDTO {
        private boolean following;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FollowingDTO {
        private Long memberId;
        private String nickname;
        private String profilePicUrl;
        private boolean isFollowed;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FollowingListDTO {
        private Long lastId;
        private List<FollowingDTO> followingDTOList = new ArrayList<>();
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FollowedDTO {
        private Long memberId;
        private String nickname;
        private String profilePicUrl;
        private boolean isFollowed;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FollowedListDTO {
        private Long lastId;
        private List<FollowedDTO> followedDTOList = new ArrayList<>();
    }

}
