package KickIt.server.domain.heartRate.controller;

import KickIt.server.domain.heartRate.dto.HeartRateDto;
import KickIt.server.domain.heartRate.dto.StatisticsDto;
import KickIt.server.domain.heartRate.service.*;
import KickIt.server.jwt.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/heartRate")
public class HeartRateController {

    private final HeartRateService heartRateService;
    private final HeartRateStatisticsService heartRateStatisticsService;
    private final StatisticsService statisticsService;
    private final TeamHeartRateStatisticsService teamHeartRateStatisticsService;
    private final TeamHeartRateService teamHeartRateService;
    private final FixtureHeartRateStatisticsService fixtureHeartRateStatisticsService;

    private final JwtTokenUtil jwtTokenUtil;

    @Autowired
    public HeartRateController(HeartRateService heartRateService, HeartRateStatisticsService heartRateStatisticsService, StatisticsService statisticsService, TeamHeartRateStatisticsService teamHeartRateStatisticsService, TeamHeartRateService teamHeartRateService, FixtureHeartRateStatisticsService fixtureHeartRateStatisticsService, JwtTokenUtil jwtTokenUtil) {
        this.heartRateService = heartRateService;
        this.heartRateStatisticsService = heartRateStatisticsService;
        this.statisticsService = statisticsService;
        this.teamHeartRateStatisticsService = teamHeartRateStatisticsService;
        this.teamHeartRateService = teamHeartRateService;
        this.fixtureHeartRateStatisticsService = fixtureHeartRateStatisticsService;
        this.jwtTokenUtil = jwtTokenUtil;
    }


    // 심박수 저장
    @PostMapping("/save")
    public ResponseEntity<Map<String, Object>> saveHeartRate(@RequestHeader(value = "xAuthToken") String xAuthToken, @RequestBody HeartRateDto heartRateDto) {
        String email = jwtTokenUtil.getEmailFromToken(xAuthToken);

        Map<String, Object> responseBody = new HashMap<>();

        if (jwtTokenUtil.validateToken(xAuthToken, email)) {
            heartRateService.save(email, heartRateDto);

            // 데이터 저장과 동시에 통계 객체 생성
            heartRateStatisticsService.saveStatistics(email, heartRateDto);

            responseBody.put("status", HttpStatus.OK.value());
            responseBody.put("message", "success");
            responseBody.put("isSuccess", true);

            return new ResponseEntity<>(responseBody, HttpStatus.OK);
        } else {
            responseBody.put("status", HttpStatus.FORBIDDEN.value());
            responseBody.put("message", "유효하지 않은 사용자입니다.");
            responseBody.put("isSuccess", false);
            return new ResponseEntity<>(responseBody, HttpStatus.FORBIDDEN);
        }
    }

    // 각 경기의 통계 데이터 가져오기
    @GetMapping("/statistics/matchId/{matchId}")
    public ResponseEntity<Map<String, Object>> getAllStatistics(@PathVariable("matchId") Long matchId, @RequestHeader(value = "xAuthToken") String xAuthToken) {
        String email = jwtTokenUtil.getEmailFromToken(xAuthToken);

        Map<String, Object> responseBody = new HashMap<>();

        StatisticsDto response = statisticsService.getHeartRateStatistics(email, matchId);

        if (jwtTokenUtil.validateToken(xAuthToken, email)) {
            responseBody.put("status", HttpStatus.OK.value());
            responseBody.put("message", "success");
            responseBody.put("data", response);
            responseBody.put("isSuccess", true);
            return new ResponseEntity<>(responseBody, HttpStatus.OK);
        } else {
            responseBody.put("status", HttpStatus.FORBIDDEN.value());
            responseBody.put("message", "이유 작성");
            responseBody.put("data", response);
            responseBody.put("isSuccess", false);
            return new ResponseEntity<>(responseBody, HttpStatus.FORBIDDEN);
        }
    }

    // 유저의 개인 심박수 데이터가 존재하는지 확인
    @GetMapping("/check-dataExists/matchId/{matchId}")
    public ResponseEntity<Map<String, Object>> getAllStatistics(@RequestHeader(value = "xAuthToken") String xAuthToken, @PathVariable(value = "matchId") Long matchId) {
        String email = jwtTokenUtil.getEmailFromToken(xAuthToken);

        Map<String, Object> responseBody = new HashMap<>();

        boolean response = heartRateService.isExist(email, matchId);

        if (jwtTokenUtil.validateToken(xAuthToken, email)) {
            if (response) {
                responseBody.put("status", HttpStatus.OK.value());
                responseBody.put("message", "해당 경기의 심박수 데이터가 존재합니다.");
                responseBody.put("data", response);
                responseBody.put("isSuccess", true);
                return new ResponseEntity<>(responseBody, HttpStatus.OK);
            } else {
                responseBody.put("status", HttpStatus.OK.value());
                responseBody.put("message", "해당 경기의 심박수 데이터가 존재하지 않습니다.");
                responseBody.put("data", response);
                responseBody.put("isSuccess", true);
                return new ResponseEntity<>(responseBody, HttpStatus.OK);
            }
        } else {
            responseBody.put("status", HttpStatus.FORBIDDEN.value());
            responseBody.put("message", "해당 토큰이 유효하지 않습니다.");
            responseBody.put("data", response);
            responseBody.put("isSuccess", false);
            return new ResponseEntity<>(responseBody, HttpStatus.FORBIDDEN);
        }

    }

    // 데이터가 모인 후 통계 데이터 만들기
    @PostMapping("/calculate/statistics/{matchId}")
    public ResponseEntity<Map<String, Object>> saveHeartRate(@PathVariable(value = "matchId") Long matchId) {
        // 팀 별 통계 계산
        teamHeartRateStatisticsService.calculateTeamHeartRate(matchId);
        // 팀 별 평균 계산
        teamHeartRateService.saveTeamMinAvgMax(matchId);
        // 통계 계산
        fixtureHeartRateStatisticsService.calculateHeartRate(matchId);

        Map<String, Object> responseBody = new HashMap<>();

        responseBody.put("status", HttpStatus.OK.value());
        responseBody.put("message", "통계 계산이 완료 되었습니다.");
        responseBody.put("isSuccess", true);
        return new ResponseEntity<>(responseBody, HttpStatus.OK);
    }
}
