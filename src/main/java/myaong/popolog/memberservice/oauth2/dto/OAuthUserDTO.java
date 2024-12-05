package myaong.popolog.memberservice.oauth2.dto;

import lombok.*;
import myaong.popolog.memberservice.enums.Permission;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OAuthUserDTO {

    // Member에 저장될 정보
    private Long memberId;
    private String providerId;
    private Permission permission;

    // MemberProfile에 저장될 정보
    private String name;

    private Boolean duplicateEmail;
}