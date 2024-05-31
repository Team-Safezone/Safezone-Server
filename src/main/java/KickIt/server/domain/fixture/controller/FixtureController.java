package KickIt.server.domain.fixture.controller;
import KickIt.server.domain.fixture.dto.FixtureDto;
import KickIt.server.domain.fixture.entity.Fixture;
import KickIt.server.domain.fixture.service.FixtureService;
import KickIt.server.global.common.crawler.FixtureCrawler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/fixture")
public class FixtureController {
    @Autowired
    private FixtureService fixtureService;

    @PostMapping("/{year}/{month}")
    public ResponseEntity crawlFixtures (@PathVariable("year") String year, @PathVariable("month") String month){
        FixtureCrawler fixtureCrawler = new FixtureCrawler();
        List<Fixture> fixtureList = new ArrayList<>();
        try{
            fixtureList = fixtureCrawler.getFixture(year, month);
            Thread.sleep(5000);
            fixtureService.saveFixtures(fixtureList);
            return ResponseEntity.ok("크롤링 성공");
        }
        catch (Exception e){
            return ResponseEntity.status(500).body("크롤링이 실패했습니다.");
        }
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
