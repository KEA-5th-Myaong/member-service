package myaong.popolog.memberservice.repository;

import myaong.popolog.memberservice.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {
}
