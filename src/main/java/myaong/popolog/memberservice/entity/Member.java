package myaong.popolog.memberservice.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import myaong.popolog.memberservice.enums.Permission;
import myaong.popolog.memberservice.enums.RequiredInfo;
import myaong.popolog.memberservice.enums.SocialType;

import java.time.LocalDate;

@Entity
@Table(name = "`member`")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "member_id")
	private Long id;

	// 외부 서비스 id
	@Column(name = "provider_id", unique = true)
	private String providerId;

	// 로그인 아이디
	@Column(name = "username", unique = true, updatable = false)
	private String username;

	@Column(name = "password")
	private String password;

	// 소셜 로그인 타입
	@Enumerated(EnumType.STRING)
	@Column(name = "social_type", nullable = false, updatable = false)
	private SocialType socialType;

	@Column(name = "email", nullable = false, unique = true)
	private String email;

	// 권한
	@Enumerated(EnumType.STRING)
	@Column(name = "permission", nullable = false)
	private Permission permission;

	// 로그인 시도 횟수. 로그인 성공 시 초기
	@Column(name = "count_attempt", nullable = false)
	private Integer countAttempt;

	// 정지 해제일. (오늘 날짜 < 정지 해제일)이면 정지된 회원
	@Column(name = "unban_date", nullable = false)
	private LocalDate unbanDate;

	// 필요 정보(프로필 정보와 관심 직군 정보가 입력되었는지 판단하는 필드)
	@Column(name = "required_info", nullable = false)
	private RequiredInfo requiredInfo;

	@Builder
	public Member(String username, String providerId, String password, SocialType socialType, String email,
				  Permission permission, Integer countAttempt, LocalDate unbanDate, RequiredInfo requiredInfo) {
		this.username = username;
		this.providerId = providerId;
		this.password = password;
		this.socialType = socialType;
		this.email = email;
		this.permission = permission;
		this.countAttempt = countAttempt;
		this.unbanDate = unbanDate;
		this.requiredInfo = requiredInfo;
	}

	public void updateEmail(String email) {
		this.email = email;
	}

	public void updateUsername(String username) {
		this.username = username;
	}

	public void updateRequiredInfo(RequiredInfo requiredInfo) {
		this.requiredInfo = requiredInfo;
	}

	public void updatePassword(String password) {
		this.password = password;
	}

	public void initiateCountAttempt() {
		this.countAttempt = 0;
	}

	public void incrementCountAttempt() {
        this.countAttempt++;
    }
}
