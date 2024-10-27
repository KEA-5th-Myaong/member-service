package myaong.popolog.memberservice.util;

import java.util.ArrayList;
import java.util.List;

public class AuthPathList {
    private static final List<AuthPath> AUTH_WHITELIST = new ArrayList<>();

    static {
        AUTH_WHITELIST.add(new AuthPath("/members/**"));
        AUTH_WHITELIST.add(new AuthPath("/members/v3/api-docs/"));
        AUTH_WHITELIST.add(new AuthPath("/auth/**"));
        AUTH_WHITELIST.add(new AuthPath("/login/**"));
        // 필요한 경로를 추가
    }

    public static List<AuthPath> getAuthWhitelist() {
        return AUTH_WHITELIST;
    }
}