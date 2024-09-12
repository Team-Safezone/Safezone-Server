/* 경기 일정 정보 불러오는 웹 크롤러 FixtureCrawler */
package KickIt.server.global.common.crawler;

import KickIt.server.domain.fixture.entity.Fixture;
import KickIt.server.domain.teams.service.TeamNameConvertService;
import KickIt.server.global.util.WebDriverUtil;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;


// 경기 일정 정보 크롤링하는 FixtureCrawler
@Component
public class FixtureCrawler {
    @Autowired
    private TeamNameConvertService teamNameConvertService;

    // YYYY년 MM 월의 경기 일정 정보를 가져와 Fixture 객체 리스트로 반환하는 getFixture 함수
    public List<Fixture> getFixture(String year, String month) {
        WebDriver driver = WebDriverUtil.getChromeDriver();
        // 입력받은 연도와 월을 합쳐 page 주소 가져옴
        // daum 스포츠 프리미어리그 경기 일정 페이지
        String pageUrl = "https://sports.daum.net/schedule/epl?date=" + year + month;
        // 결과로 반환할 Fixture 객체 리스트
        List<Fixture> fixtureList = new ArrayList<>();

        if (!ObjectUtils.isEmpty(driver)) {
            // 페이지 열고 타임 아웃 관련 처리
            driver.get(pageUrl);
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
            // 현재 시즌 정보를 불러와 변수로 저장 -> 이후 읽어 들인 페이지의 모든 경기에 정보 넣어 줌
            String season = driver.findElement(By.className("emph_day")).getText();

            try{
                // 페이지에서 불러온 경기 일정 table fixtureTable에 저장
                List<WebElement> webElementList = driver.findElements(By.id("scheduleList"));
                WebElement fixtureTable = webElementList.get(0);
                // fixtureTable의 행(tr)들을 담은 list fixtureTrList
                List<WebElement> fixtureTrList = fixtureTable.findElements(By.tagName("tr"));

                // fixtureTrList의 요소마다 내부 정보 parsing해 Fixture 객체로 build해 list에 추가하는 과정
                for (int i = 0; i < fixtureTrList.size(); i++) {
                    WebElement tr = fixtureTrList.get(i);
                    String trClassName = tr.getAttribute("class");
                    // 경기가 있는 날인 경우
                    if (!trClassName.contains("tr_empty")) {
                        // fixtureList에 Fixture build해 삽입
                        // 각 팀의 정보를 포함하는 tr을 담은 list
                        List<WebElement> teams = getTeams(tr);
                        // 각 팀의 이름 EplTeams 배열로 저장
                        String[] teamNames = getTeamNames(teams);
                        // 각 팀의 점수 배열로 저장
                        Integer[] teamScores = getTeamScores(teams);
                        // 한 경기의 경기 고유 id(생성), 시즌 정보, 날짜 및 시간, 홈팀 이름, 원정팀 이름,
                        // 홈팀 점수, 원정팀 점수, 라운드 정보, 경기 상태를 Fixture 객체에 build
                        Fixture fixture = Fixture.builder()
                                .season(season)
                                .date(getFixtureDate(tr))
                                .homeTeam(teamNames[0])
                                .awayTeam(teamNames[1])
                                .homeTeamScore(teamScores[0])
                                .awayteamScore(teamScores[1])
                                .round(getRound(tr))
                                .status(getStatus(tr))
                                .stadium(getStadium(tr))
                                .lineupUrl(getLineupUrl(tr))
                                .build();
                        // fixture 리스트에 삽입
                        fixtureList.add(fixture);
                    }
                }
            }
            catch (Exception e){
                fixtureList = null;
                Logger.getGlobal().log(Level.WARNING, String.format("fixture 크롤링 오류: %s", e.toString()));
            }
            finally {
                WebDriverUtil.quit(driver);
            }
        }
        return fixtureList;
    }

