package KickIt.server.domain.teams.controller;

import KickIt.server.domain.teams.entity.Teaminfo;
import KickIt.server.domain.teams.service.TeaminfoService;
import KickIt.server.global.common.crawler.TeaminfoCrawler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/teaminfo")
public class TeaminfoController {

    private final TeaminfoService teaminfoService;

    public TeaminfoController(TeaminfoService teaminfoService){
        this.teaminfoService = teaminfoService;
    }

    @PostMapping("/crawl")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Map<String, Object>> crawlTeaminfo (@RequestParam("soccerSeason") int soccerSeason){
        List<Teaminfo> teaminfoList = new ArrayList<>();

        teaminfoList = teaminfoService.saveTeaminfo(soccerSeason);

        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("status", HttpStatus.OK.value());
        responseBody.put("message", "success");
        responseBody.put("soccerTeams", teaminfoList);
        return new ResponseEntity<>(responseBody, HttpStatus.OK);
    }

}
