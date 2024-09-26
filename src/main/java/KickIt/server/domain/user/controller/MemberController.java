package KickIt.server.domain.user.controller;

import KickIt.server.domain.user.JwtService;
import KickIt.server.domain.user.entity.LoginRequest;
import KickIt.server.domain.user.entity.Member;
import KickIt.server.domain.user.entity.OAuthProvider;
import KickIt.server.domain.user.entity.SignupRequest;
import KickIt.server.domain.user.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;


@RestController
@RequestMapping("/users")
public class MemberController {

    private final MemberService memberService;
    private final JwtService jwtService;

    @Autowired
    public MemberController(MemberService memberService, JwtService jwtService) {
        this.memberService = memberService;
        this.jwtService = jwtService;
    }

    @PostMapping("/signup")
    public ResponseEntity<Map<String, Object>> getMember(@RequestParam(value = "loginId") String loginId, @RequestBody SignupRequest signupRequest) {
        Map<String, Object> responseBody = new HashMap<>();

        OAuthProvider oAuthProvider = memberService.transAuth(loginId);

        Member member = new Member(signupRequest.getEmail(), signupRequest.getNickname(),
                signupRequest.getFavoriteTeams(), "탱탱볼", signupRequest.isMarketingConsent(), oAuthProvider);

        if(memberService.saveMember(member)) {
            String accessToken = jwtService.createAccessToken(member.getEmail());
            responseBody.put("status", HttpStatus.OK.value());
            responseBody.put("message", "success");
            responseBody.put("xAuthToken", accessToken);
            return new ResponseEntity<>(responseBody, HttpStatus.OK);
        } else {
            responseBody.put("status", HttpStatus.CONFLICT.value());
            responseBody.put("message", "이미 가입된 회원입니다.");
            return new ResponseEntity<>(responseBody, HttpStatus.CONFLICT);
        }

    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> authLogin(@RequestParam(value="loginId") String loginId, @RequestBody LoginRequest loginRequest) {
        Map<String, Object> responseBody = new HashMap<>();

        OAuthProvider oAuthProvider = memberService.transAuth(loginId);

        if(memberService.isMemberExist(loginRequest.getEmail(), oAuthProvider)) {
            String accessToken = jwtService.createAccessToken(loginRequest.getEmail());
            responseBody.put("status", HttpStatus.OK.value());
            responseBody.put("message", "success");
            responseBody.put("xAuthToken", accessToken);
            return new ResponseEntity<>(responseBody, HttpStatus.OK);
        } else {
            responseBody.put("status", HttpStatus.CONFLICT.value());
            responseBody.put("message", "로그인 오류.");
            return new ResponseEntity<>(responseBody, HttpStatus.CONFLICT);
        }

    }

}

