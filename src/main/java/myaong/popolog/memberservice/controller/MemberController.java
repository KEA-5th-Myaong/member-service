package myaong.popolog.memberservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import myaong.popolog.memberservice.common.exception.ApiResponse;
import myaong.popolog.memberservice.dto.response.MemberResponse;
import myaong.popolog.memberservice.service.MemberCommandService;
import myaong.popolog.memberservice.service.MemberQueryService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/members")
public class MemberController {
    private final MemberQueryService memberQueryService;
    private final MemberCommandService memberCommandService;

    @Operation(summary = "API 명세서 v0.3 line 15", description = "현재 로그인한 회원의 정보 조회")
    @GetMapping("/me")
    public ApiResponse<MemberResponse.BasicInfoDTO> getMemberBasicInfo() {
        return ApiResponse.onSuccess(memberQueryService.getMemberBasicInfo());
    }

    @Operation(summary = "API 명세서 v0.3 line 16", description = "memberId로 회원 정보 조회")
    @GetMapping("/{memberId}")
    public ApiResponse<MemberResponse.PartialInfoDTO> getMemberPartialInfoByMemberId(@PathVariable Long memberId) {
        return ApiResponse.onSuccess(memberQueryService.getMemberPartialInfoByMemberId(memberId));
    }

    @Operation(summary = "API 명세서 v0.3 line 17", description = "username으로 회원 정보 조회")
    @GetMapping
    public ApiResponse<MemberResponse.PartialInfoDTO> getMemberPartialInfoByUsername(@RequestParam(required = false, defaultValue = "admin1") String username) {
        return ApiResponse.onSuccess(memberQueryService.getMemberPartialInfoByUsername(username));
    }

    @Operation(summary = "API 명세서 v0.3 line 22", description = "회원 정보 조회 (블로그 접속 시)")
    @GetMapping("/{memberId}/info")
    public ApiResponse<MemberResponse.BlogInfoDTO> getMemberBlogInfo(@PathVariable(required = false) Long memberId) {
        return ApiResponse.onSuccess(memberQueryService.getMemberBlogInfo(memberId));
    }

    @Operation(summary = "API 명세서 v0.3 line 23", description = "팔로우 토글(팔로우 시 알림 발송 기능은 아직 미구현)")
    @PostMapping("/{memberId}/follow")
    public ApiResponse<MemberResponse.FollowDTO> followMember(@PathVariable(required = false) Long memberId) {
        return ApiResponse.onSuccess(memberCommandService.followMember(memberId));
    }

    @Operation(summary = "API 명세서 v0.3 line 24", description = "팔로잉 조회 (무한 스크롤)")
    @GetMapping("/{memberId}/following/{lastId}")
    public ApiResponse<MemberResponse.FollowingListDTO> getMemberFollowingList(@PathVariable(required = false) Long memberId, @PathVariable(required = false) Long lastId) {
        return ApiResponse.onSuccess(memberQueryService.getMemberFollowingList(memberId, lastId));
    }

    @Operation(summary = "API 명세서 v0.3 line 25", description = "팔로워 조회 (무한 스크롤)")
    @GetMapping("/{memberId}/followed/{lastId}")
    public ApiResponse<MemberResponse.FollowedListDTO> getMemberFollowedList(@PathVariable(required = false) Long memberId, @PathVariable(required = false) Long lastId) {
        return ApiResponse.onSuccess(memberQueryService.getMemberFollowedList(memberId, lastId));
    }
}
