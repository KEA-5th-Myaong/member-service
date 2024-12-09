package myaong.popolog.memberservice.jwt;

import jakarta.annotation.Nullable;
import myaong.popolog.memberservice.enums.Permission;
import org.springframework.http.HttpMethod;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import static org.springframework.http.HttpMethod.*;

@Component
public class RequestMatcherHolder {

    private static final List<RequestInfo> REQUEST_INFO_LIST = List.of(
        // main
        new RequestInfo(GET, "/", null),

        // auth
        new RequestInfo(GET, "/auth/**", null),
        new RequestInfo(POST, "/auth/**", null),
        new RequestInfo(GET, "/oauth2/**", null),
        new RequestInfo(POST, "/oauth2/**", null),

        new RequestInfo(GET, "/login/**", null),
        new RequestInfo(POST, "/login/**", null),
        new RequestInfo(GET, "/webjars/**",null),

        // swagger
        new RequestInfo(GET, "/api-docs/**", null),

        // test
        new RequestInfo(GET, "/tests/members/**", null),

        // user
        new RequestInfo(GET, "/members/**", Permission.MEMBER),
        new RequestInfo(PUT, "/members/**", Permission.MEMBER),
        new RequestInfo(POST, "/members/**", Permission.MEMBER),
        new RequestInfo(DELETE, "/members/**", Permission.MEMBER),

        // static resources
        new RequestInfo(GET, "/docs/**", null),
        new RequestInfo(GET, "/*.ico", null),
        new RequestInfo(GET, "/resources/**", null),
        new RequestInfo(GET, "/error", null),

        // 각 Permission의 권한이 필요한 RequestInfo가 최소 1개씩은 리스트에 있어야함
        new RequestInfo(GET, "/admin/**", Permission.ADMIN),
        new RequestInfo(GET, "/super/**", Permission.SUPER)
    );
    private final ConcurrentHashMap<String, RequestMatcher> reqMatcherCacheMap = new ConcurrentHashMap<>();

    /**
     * 최소 권한이 주어진 요청에 대한 RequestMatcher 반환
     * @param minPermission 최소 권한 (Nullable)
     * @return 생성된 RequestMatcher
     */
    public RequestMatcher getRequestMatchersByMinPermission(@Nullable Permission minPermission) {
        var key = getKeyByRole(minPermission);
        return reqMatcherCacheMap.computeIfAbsent(key, k ->
            new OrRequestMatcher(REQUEST_INFO_LIST.stream()
                .filter(reqInfo -> Objects.equals(reqInfo.minPermission, minPermission))
                .map(reqInfo -> new AntPathRequestMatcher(reqInfo.pattern(),
                    reqInfo.method().name()))
                .toArray(AntPathRequestMatcher[]::new)));
    }

    private String getKeyByRole(@Nullable Permission minPermission) {
        return minPermission == null ? "VISITOR" : minPermission.name();
    }

    private record RequestInfo(HttpMethod method, String pattern, Permission minPermission) {

    }
}