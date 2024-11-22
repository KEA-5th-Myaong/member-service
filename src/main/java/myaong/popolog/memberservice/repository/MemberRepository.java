package myaong.popolog.memberservice.repository;

import myaong.popolog.memberservice.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);

    Member findByUsername(String username);
    Member findByProviderId(String providerId);

    @Query("SELECT m.password FROM Member m WHERE m.id = :memberId")
    Optional<String> findPasswordById(@Param("memberId") Long memberId);

    Optional<Member> findByEmail(String email);
    List<Member> findByIdIn(List<Long> ids);
}
