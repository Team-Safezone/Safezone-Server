package KickIt.server.domain.home.controller;

import KickIt.server.domain.home.service.HomeService;
import KickIt.server.domain.member.entity.Member;
import KickIt.server.domain.member.entity.MemberRepository;
import KickIt.server.domain.member.service.MemberService;
import KickIt.server.jwt.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/home")
public class HomeController {
    @Autowired
    JwtTokenUtil jwtTokenUtil;
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    MemberService memberService;
    @Autowired
    HomeService homeService;

    @GetMapping()
    public ResponseEntity<Map<String, Object>> showHome(@RequestHeader(value = "xAuthToken") String xAuthToken){
        // 반환할 responseBody
        Map<String, Object> responseBody = new HashMap<>();
        // member를 찾기 위해 token으로 email 조회
        String memberEmail = jwtTokenUtil.getEmailFromToken(xAuthToken);
        // 찾은 email로 member 조회
        Member foundMember = memberRepository.findByEmailAndAuthProvider(memberEmail, memberService.transAuth("kakao")).orElse(null);

        // 입력된 token의 email로 찾은 member가 존재하는 경우
        if(foundMember != null){
            responseBody.put("status", HttpStatus.OK.value());
            responseBody.put("message", "success");
            responseBody.put("data", homeService.inquireHomeInfos(foundMember));
            responseBody.put("isSuccess", true);
            return new ResponseEntity<>(responseBody, HttpStatus.OK);
        }
        // 입력된 token의 email로 찾은 member가 존재하지 않는 경우
        else{
            responseBody.put("status", HttpStatus.NOT_FOUND.value());
            responseBody.put("message", "해당 사용자 없음");
            responseBody.put("isSuccess", false);
            return new ResponseEntity<>(responseBody, HttpStatus.NOT_FOUND);
        }
    }
}
