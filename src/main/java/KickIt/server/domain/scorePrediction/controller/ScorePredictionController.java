package KickIt.server.domain.scorePrediction.controller;

import KickIt.server.domain.fixture.entity.Fixture;
import KickIt.server.domain.fixture.entity.FixtureRepository;
import KickIt.server.domain.member.entity.Member;
import KickIt.server.domain.member.entity.MemberRepository;
import KickIt.server.domain.member.service.MemberService;
import KickIt.server.domain.scorePrediction.dto.ScorePredictionDto;
import KickIt.server.domain.scorePrediction.entity.ScorePrediction;
import KickIt.server.domain.scorePrediction.service.ScorePredictionService;
import KickIt.server.jwt.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/score-predict")
public class ScorePredictionController {
    @Autowired
    JwtTokenUtil jwtTokenUtil;
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    MemberService memberService;
    @Autowired
    FixtureRepository fixtureRepository;
    @Autowired
    ScorePredictionService scorePredictionService;

    @PostMapping("/save")
    public ResponseEntity<Map<String, Object>> saveScorePrediction(@RequestParam("xAuthToken") String xAuthToken, @RequestParam("matchId") Long matchId, @RequestBody ScorePredictionDto.ScorePredictionSaveRequest scorePredictionSaveRequest){
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
                ScorePrediction scorePrediction = ScorePrediction.builder()
                        .member(foundMember)
                        .fixture(foundFixture)
                        .homeTeamScore(scorePredictionSaveRequest.getHomeTeamScore())
                        .awayTeamScore(scorePredictionSaveRequest.getAwayTeamScore())
                        .build();
                HttpStatus saveStatus = scorePredictionService.saveScorePrediction(scorePrediction);
                if(saveStatus == HttpStatus.OK) {
                    ScorePredictionDto.ScorePredictionSaveResponse response = new ScorePredictionDto.ScorePredictionSaveResponse(foundMember);
                    responseBody.put("status", HttpStatus.OK.value());
                    responseBody.put("message", "success");
                    responseBody.put("data", response);
                    responseBody.put("isSuccess", false);
                    return new ResponseEntity<>(responseBody, HttpStatus.OK);
                }
                else if(saveStatus == HttpStatus.CONFLICT){
                    responseBody.put("status", saveStatus);
                    responseBody.put("message", "중복 저장 시도");
                    responseBody.put("isSuccess", false);
                    return new ResponseEntity<>(responseBody, HttpStatus.CONFLICT);
                }
                else {
                    responseBody.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
                    responseBody.put("message", "저장 실패");
                    responseBody.put("isSuccess", false);
                    return new ResponseEntity<>(responseBody, HttpStatus.INTERNAL_SERVER_ERROR);
                }
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
