package myaong.popolog.memberservice.oauth2;

import myaong.popolog.memberservice.oauth2.dto.OAuthUserDTO;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

public class CustomOAuth2User implements OAuth2User {

    private final OAuthUserDTO oAuthUserDTO;

    public CustomOAuth2User(OAuthUserDTO oAuthUserDTO) {
        this.oAuthUserDTO = oAuthUserDTO;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return null;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> collection = new ArrayList<>();

        collection.add(new GrantedAuthority() {
            @Override
            public String getAuthority() {
                return oAuthUserDTO.getPermission().name();
            }
        });

        return collection;
    }

    @Override
    public String getName() {
        return oAuthUserDTO.getName();
    }

    public Long getMemberId() {
        return oAuthUserDTO.getMemberId();
    }

    public String getProviderId() {
        return oAuthUserDTO.getProviderId();
    }

    public Boolean getDuplicateEmail() {
        return oAuthUserDTO.getDuplicateEmail();
    }

}
