package myaong.popolog.memberservice.service;

import lombok.RequiredArgsConstructor;
import myaong.popolog.memberservice.client.BlogServiceClient;
import myaong.popolog.memberservice.converter.MemberConverter;
import myaong.popolog.memberservice.dto.request.MemberProfileRequest;
import myaong.popolog.memberservice.dto.request.MemberRequest;
import myaong.popolog.memberservice.entity.Member;
import myaong.popolog.memberservice.enums.RequiredInfo;
import myaong.popolog.memberservice.repository.MemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberCommandServiceImpl implements MemberCommandService {
    private final MemberQueryService memberQueryService;
    private final MemberRepository memberRepository;

    private final BlogServiceClient blogServiceClient;

    @Override
    public void addAdditionalBasicInfo(Long memberId, MemberRequest.AdditionalBasicInfoDTO request) {
        // Member 조회
        Member findMember = memberQueryService.findMemberByMemberId(memberId);

        // username만 수정
        findMember.updateUsername(request.getUsername());

        // Blog Service로 나머지 데이터 수정 요청, MemberProfile 저장(POST /blog/profile 호출)
        MemberProfileRequest.CreateDTO memberProfileCreateDTO = MemberConverter.toMemberProfileCreateDTO(findMember.getId(), request);
        blogServiceClient.createMemberProfile(memberProfileCreateDTO);

        // MemberProfile 정보는 입력됐으므로 관심 직군 정보만 필요
        findMember.updateRequiredInfo(RequiredInfo.PREJOBS);
    }

    @Override
    public void editBasicInfo(MemberRequest.editBasicInfoDTO request) {

    }

    @Override
    public Member saveMember(Member member) {
        return memberRepository.save(member);
    }
}
