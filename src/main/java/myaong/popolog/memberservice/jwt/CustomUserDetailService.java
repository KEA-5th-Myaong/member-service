package myaong.popolog.memberservice.jwt;

import lombok.RequiredArgsConstructor;
import myaong.popolog.memberservice.entity.Member;
import myaong.popolog.memberservice.service.MemberQueryService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailService implements UserDetailsService {
    private final MemberQueryService memberQueryService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Member findMember = memberQueryService.findMemberByUsername(username);
        return new CustomUserDetails(findMember);
    }
}
