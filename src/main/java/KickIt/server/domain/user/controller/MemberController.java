package KickIt.server.domain.user.controller;

import KickIt.server.domain.realtime.service.RealTimeService;
import KickIt.server.domain.user.JwtService;
import KickIt.server.domain.user.dto.MemberRepository;
import KickIt.server.domain.user.entity.Member;
import KickIt.server.domain.user.entity.MemberRequest;
import KickIt.server.domain.user.entity.OAuthProvider;
import KickIt.server.domain.user.service.MemberService;
import io.jsonwebtoken.Jwt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static KickIt.server.domain.user.entity.OAuthProvider.APPLE;
import static KickIt.server.domain.user.entity.OAuthProvider.NAVER;

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
    public ResponseEntity<Map<String, Object>> getMember(@RequestParam(value="loginId") String loginId,  @RequestBody MemberRequest memberRequest) {
        Map<String, Object> responseBody = new HashMap<>();

        Member member;
        if(loginId.equals("naver")) {
            member = new Member(memberRequest.getEmail(),memberRequest.getNickname(),
                    memberRequest.getFavoriteTeams(), "레벨 1", memberRequest.isMarketingConsent(), NAVER);
        } else if (loginId.equals("apple")) {
            member = new Member(memberRequest.getEmail(),memberRequest.getNickname(),
                    memberRequest.getFavoriteTeams(), "레벨 1", memberRequest.isMarketingConsent(), APPLE);
        } else {
            responseBody.put("status", HttpStatus.NOT_FOUND.value());
            responseBody.put("message", "지원하지 않는 로그인 ID입니다.");
            return new ResponseEntity<>(responseBody, HttpStatus.NOT_FOUND);
        }


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

}

