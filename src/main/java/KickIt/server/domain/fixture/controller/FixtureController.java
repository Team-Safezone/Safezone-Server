package KickIt.server.domain.fixture.controller;

import KickIt.server.domain.fixture.dto.FixtureDto;
import KickIt.server.domain.fixture.entity.Fixture;
import KickIt.server.domain.fixture.entity.FixtureRepository;
import KickIt.server.domain.fixture.service.FixtureService;
import KickIt.server.global.common.crawler.FixtureCrawler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

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

    // 입력 받은 yyyy-MM-dd로 일치하는 날짜의 경기 일정 반환
    @GetMapping()
    public ResponseEntity<Map<String, Object>> getFixturesByDate (@RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date date){
        Map<String, Object> responseBody = new HashMap<>();
        List<FixtureDto.FixtureResponse> responseList = fixtureService.findFixturesByDate(date);
        // 조회해 가져온 데이터가 존재하는 경우 성공, OK status로 반환
        if(!responseList.isEmpty()){
            responseBody.put("status", HttpStatus.OK.value());
            responseBody.put("message", "success");
            responseBody.put("data", responseList);
            return new ResponseEntity<>(responseBody, HttpStatus.OK);
        }
        // 조회한 list가 비어있는 경우 데이터 없음 처리, NOT FOUND로 반환
        else{
            responseBody.put("status", HttpStatus.NOT_FOUND.value());
            responseBody.put("message", "데이터 없음");
            return new ResponseEntity<>(responseBody, HttpStatus.NOT_FOUND);
        }
    }
}
