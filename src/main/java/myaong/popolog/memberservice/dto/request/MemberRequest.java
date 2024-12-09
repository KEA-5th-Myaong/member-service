package myaong.popolog.memberservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import myaong.popolog.memberservice.common.annotation.EmailFormat;
import myaong.popolog.memberservice.common.annotation.LoginIdFormat;
import myaong.popolog.memberservice.common.annotation.PasswordFormat;

public class MemberRequest {

    @Getter
    public static class AdditionalBasicInfoDTO {
        @NotBlank(message = "이름은 공백일 수 없습니다.")
        private String name;

        private String nickname;

        @LoginIdFormat
        private String username;
    }

    @Getter
    public static class CheckPasswordDTO {
        @Size(min = 8, max = 20, message = "비밀번호는 8~20자 이어야 합니다.")
        private String password;
    }

    @Getter
    public static class UpdatePasswordDTO {
        private String originPassword;

        @Size(min = 8, max = 20, message = "비밀번호는 8~20자 이어야 합니다.")
        private String newPassword;
    }

    @Getter
    public static class editBasicInfoDTO {
        @EmailFormat
        private String email;
    }
}
