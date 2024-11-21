package myaong.popolog.memberservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class MemberProfileResponse {

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProfileInfoDTO {
        private String name;
        private String nickname;
        private String profilePicUrl; // nullable
    }
}
