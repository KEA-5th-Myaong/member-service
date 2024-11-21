package myaong.popolog.memberservice.client;

import myaong.popolog.memberservice.dto.request.MemberProfileRequest;
import myaong.popolog.memberservice.dto.response.MemberProfileResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "blog-service")
public interface BlogServiceClient {

    @PostMapping("/blog/profiles")
    void createMemberProfile(@RequestBody MemberProfileRequest.CreateDTO createDTO);

    @GetMapping("/blog/profiles")
    MemberProfileResponse.ProfileInfoDTO getProfileInfo(@RequestHeader("memberId") Long memberId);
}
