package KickIt.server.domain.teams.controller;

import KickIt.server.domain.fixture.dto.FixtureDto;
import KickIt.server.domain.teams.EplTeams;
import KickIt.server.domain.teams.dto.TeaminfoDto;
import KickIt.server.domain.teams.entity.Teaminfo;
import KickIt.server.domain.teams.service.TeaminfoService;
import KickIt.server.global.common.crawler.TeaminfoCrawler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/teaminfo")
public class TeaminfoController {

    private final TeaminfoService teaminfoService;

    public TeaminfoController(TeaminfoService teaminfoService) {
        this.teaminfoService = teaminfoService;
    }

    @PostMapping("/crawl")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Map<String, Object>> crawlTeaminfo(@RequestParam("soccerSeason") int soccerSeason) {
        List<Teaminfo> teaminfoList = new ArrayList<>();

        teaminfoList = teaminfoService.saveTeaminfo(soccerSeason);

        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("status", HttpStatus.OK.value());
        responseBody.put("message", "success");
        responseBody.put("soccerTeams", teaminfoList);
        return new ResponseEntity<>(responseBody, HttpStatus.OK);
    }

    @GetMapping()
    public ResponseEntity<Map<String, Object>> getTeaminfo(@RequestParam("soccerSeason") String season) {
        Map<String, Object> responseBody = new HashMap<>();
        List<TeaminfoDto.TeaminfoResponse> responseList;
        responseList = teaminfoService.findTeaminfoBySeason(season);

        // 조회해 가져온 데이터가 존재하는 경우 성공, OK status로 반환
        if (!responseList.isEmpty()) {
            responseBody.put("status", HttpStatus.OK.value());
            responseBody.put("message", "success");
            responseBody.put("soccerTeams", responseList);
            return new ResponseEntity<>(responseBody, HttpStatus.OK);
        }
        // 조회한 list가 비어있는 경우 데이터 없음 처리, NOT FOUND로 반환
        else {
            responseBody.put("status", HttpStatus.NOT_FOUND.value());
            responseBody.put("message", "데이터 없음");
            return new ResponseEntity<>(responseBody, HttpStatus.NOT_FOUND);
        }

    }
}
