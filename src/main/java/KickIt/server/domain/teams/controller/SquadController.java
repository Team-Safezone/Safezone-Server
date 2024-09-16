package KickIt.server.domain.teams.controller;

import KickIt.server.domain.teams.entity.Squad;
import KickIt.server.domain.teams.service.SquadService;
import KickIt.server.global.common.crawler.FixtureCrawler;
import KickIt.server.global.common.crawler.SquadCrawler;
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
        return new ResponseEntity<>(responseBody, HttpStatus.OK);
    }
}
