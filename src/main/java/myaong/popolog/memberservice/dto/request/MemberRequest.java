package myaong.popolog.memberservice.dto.request;

import lombok.Getter;

public class MemberRequest {

    @Getter
    public static class FollowPageRequestDTO {
        private Long memberId;
        private Long lastId;
    }
}
