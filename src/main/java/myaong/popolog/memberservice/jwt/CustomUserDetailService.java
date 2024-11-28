package myaong.popolog.memberservice.jwt;

import lombok.RequiredArgsConstructor;
import myaong.popolog.memberservice.converter.AuthConverter;
import myaong.popolog.memberservice.converter.MemberConverter;
import myaong.popolog.memberservice.entity.Member;
import myaong.popolog.memberservice.enums.SocialType;
import myaong.popolog.memberservice.repository.MemberRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailService implements UserDetailsService {
    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Member findMember = memberRepository.findByUsername(username);
        findMember = (findMember == null) ? new Member(username, null, null, SocialType.NORMAL,null, null, null, null, null) : findMember;
        return new CustomUserDetails(findMember);
    }
}
