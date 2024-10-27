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
        new RequestInfo(GET, "/**", null),
        new RequestInfo(POST, "/**", null),

        // auth
        new RequestInfo(POST, "/auth/**", null),
        new RequestInfo(GET, "/auth/**", null),
        new RequestInfo(GET, "/login/**", null),
        new RequestInfo(POST, "/login/**", null),

        // user
        new RequestInfo(GET, "/members/**", null),
//        new RequestInfo(GET, "/members/v3/api-docs/", null),

        // static resources
        new RequestInfo(GET, "/docs/**", null),
        new RequestInfo(GET, "/*.ico", null),
        new RequestInfo(GET, "/resources/**", null),
        new RequestInfo(GET, "/error", null)
    );
    private final ConcurrentHashMap<String, RequestMatcher> reqMatcherCacheMap = new ConcurrentHashMap<>();

    /**
     * 최소 권한이 주어진 요청에 대한 RequestMatcher 반환
     * @param minPermission 최소 권한 (Nullable)
     * @return 생성된 RequestMatcher
     */
//    public RequestMatcher getRequestMatchersByMinPermission(@Nullable Permission minPermission) {
//        var key = getKeyByRole(minPermission);
//        return reqMatcherCacheMap.computeIfAbsent(key, k ->
//            new OrRequestMatcher(REQUEST_INFO_LIST.stream()
//                .filter(reqInfo -> Objects.equals(reqInfo.minPermission, minPermission))
//                .map(reqInfo -> new AntPathRequestMatcher(reqInfo.pattern(),
//                    reqInfo.method().name()))
//                .toArray(AntPathRequestMatcher[]::new)));
//    }

    public RequestMatcher getRequestMatchersByMinPermission(@Nullable Permission minPermission) {
        var key = getKeyByRole(minPermission);
        return reqMatcherCacheMap.computeIfAbsent(key, k -> {
            var matchers = REQUEST_INFO_LIST.stream()
                    .filter(reqInfo -> Objects.equals(reqInfo.minPermission, minPermission))
                    .map(reqInfo -> new AntPathRequestMatcher(reqInfo.pattern(), reqInfo.method().name()))
                    .toArray(AntPathRequestMatcher[]::new);

            if (matchers.length == 0) {
                // 빈 경우 기본 요청 매처 반환 (예: 모든 요청 허용)
                return new OrRequestMatcher(new AntPathRequestMatcher("/**"));
            }
            return new OrRequestMatcher(matchers);
        });
    }

    private String getKeyByRole(@Nullable Permission minPermission) {
        return minPermission == null ? "VISITOR" : minPermission.name();
    }

    private record RequestInfo(HttpMethod method, String pattern, Permission minPermission) {

    }

}