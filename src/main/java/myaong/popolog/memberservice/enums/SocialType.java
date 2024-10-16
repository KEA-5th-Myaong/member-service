package myaong.popolog.memberservice.enums;

public enum SocialType {

	NORMAL, KAKAO, GOOGLE;

	public static SocialType valueOfLower(String name) {
		return SocialType.valueOf(name.toUpperCase());
	}
}
