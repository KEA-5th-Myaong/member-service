package myaong.popolog.memberservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

public class MemberRequest {

    @Getter
    public static class CheckPasswordDTO {
        @NotBlank(message = "비밀번호를 입력해주세요.")
        private String password;
    }

    @Getter
    public static class editBasicInfoDTO {
        @NotBlank(message = "이름을 입력해주세요.")
        private String name;

        @NotBlank(message = "이메일을 입력해주세요.")
        private String email;
    }
}
