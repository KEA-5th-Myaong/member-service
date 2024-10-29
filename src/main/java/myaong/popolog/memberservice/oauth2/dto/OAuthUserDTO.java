package myaong.popolog.memberservice.oauth2.dto;

import lombok.*;
import myaong.popolog.memberservice.enums.Permission;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OAuthUserDTO {

    private String name;
    private Long memberId;
    // 서버에서 발급받는 아이디
    private String providerId;
    private Permission permission;
    private String profilePicUrl;
    private boolean isNewMember;
}