package myaong.popolog.memberservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import myaong.popolog.memberservice.common.annotation.EmailFormat;
import myaong.popolog.memberservice.common.annotation.LoginIdFormat;
import myaong.popolog.memberservice.common.annotation.PasswordFormat;

public class AuthRequest {

    @Getter
    public static class SignUpDTO {
        @NotBlank(message = "이름을 입력해주세요")
        private String name;

        @NotBlank(message = "닉네임을 입력해주세요.")
        private String nickname;

        @EmailFormat
        private String email;

        @LoginIdFormat
        private String username;

        @Setter
        @Size(min = 8, max = 20, message = "비밀번호는 8~20자 이어야 합니다.")
        private String password;

        @PasswordFormat
        private String confirmPassword;
    }
}
