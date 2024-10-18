package myaong.popolog.memberservice.controller;

import lombok.RequiredArgsConstructor;
import myaong.popolog.memberservice.common.exception.ApiResponse;
import myaong.popolog.memberservice.dto.request.MemberRequest;
import myaong.popolog.memberservice.dto.response.MemberResponse;
import myaong.popolog.memberservice.service.MemberQueryService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/members")
public class MemberController {
    private final MemberQueryService memberQueryService;

    @GetMapping
    public ApiResponse<MemberResponse.BasicInfoDTO> getMemberBasicInfo() {
        return ApiResponse.onSuccess(memberQueryService.getMemberBasicInfo());
    }

    @GetMapping("/{memberId}")
    public ApiResponse<MemberResponse.BasicInfoDTO> getMemberBasicInfoByMemberId(@PathVariable Long memberId) {
        return null;
    }

    @GetMapping("/{username}")
    public ApiResponse<MemberResponse.BasicInfoDTO> getMemberBasicInfoByUsername(@PathVariable String username) {
        return null;
    }

    @GetMapping("/{memberId}/info")
    public ApiResponse<MemberResponse.BlogInfoDTO> getMemberBlogInfo(@PathVariable Long memberId) {
        return null;
    }

    @GetMapping("/following")
    public ApiResponse<MemberResponse.BlogInfoDTO> getMemberFollowingList(@RequestBody MemberRequest.FollowPageRequestDTO pageRequest) {
        return null;
    }

    @GetMapping("/followed")
    public ApiResponse<MemberResponse.BlogInfoDTO> getMemberFollowedList(@RequestBody MemberRequest.FollowPageRequestDTO pageRequest) {
        return null;
    }
}