    // 경기 날짜와 시간 정보를 tr에서 가져와 Date format으로 변경한 후 return 하는 함수 getFixtureDate()
    Timestamp getFixtureDate(WebElement row) {
        // table row에서 가져온 날짜 문자열
        String dateStr = row.getAttribute("data-date");
        String year = dateStr.substring(0, 4);
        String month = dateStr.substring(4, 6);
        String day = dateStr.substring(6,8);
        // table row에서 가져온 시간 문자열
        String timeStr = row.findElement(By.className("td_time")).getText().replaceAll("[^0-9:]", "") + ":00";
        // 반환할 Timestamp 객체
        Timestamp fixtureDate = Timestamp.valueOf(String.format("%s-%s-%s %s", year, month, day, timeStr));

        return fixtureDate;
    }

    String getStadium(WebElement row){
        return row.findElement(By.className("td_area")).getText();
    }

    // 경기 상태를 반환하는 함수 getStatus
    // (전) 경기 종료: 0 전반전: 1 하프 타임: 2 후반전: 3 경기 전: 4 연기(예외): 5
    // ! 변경됨 ! (현재) 경기 예정: 0 경기 중: 1 휴식 시간: 2 경기 종료: 3 경기 연기: 4
    int getStatus(WebElement row) {
        switch (row.findElement(By.className("state_game")).getText()) {
            case "종료":
                return 3;
            case "경기전":
                return 0;
            case "전반전":
                return 1;
            case "하프타임":
                return 2;
            case "후반전", "추가시간":
                return 1;
        }
        // 이외의 경우 예외 처리
        return 4;
    }

    // 경기 참여하는 두 팀 정보를 담은 webElement를 반환하는 함수 getTeams
    List<WebElement> getTeams(WebElement row) {
        return row.findElement(By.className("td_team")).findElements(By.tagName("div"));
    }

    // 경기에 참여하는 두 팀의 이름을 담은 배열을 반환하는 함수 getTeamNames
    // 0 번 요소가 홈팀, 1 번 요소가 원정팀
    String[] getTeamNames(List<WebElement> row) {
        String homeTeamName = row.get(0).findElement(By.className("txt_team")).getText();
        String awayTeamName = row.get(1).findElement(By.className("txt_team")).getText();
        return new String[]{teamNameConvertService.convertFromKrName(homeTeamName), teamNameConvertService.convertFromKrName(awayTeamName)};
    }

    // 경기에 참여하는 두 팀의 점수를 담은 배열을 반환하는 함수 getTeamScores
    // 0 번 요소가 홈팀, 1 번 요소가 원정팀
    // 아직 경기 치르지 않은 경우 null로 처리
    Integer[] getTeamScores(List<WebElement> row) {
        String homeTeamScore = row.get(0).findElement(By.className("num_score")).getText().replaceAll("[^0-9]", "");
        String awayTeamScore = row.get(1).findElement(By.className("num_score")).getText().replaceAll("[^0-9]", "");
        return new Integer[]{homeTeamScore.isEmpty() ? null : Integer.parseInt(homeTeamScore), awayTeamScore.isEmpty() ? null : Integer.parseInt(awayTeamScore)};
    }

    // 경기 라운드 정보 가져오는 함수 getRound
    int getRound(WebElement row) {
        return Integer.parseInt(row.findElement(By.className("td_tv")).getText().replaceAll("[^0-9]", ""));
    }

    // 이후 경기 라인업 정보를 가져오기 위한 경기 상세 페이지 URL 정보를 가져오는 함수 getLineupURL
    // 경기 상태가 중단된 경우 경기 기록 버튼이 비활성화 => 링크 가져올 수 없어 error => null 값 반환
    String getLineupUrl(WebElement row) {
        // a 태그에 바로 접근 불가해 버튼의 내부 html을 가져와 a 태그 부분을 parsing
        String innerHTML = row.findElement(By.className("td_btn")).getDomProperty("innerHTML");
        // <a href=" 끝나는 index (주소 시작 index)
        int start = innerHTML.indexOf("<a href=\"") + 10;
        // " class~ 시작하는 index(주소 마지막 index)
        int end = innerHTML.indexOf("\" class");
        // 경기 중단된 경우 버튼 비활성화로 a 태그 내부 html에 포함되지 않아 start, end가 -1이 됨
        // 아 경우 null 반환
        if (start < 0 || end < 0){
            return null;
        }
        // 경기 상세 페이지 url만을 잘리 반환
        return innerHTML.substring(start, end);
    }
}
