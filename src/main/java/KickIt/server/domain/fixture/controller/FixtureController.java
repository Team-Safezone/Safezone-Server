package KickIt.server.domain.fixture.controller;

import KickIt.server.domain.fixture.dto.FixtureDto;
import KickIt.server.domain.fixture.entity.Fixture;
import KickIt.server.domain.fixture.entity.FixtureRepository;
import KickIt.server.domain.fixture.service.FixtureService;
import KickIt.server.domain.teams.service.TeamNameConvertService;
import KickIt.server.global.common.crawler.FixtureCrawler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/fixture")
public class FixtureController {
    @Autowired
    private FixtureService fixtureService;
    @Autowired
    private TeamNameConvertService teamNameConvertService;
    @Autowired
    private FixtureCrawler fixtureCrawler;

    // 입력 받은 year과 month의 경기 일정을 크롤링해 중복하지 않은 fixture만 db에 save
    @PostMapping("/crawl")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Map<String, Object>> crawlFixtures (@RequestParam("year") String year, @RequestParam("month") String month){
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
    public ResponseEntity<Map<String, Object>> getFixturesByDate (@RequestParam("date") @DateTimeFormat(pattern = "yyyy/MM/dd") Date date, @RequestParam(value="teamName", required = false) String teamName){
        Map<String, Object> responseBody = new HashMap<>();
        List<FixtureDto.FixtureResponse> responseList;
        // teamName이 입력되지 않은 경우 날짜만으로 경기 조회
        if(teamName == null){
            responseList = fixtureService.findFixturesByDate(date);
        }
        // teamName이 입력된 경우 날짜와 팀으로 경기 조회
        else {
            String team = teamNameConvertService.convertFromKrName(teamName);
            // 입력된 teamName으로 EplTeam이 찾아지지 않는 경우 Bad Request 처리
            if(team == null){
                responseBody.put("status", HttpStatus.BAD_REQUEST.value());
                responseBody.put("message", "팀 이름 입력 오류");
                responseBody.put("isSuccess", false);
                return new ResponseEntity<>(responseBody, HttpStatus.BAD_REQUEST);
            }
            responseList = fixtureService.findFixturesByDateAndTeam(date, team);
        }
        // 조회해 가져온 데이터가 존재하는 경우 성공, OK status로 반환
        if(!responseList.isEmpty()){
            responseBody.put("status", HttpStatus.OK.value());
            responseBody.put("message", "success");
            responseBody.put("isSuccess", true);
            responseBody.put("data", responseList);
            return new ResponseEntity<>(responseBody, HttpStatus.OK);
        }
        // 조회한 list가 비어있는 경우 데이터 없음 처리, NOT FOUND로 반환
        else{
            responseBody.put("status", HttpStatus.NOT_FOUND.value());
            responseBody.put("isSuccess", false);
            responseBody.put("message", "데이터 없음");
            return new ResponseEntity<>(responseBody, HttpStatus.NOT_FOUND);
        }
    }

    // 입력받은 yyyy-MM으로 해당 월의 경기 일정 모두 반환
    @GetMapping("/dates")
    public ResponseEntity<Map<String, Object>> getFixturesByMonth(@RequestParam("yearMonth") @DateTimeFormat(pattern = "yyyy/MM") Date yearMonth, @RequestParam(value="teamName", required = false) String teamName){
        Map<String, Object> responseBody = new HashMap<>();
        FixtureDto.FixtureDateResponse response;
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(yearMonth);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;

        // teamName이 입력되지 않은 경우 월만으로 경기 조회
        if(teamName == null){
            response = fixtureService.findFixturesByMonth(year, month);
            // 입력된 teamName으로 EplTeam이 찾아지지 않는 경우 Bad Request 처리
        }
        // teamName이 입력된 경우 날짜와 팀으로 경기 조회
        else{
            String team = teamNameConvertService.convertFromKrName(teamName);
            if(team == null){
                responseBody.put("status", HttpStatus.BAD_REQUEST.value());
                responseBody.put("message", "팀 이름 입력 오류");
                responseBody.put("isSuccess", false);
                return new ResponseEntity<>(responseBody, HttpStatus.BAD_REQUEST);
            }
            response = fixtureService.findFixtureByMonthAndTeam(year, month, team);
        }
        // 조회해 가져온 데이터가 존재하는 경우 성공, OK status로 반환
        if(response != null){
            responseBody.put("status", HttpStatus.OK.value());
            responseBody.put("message", "success");
            responseBody.put("data", response);
            responseBody.put("isSuccess", true);
            return new ResponseEntity<>(responseBody, HttpStatus.OK);
        }
        // 조회한 list가 비어있는 경우 데이터 없음 처리, NOT FOUND로 반환
        else{
            responseBody.put("status", HttpStatus.NOT_FOUND.value());
            responseBody.put("message", "데이터 없음");
            responseBody.put("isSuccess", false);
            return new ResponseEntity<>(responseBody, HttpStatus.NOT_FOUND);
        }
    }

    @PatchMapping("/editScore")
    public ResponseEntity<Map<String, Object>> editFixtureScore(@RequestParam("fixtureId") Long fixtureId, @RequestParam("homeTeamScore") Integer homeTeamScore, @RequestParam("awayTeamScore") Integer awayTeamScore){
        Boolean isSuccess = fixtureService.updateFixtureScore(fixtureId, homeTeamScore, awayTeamScore);
        Map<String, Object> responseBody = new HashMap<>();
        if(isSuccess){
            responseBody.put("status", HttpStatus.OK.value());
            responseBody.put("message", "success");
            return new ResponseEntity<>(responseBody, HttpStatus.OK);
        }
        else{
            responseBody.put("status", HttpStatus.NOT_FOUND.value());
            responseBody.put("message", "찾는 경기 없음");
            return new ResponseEntity<>(responseBody, HttpStatus.NOT_FOUND);
        }
    }
}
