package myaong.popolog.memberservice.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.stream.Collectors;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
@RedisHash(value = "refresh", timeToLive = 60*60*24*1) // 초 단위 -> 1일
public class RefreshToken {

    // key값을 providerId로 저장
    @Id
    private String id;
    Collection<? extends GrantedAuthority> authorities;
 
    @Indexed
    private String refreshToken;
 
    public String getAuthority() {
        return authorities.stream()
                .map(authority -> new SimpleGrantedAuthority(authority.getAuthority()))
                .collect(Collectors.toList())
                .get(0)
                .getAuthority();
    }
}