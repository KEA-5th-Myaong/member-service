package myaong.popolog.memberservice.service;

import myaong.popolog.memberservice.dto.request.AuthRequest;
import myaong.popolog.memberservice.dto.request.MemberRequest;

public interface AuthService {
    void signUp(AuthRequest.SignUpDTO request);
}
