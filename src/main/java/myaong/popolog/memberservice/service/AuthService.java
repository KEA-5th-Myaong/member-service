package myaong.popolog.memberservice.service;

import myaong.popolog.memberservice.dto.request.AuthRequest;
import myaong.popolog.memberservice.dto.request.MemberRequest;

public interface AuthService {
    void signUp(AuthRequest.SignUpDTO request);
    String encodePassword(String rawPassword);
    boolean matches(String rawPassword, String encodedPassword);
    boolean checkPassword(Long memberId, MemberRequest.CheckPasswordDTO request);
    void updatePassword(Long memberId, MemberRequest.UpdatePasswordDTO request);
}
