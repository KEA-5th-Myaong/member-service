package myaong.popolog.memberservice.dto.request;

import jakarta.validation.constraints.NotBlank;
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
        @PasswordFormat
        private String password;
    }

    @Getter
    public static class UpdatePasswordDTO {
        @PasswordFormat
        private String originPassword;

        @PasswordFormat
        private String newPassword;
    }

    @Getter
    public static class editBasicInfoDTO {
        @EmailFormat
        private String email;
    }
}
