package myaong.popolog.memberservice.repository;

import myaong.popolog.memberservice.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Member findByUsername(String username);
    Member findByProviderId(String providerId);
    Optional<Member> findByEmail(String email);
    List<Member> findByIdIn(List<Long> ids);
}
