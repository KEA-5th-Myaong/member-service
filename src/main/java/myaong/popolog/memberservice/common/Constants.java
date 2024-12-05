package myaong.popolog.memberservice.common;

public class Constants {
    public static final long ACCESS_DURATION_MILLIS = 30 * 24 * 60 * 60 * 1000L; // 1000L(1초)
    public static final long REFRESH_DURATION_MILLIS = 30 * 24 * 60 * 60 * 1000L;
    public static final String ACCESS_KEY_NAME = "accessToken";
    public static final String REFRESH_KEY_NAME = "refreshToken";
    public static final String AUTH_TYPE = "Bearer ";
    public static final String AUTHORIZATION_HEADER = "Authorization";

}