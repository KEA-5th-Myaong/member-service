package myaong.popolog.memberservice.client;

import myaong.popolog.memberservice.dto.request.MemberProfileRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "blog-service")
public interface BlogServiceClient {
    @PostMapping("/blog/profile")
    void createMemberProfile(@RequestBody MemberProfileRequest.CreateDTO createDTO);
}
