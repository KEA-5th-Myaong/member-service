package myaong.popolog.memberservice.repository;

import myaong.popolog.memberservice.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Member findByUsername(String username);
    List<Member> findByIdIn(List<Long> ids);
}
