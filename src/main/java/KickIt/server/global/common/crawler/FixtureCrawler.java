/* 경기 일정 정보 불러오는 웹 크롤러 FixtureCrawler */
package KickIt.server.global.common.crawler;

import KickIt.server.domain.fixture.entity.Fixture;
import KickIt.server.domain.teams.EplTeams;
import KickIt.server.global.util.WebDriverUtil;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.springframework.util.ObjectUtils;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

// 경기 일정 정보 크롤링하는 FixtureCrawler
public class FixtureCrawler {
    // YYYY년 MM 월의 경기 일정 정보를 가져와 Fixture 객체 리스트로 반환하는 getFixture 함수
    List<Fixture> getFixture(String year, String month){
        WebDriver driver = WebDriverUtil.getChromeDriver();
        // 입력받은 연도와 월을 합쳐 page 주소 가져옴
        // daum 스포츠 프리미어리그 경기 일정 페이지
        String pageUrl = "https://sports.daum.net/schedule/epl?date=" + year + month;
        // 결과로 반환할 Fixture 객체 리스트
        List<Fixture> fixtureList = new ArrayList<>();

        if(!ObjectUtils.isEmpty(driver)){
            // 페이지 열고 타임 아웃 관련 처리
            driver.get(pageUrl);
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
            // 현재 시즌 정보를 불러와 변수로 저장 -> 이후 읽어 들인 페이지의 모든 경기에 정보 넣어 줌
            String season = driver.findElement(By.className("emph_day")).getText();
            // 페이지에서 불러온 경기 일정 table fixtureTable에 저장
            List<WebElement> webElementList = driver.findElements(By.id("scheduleList"));
            WebElement fixtureTable = webElementList.get(0);
            // fixtureTable의 행(tr)들을 담은 list fixtureTrList
            List<WebElement> fixtureTrList = fixtureTable.findElements(By.tagName("tr"));

            // fixtureTrList의 요소마다 내부 정보 parsing해 Fixture 객체로 build해 list에 추가하는 과정
            for(int i = 0; i < fixtureTrList.size(); i++){
                WebElement tr = fixtureTrList.get(i);
                String trClassName = tr.getAttribute("class");
                // 경기가 있는 날인 경우
                if (!trClassName.contains("tr_empty")){
                    // fixtureList에 Fixture build해 삽입
                    // 각 팀의 정보를 포함하는 tr을 담은 list
                    List<WebElement> teams = getTeams(tr);
                    // 각 팀의 이름 EplTeams 배열로 저장
                    EplTeams[] teamNames = getTeamNames(teams);
                    // 각 팀의 점수 배열로 저장
                    Integer[] teamScores = getTeamScores(teams);
                    // 한 경기의 경기 고유 id(생성), 시즌 정보, 날짜 및 시간, 홈팀 이름, 원정팀 이름,
                    // 홈팀 점수, 원정팀 점수, 라운드 정보, 경기 상태를 Fixture 객체에 build
                    Fixture fixture = Fixture.builder()
                            .id(UUID.randomUUID())
                            .season(season)
                            .date(getFixtureDate(tr))
                            .homeTeam(teamNames[0])
                            .awayTeam(teamNames[1])
                            .homeTeamScore(teamScores[0])
                            .awayteamScore(teamScores[1])
                            .round(getRound(tr))
                            .status(getStatus(tr))
                            .build();
                    // fixture 리스트에 삽입
                    fixtureList.add(fixture);
                }
            }
        }

        driver.quit();
        return fixtureList;
    }

    // 경기 날짜와 시간 정보를 tr에서 가져와 Date format으로 변경한 후 return 하는 함수 getFixtureDate()
    Date getFixtureDate(WebElement row){
        // table row에서 가져온 날짜 문자열
        String dateStr = row.getAttribute("data-date");
        // table row에서 가져온 시간 문자열
        String timeStr = row.findElement(By.className("td_time")).getText().replaceAll("[^0-9:]", "");
        String fixtureDateStr = dateStr+timeStr+":00";
        // 가져온 날짜, 시간 문자열을 Date로 변환할 SimpleDataFormat 객체
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHH:mm:ss");
        // 반환할 Date 객체
        Date fixtureDate = null;
        // 가져온 날짜, 시간 문자열을 Date format으로 변환
        try {
            fixtureDate = sdf.parse(fixtureDateStr);

        } catch (Exception e){
            // TODO: handle exception
        }
        return fixtureDate;
    }

    // 경기 상태를 반환하는 함수 getStatus
    // 경기 종료: 0 전반전: 1 후반전: 2 경기 전: 3 연기(예외): 4
    int getStatus(WebElement row){
        switch (row.findElement(By.className("td_team")).findElement(By.className("state_game")).getText()){
            case "종료":
                return 0;
            case "경기전":
                return 3;
            case "전반전":
                return 1;
            case "후반전":
                return 2;
        }
        // 이외의 경우 예외 처리
        return 4;
    }
    // 경기 참여하는 두 팀 정보를 담은 webElement를 반환하는 함수 getTeams
    List<WebElement> getTeams(WebElement row){
        return row.findElement(By.className("td_team")).findElements(By.tagName("div"));
    }
    // 경기에 참여하는 두 팀의 이름을 담은 배열을 반환하는 함수 getTeamNames
    // 0 번 요소가 홈팀, 1 번 요소가 원정팀
    EplTeams[] getTeamNames(List<WebElement> row){
        String homeTeamName = row.get(0).findElement(By.className("txt_team")).getText();
        String awayTeamName = row.get(1).findElement(By.className("txt_team")).getText();
        return new EplTeams[]{EplTeams.valueOfKrName(homeTeamName), EplTeams.valueOfKrName(awayTeamName)};
    }
    // 경기에 참여하는 두 팀의 점수를 담은 배열을 반환하는 함수 getTeamScores
    // 0 번 요소가 홈팀, 1 번 요소가 원정팀
    // 아직 경기 치르지 않은 경우 null로 처리
    Integer[] getTeamScores(List<WebElement> row){
        String homeTeamScore = row.get(0).findElement(By.className("num_score")).getText().replaceAll("[^0-9]","");
        String awayTeamScore = row.get(1).findElement(By.className("num_score")).getText().replaceAll("[^0-9]","");
        return new Integer[]{homeTeamScore.isEmpty()?null:Integer.parseInt(homeTeamScore), awayTeamScore.isEmpty()?null:Integer.parseInt(awayTeamScore)};
    }
    // 경기 라운드 정보 가져오는 함수 getRound
    int getRound(WebElement row){
        return Integer.parseInt(row.findElement(By.className("td_tv")).getText().replaceAll("[^0-9]", ""));
    }
}