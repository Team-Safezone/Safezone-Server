package KickIt.server.domain.teams.controller;

import KickIt.server.domain.member.entity.Member;
import KickIt.server.domain.member.entity.MemberRepository;
import KickIt.server.domain.member.service.MemberService;
import KickIt.server.domain.teams.dto.SquadDto;
import KickIt.server.domain.teams.entity.Squad;
import KickIt.server.domain.teams.service.SquadService;
import KickIt.server.global.common.crawler.FixtureCrawler;
import KickIt.server.global.common.crawler.SquadCrawler;
import KickIt.server.jwt.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/squad")
public class SquadController {
    @Autowired
    SquadService squadService;
    @Autowired
    SquadCrawler squadCrawler;
    @Autowired
    JwtTokenUtil jwtTokenUtil;
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    MemberService memberService;

    @PostMapping("/crawl")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Map<String, Object>> crawlSquad(@RequestParam("season") String season){
        List<Squad> squadList = new ArrayList<>();
        squadList = squadCrawler.getTeamSquads(season);

        squadService.saveSquads(squadList);
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("status", HttpStatus.OK.value());
        responseBody.put("message", "success");
        responseBody.put("data", squadList);
        responseBody.put("isSuccess", true);
        return new ResponseEntity<>(responseBody, HttpStatus.OK);
    }

    @GetMapping("name-url")
    // 현재 시즌 프리미어리그 팀 전체의 이름과 로고 이미지 조회 API
    public ResponseEntity<Map<String, Object>> inquireEplTeamNameAndUrl(@RequestHeader(value = "xAuthToken", required = false) String xAuthToken){
        // 반환할 response
        Map<String, Object> responseBody = new HashMap<>();

        // xAuthToken 없는 경우
        if(xAuthToken == null){
            List<SquadDto.EplNameUrlResponse> data = squadService.getEplTeamNameAndUrl();
            // 등록된 것 중 가장 최근 시즌의 Squad 데이터 조회 완료
            if(data != null){
                responseBody.put("status", HttpStatus.OK.value());
                responseBody.put("message", "success");
                responseBody.put("data", data);
                responseBody.put("isSuccess", true);
                return new ResponseEntity<>(responseBody, HttpStatus.OK);
            }
            // 조회한 squad 데이터가 없는 경우
            else{
                responseBody.put("status", HttpStatus.NOT_FOUND.value());
                responseBody.put("message", "현재 시즌 팀 데이터 조회 실패");
                responseBody.put("isSuccess", false);
                return new ResponseEntity<>(responseBody, HttpStatus.NOT_FOUND);
            }
        }

        // xAuthToken 있는 경우
        else{
            // member를 찾기 위해 token으로 email 조회
            String memberEmail = jwtTokenUtil.getEmailFromToken(xAuthToken);
            // 찾은 email로 member 조회
            Member foundMember = memberRepository.findByEmailAndAuthProvider(memberEmail, memberService.transAuth("kakao")).orElse(null);

            // 입력된 token의 email로 찾은 member가 존재하는 경우
            if(foundMember != null){
                List<SquadDto.EplNameUrlResponse> data = squadService.getEplTeamNameAndUrl();
                // 등록된 것 중 가장 최근 시즌의 Squad 데이터 조회 완료
                if(data != null){
                    responseBody.put("status", HttpStatus.OK.value());
                    responseBody.put("message", "success");
                    responseBody.put("data", data);
                    responseBody.put("isSuccess", true);
                    return new ResponseEntity<>(responseBody, HttpStatus.OK);
                }
                // 조회한 squad 데이터가 없는 경우
                else{
                    responseBody.put("status", HttpStatus.NOT_FOUND.value());
                    responseBody.put("message", "현재 시즌 팀 데이터 조회 실패");
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
}
