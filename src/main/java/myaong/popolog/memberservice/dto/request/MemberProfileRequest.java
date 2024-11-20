package myaong.popolog.memberservice.dto.request;

import lombok.Builder;
import lombok.Getter;

public class MemberProfileRequest {

    @Getter
    @Builder
    public static class CreateDTO {
        private Long memberId;
        private String username;
        private String name;
        private String nickname;
    }
}
