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
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CheckDuplicateDTO {
        private boolean usable;
    }

}
