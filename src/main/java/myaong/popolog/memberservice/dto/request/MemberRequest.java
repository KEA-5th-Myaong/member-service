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

        // TODO: 아이디 형식 정규식 검증
        @NotBlank(message = "아이디는 공백일 수 없습니다.")
        @Size(min = 6, max = 12, message = "아이디는 6~12자 이내로 가능합니다.")
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
        @Size(min = 8, max = 20, message = "비밀번호는 8~20자 이내로 가능합니다.")
        private String originPassword;

        // TODO: 새 비밀번호가 규칙에 맞는지 검증(정규식)
        @NotBlank(message = "새 비밀번호를 입력해주세요.")
        @Size(min = 8, max = 20, message = "비밀번호는 8~20자 이내로 가능합니다.")
        private String newPassword;
    }

    @Getter
    public static class editBasicInfoDTO {
        // TODO: 이메일 형식 정규식 검증
        @NotBlank(message = "이메일을 입력해주세요.")
        private String email;
    }
}
