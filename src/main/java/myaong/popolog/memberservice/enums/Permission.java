package myaong.popolog.memberservice.enums;

public enum Permission {

	MEMBER, ADMIN, SUPER;

	public static Permission valueOfLower(String name) {
		return Permission.valueOf(name.toUpperCase());
	}
}
