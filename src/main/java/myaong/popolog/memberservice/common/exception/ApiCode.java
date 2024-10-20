package myaong.popolog.memberservice.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ApiCode {

	OK(HttpStatus.OK, "COMMON_2000", "OK"),
	INVALID_DATA(HttpStatus.BAD_REQUEST, "COMMON_4000", "Request data missing or invalid"),
	FORBIDDEN(HttpStatus.FORBIDDEN, "COMMON_4030", "Forbidden"),
	METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "COMMON_4050", "Method not allowed"),
	INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON_5000", "Internal Server Error"),
	DB_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON_5001", "DB Error"),

	RE_AUTHENTICATION_FAILURE(HttpStatus.BAD_REQUEST, "MEMBER_4000", "기존 비밀번호와 일치하지 않습니다."),
	ALREADY_FOLLOWING(HttpStatus.BAD_REQUEST, "MEMBER_4001", "이미 팔로우 중인 회원입니다."),
	INCORRECT_ID(HttpStatus.UNAUTHORIZED, "MEMBER_4010", "아이디가 맞지 않습니다. 다시 시도해주세요."),
	INCORRECT_PASSWORD(HttpStatus.UNAUTHORIZED, "MEMBER_4011", "비밀번호가 맞지 않습니다. 다시 시도해주세요."),	// 재시도 횟수를 함께 전송
	MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "MEMBER_4040", "존재하지 않는 회원입니다."),
	ID_NOT_FOUND(HttpStatus.NOT_FOUND, "MEMBER_4041", "가입하지 않은 아이디입니다."),
	ID_DUPLICATED(HttpStatus.CONFLICT, "MEMBER_4090", "이미 사용된 아이디입니다."),
	EMAIL_DUPLICATED(HttpStatus.CONFLICT, "MEMBER_4091", "이미 사용된 이메일 주소입니다."),
	MEMBER_PERMISSION_CONFLICT(HttpStatus.CONFLICT, "MEMBER_4092", "직원이 아닌 회원에게 관리자 권한을 줄 수 없습니다."),
	ADMIN_PERMISSION_CONFLICT(HttpStatus.CONFLICT, "MEMBER_4093", "관리자 권한이 없는 회원에게 최고 관리자 권한을 줄 수 없습니다."),
	UNSUPPORTED_MEDIA_TYPE(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "MEMBER_4150", "Unsupported media type"),
	;

	private final HttpStatus httpStatus;
	private final String code;
	private final String message;
}
