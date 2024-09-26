package KickIt.server.domain.lineup.controller;

import KickIt.server.domain.lineup.dto.MatchLineupDto;
import KickIt.server.domain.lineup.service.MatchLineupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/match-lineup")
public class MatchLineupController {
    @Autowired
    MatchLineupService matchLineupService;
    @GetMapping()
    public ResponseEntity<Map<String, Object>> getMatchLineupByFixture(@RequestParam("matchId") long id){
        Map<String, Object> responseBody = new HashMap<>();
        MatchLineupDto.MatchLineupResponse lineup = matchLineupService.findMatchLineupByFixture(id);
        if(lineup != null){
            responseBody.put("status", HttpStatus.OK.value());
            responseBody.put("message", "success");
            responseBody.put("data", lineup);
            return new ResponseEntity<>(responseBody, HttpStatus.OK);
        }
        else{
            responseBody.put("status", HttpStatus.NOT_FOUND.value());
            responseBody.put("message", "아직 선발 라인업 데이터 없음");
            return new ResponseEntity<>(responseBody, HttpStatus.NOT_FOUND);
        }
    }
}
