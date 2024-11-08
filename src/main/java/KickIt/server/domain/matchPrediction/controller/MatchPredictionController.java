package KickIt.server.domain.matchPrediction.controller;

import KickIt.server.domain.fixture.entity.Fixture;
import KickIt.server.domain.fixture.entity.FixtureRepository;
import KickIt.server.domain.matchPrediction.service.MatchPredictionService;
import KickIt.server.domain.member.entity.Member;
import KickIt.server.domain.member.entity.MemberRepository;
import KickIt.server.domain.member.service.MemberService;
import KickIt.server.jwt.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/match-predict")
public class MatchPredictionController {
    @Autowired
    JwtTokenUtil jwtTokenUtil;
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    MemberService memberService;
    @Autowired
    FixtureRepository fixtureRepository;
    @Autowired
    MatchPredictionService matchPredictionService;

    @GetMapping()
    public ResponseEntity<Map<String, Object>> inquireMatchPrediction(@RequestHeader(value = "xAuthToken") String xAuthToken, @RequestParam("matchId") Long matchId){
        // 반환할 responseBody
        Map<String, Object> responseBody = new HashMap<>();
        // member를 찾기 위해 token으로 email 조회
        String memberEmail = jwtTokenUtil.getEmailFromToken(xAuthToken);
        // 찾은 email로 member 조회
        Member foundMember = memberRepository.findByEmailAndAuthProvider(memberEmail, memberService.transAuth("kakao")).orElse(null);

        // 입력된 token의 email로 찾은 member가 존재하는 경우
        if(foundMember != null){
            // 경기 id로 경기 조회
            Fixture foundFixture = fixtureRepository.findById(matchId).orElse(null);
            // 경기 id로 조회한 경기가 존재하는 경우
            if(foundFixture != null){
                responseBody.put("status", HttpStatus.OK.value());
                responseBody.put("message", "success");
                responseBody.put("data", matchPredictionService.inquireMatchPrediction(foundFixture, foundMember));
                responseBody.put("isSuccess", true);
                return new ResponseEntity<>(responseBody, HttpStatus.OK);
            }
            // 경기 id로 조회한 경기가 존재하지 않는 경우
            else{
                responseBody.put("status", HttpStatus.NOT_FOUND.value());
                responseBody.put("message", "해당 경기 없음");
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
