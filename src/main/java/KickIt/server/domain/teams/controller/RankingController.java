package KickIt.server.domain.teams.controller;

import KickIt.server.domain.member.entity.Member;
import KickIt.server.domain.member.entity.MemberRepository;
import KickIt.server.domain.member.service.MemberService;
import KickIt.server.domain.teams.dto.RankingDto;
import KickIt.server.domain.teams.entity.Ranking;
import KickIt.server.domain.teams.service.RankingService;
import KickIt.server.jwt.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/ranking")
public class RankingController {
    @Autowired
    JwtTokenUtil jwtTokenUtil;
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    MemberService memberService;
    @Autowired
    RankingService rankingService;

    @GetMapping("")
    public ResponseEntity<Map<String, Object>> inquireRanking(@RequestHeader(value = "xAuthToken") String xAuthToken){
        // 반환할 responseBody
        Map<String, Object> responseBody = new HashMap<>();
        // member를 찾기 위해 token으로 email 조회
        String memberEmail = jwtTokenUtil.getEmailFromToken(xAuthToken);
        // 찾은 email로 member 조회
        Member foundMember = memberRepository.findByEmailAndAuthProvider(memberEmail, memberService.transAuth("kakao")).orElse(null);

        // 입력된 token의 email로 찾은 member가 존재하는 경우
        if(foundMember != null){
            RankingDto.RankingResponse response = rankingService.inquireRanking();
            if(response != null){
                responseBody.put("status", HttpStatus.OK.value());
                responseBody.put("message", "success");
                responseBody.put("data", response);
                responseBody.put("isSuccess", true);
                return new ResponseEntity<>(responseBody, HttpStatus.OK);
            }
            else{
                responseBody.put("status", HttpStatus.NOT_FOUND.value());
                responseBody.put("message", "랭킹 데이터 블러오지 못 함");
                responseBody.put("data", response);
                responseBody.put("isSuccess", false);
                return new ResponseEntity<>(responseBody, HttpStatus.NOT_FOUND);
            }
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
