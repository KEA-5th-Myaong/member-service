package myaong.popolog.memberservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;

public class MemberRequest {

    @Getter
    public static class AdditionalBasicInfoDTO {
        @NotBlank(message = "이름은 공백일 수 없습니다.")
        private String name;

        private String nickname;

        @NotBlank(message = "아이디는 공백일 수 없습니다.")
        @Size(max = 10, message = "아이디는 최대 10자까지만 가능합니다.")
        private String username;
    }

    @Getter
    public static class CheckPasswordDTO {
        @NotBlank(message = "비밀번호를 입력해주세요.")
        private String password;
    }

    @Getter
    public static class UpdatePasswordDTO {
        @NotBlank(message = "기존 비밀번호를 입력해주세요.")
        private String originPassword;

        // TODO: 새 비밀번호가 규칙에 맞는지 검증
        @NotBlank(message = "새 비밀번호를 입력해주세요.")
        private String newPassword;
    }

    @Getter
    public static class editBasicInfoDTO {
        @NotBlank(message = "이메일을 입력해주세요.")
        private String email;
    }
}
