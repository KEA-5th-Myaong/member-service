package myaong.popolog.memberservice.service;

import lombok.RequiredArgsConstructor;
import myaong.popolog.memberservice.common.exception.ApiCode;
import myaong.popolog.memberservice.common.exception.ApiException;
import myaong.popolog.memberservice.converter.MemberConverter;
import myaong.popolog.memberservice.dto.response.MemberResponse;
import myaong.popolog.memberservice.entity.Follow;
import myaong.popolog.memberservice.entity.Member;
import myaong.popolog.memberservice.repository.FollowRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberCommandServiceImpl implements MemberCommandService {
    private final MemberQueryService memberQueryService;
    private final FollowRepository followRepository;

    @Override
    public MemberResponse.FollowDTO followMember(Long memberId) {
        // 일단 팔로우하는 사람은 id가 5인 member
        Member followingMember = memberQueryService.findMemberByMemberId(5L);
        Member followedMember = memberQueryService.findMemberByMemberId(memberId);

        boolean isExist = followRepository.existsByFollowingAndFollowed(followingMember, followedMember);

        if (isExist) {
            throw new ApiException(ApiCode.ALREADY_FOLLOWING);
        }

        Follow follow = MemberConverter.toFollow(followingMember, followedMember);
        followRepository.save(follow);

        return MemberResponse.FollowDTO.builder()
                .following(true)
                .build();
    }
}
