package myaong.popolog.memberservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import myaong.popolog.memberservice.common.exception.ApiResponse;
import myaong.popolog.memberservice.dto.request.MemberRequest;
import myaong.popolog.memberservice.dto.response.MemberResponse;
import myaong.popolog.memberservice.service.AuthService;
import myaong.popolog.memberservice.service.MemberCommandService;
import myaong.popolog.memberservice.service.MemberQueryService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/members")
public class MemberController {
    private final MemberQueryService memberQueryService;
    private final MemberCommandService memberCommandService;
    private final AuthService authService;

    @Operation(summary = "API 명세서 v0.4 line 14", description = "기본 정보 추가 입력, 소셜 회원가입 시 필수 데이터를 불러올 수 없어, 사용자에게 필수 데이터를 요청합니다.")
    @PostMapping
    public ApiResponse addAdditionalBasicInfo(@RequestHeader("memberId") Long memberId,
                                              @RequestBody @Valid MemberRequest.AdditionalBasicInfoDTO request) {
        memberCommandService.addAdditionalBasicInfo(memberId, request);
        return ApiResponse.onSuccess(null);
    }

    @Operation(summary = "API 명세서 v0.4 line 15", description = "현재 로그인한 회원의 정보 조회")
    @GetMapping("/me")
    public ApiResponse<MemberResponse.BasicInfoDTO> getMemberBasicInfo(@RequestHeader("memberId") Long memberId) {
        return ApiResponse.onSuccess(memberQueryService.getMemberBasicInfo(memberId));
    }

    @Operation(summary = "API 명세서 v0.4 line 18", description = "기본 정보 수정")
    @PutMapping
    public ApiResponse editBasicInfo(@RequestHeader("memberId") Long memberId, @RequestBody @Valid MemberRequest.editBasicInfoDTO request) {
        memberCommandService.editBasicInfo(memberId, request);
        return ApiResponse.onSuccess(null);
    }

    @Operation(summary = "API 명세서 v0.4 line 16", description = "개인정보 수정 시 비밀번호 일치 확인")
    @PostMapping("/check-password")
    public ApiResponse<Boolean> checkPassword(@RequestHeader("memberId") Long memberId, @RequestBody @Valid MemberRequest.CheckPasswordDTO request) {
        boolean matches = authService.checkPassword(memberId, request);
        return ApiResponse.onSuccess(matches);
    }

    @Operation(summary = "API 명세서 v0.4 line 17", description = "비밀번호 변경")
    @PutMapping("/password")
    public ApiResponse updatePassword(@RequestHeader("memberId") Long memberId, @RequestBody @Valid MemberRequest.UpdatePasswordDTO request ) {
        authService.updatePassword(memberId, request);
        return ApiResponse.onSuccess(null);
    }
}
