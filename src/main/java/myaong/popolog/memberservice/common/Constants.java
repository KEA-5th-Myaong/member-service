package myaong.popolog.memberservice.common;

public class Constants {
    public static final long ACCESS_DURATION_MILLIS = 60 * 60 * 12 * 1000 * 1L; // 12시간(임시)
    public static final long REFRESH_DURATION_MILLIS = 60 * 60 * 24 * 1000 * 1L; // 1일
    public static final String ACCESS_KEY_NAME = "access";
    public static final String REFRESH_KEY_NAME = "refresh";
    public static final String AUTH_TYPE = "Bearer ";
    public static final String AUTHORIZATION_HEADER = "Authorization";

}