package KickIt.server.domain.fixture.controller;

import KickIt.server.domain.exceptionHandler.GlobalExceptionHandler;
import KickIt.server.domain.fixture.entity.Fixture;
import KickIt.server.domain.fixture.service.FixtureService;
import KickIt.server.global.common.crawler.FixtureCrawler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/fixture")
public class FixtureController {
    @Autowired
    private FixtureService fixtureService;

    // 입력 받은 year과 month의 경기 일정을 크롤링해 중복하지 않은 fixture만 db에 save
    @PostMapping("/crawl")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Map<String, Object>> crawlFixtures (@RequestParam("year") String year, @RequestParam("month") String month){
        FixtureCrawler fixtureCrawler = new FixtureCrawler();
        List<Fixture> fixtureList = new ArrayList<>();
        fixtureList = fixtureCrawler.getFixture(year, month);

        fixtureService.saveFixtures(fixtureList);
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("status", HttpStatus.OK.value());
        responseBody.put("message", "success");
        responseBody.put("data", fixtureList);
        return new ResponseEntity<>(responseBody, HttpStatus.OK);
    }


    /*
    @GetMapping("/{year}/{month}")
    public List<Fixture> getFixtureList (@RequestParam(value="year") String year, @RequestParam(value="month") String month, Model model){
        model.addAttribute("year", year);
        model.addAttribute("month", month);
        return fixtureList;
    }

     */
}
