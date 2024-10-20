package myaong.popolog.memberservice.repository;

import myaong.popolog.memberservice.entity.Follow;
import myaong.popolog.memberservice.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FollowRepository extends JpaRepository<Follow, Long> {
    boolean existsByFollowingAndFollowed(Member following, Member followed);
    void deleteByFollowingAndFollowed(Member following, Member followed);
}
