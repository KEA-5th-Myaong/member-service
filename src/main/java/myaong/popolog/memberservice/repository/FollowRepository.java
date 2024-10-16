package myaong.popolog.memberservice.repository;

import myaong.popolog.memberservice.entity.Follow;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FollowRepository extends JpaRepository<Follow, Long> {
}
