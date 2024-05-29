package KickIt.server.global.common.crawler;

import KickIt.server.domain.fixture.entity.Fixture;
import KickIt.server.domain.lineup.entity.MatchLineup;
import KickIt.server.domain.lineup.entity.TeamLineup;
import KickIt.server.domain.teams.EplTeams;
import KickIt.server.domain.teams.entity.Player;
import KickIt.server.global.util.WebDriverUtil;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.util.ObjectUtils;

import java.time.Duration;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

// 경기 선발 라인업을 크롤링하는 LineupCrawler
public class LineupCrawler {
    MatchLineup getLineup(Fixture fixture){
        WebDriver driver = WebDriverUtil.getChromeDriver();
        String pageUrl = "https://sports.daum.net/" + fixture.getLineupUrl() + "?tab=lineup";
        // String pageUrl = "https://sports.daum.net/match/80074533?tab=lineup";
        MatchLineup matchLineup = new MatchLineup();

        if (!ObjectUtils.isEmpty(driver)) {
            // 페이지 열고 타임 아웃 관련 처리
            driver.get(pageUrl);
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));

            // 선발 라인업 정보 전체 포함하는 WebElement
            WebElement homeElement = driver.findElement(By.className("lineup_vs1"));
            WebElement awayElement = driver.findElement(By.className("lineup_vs2"));

            try {
                // 아직 선발 라인업 업로드하지 않아 txt_lineup class id element를 찾을 수 없는 경우
                // 일정 시간 간격 동안 해당 element가 생길 때까지 기다림
                // => 성공(element 존재) 시 바로 wait 끝내고 나머지 요소들 모두 크롤링
                // => 실패 시 예외 처리
                // 이후 getLineup 함수 자체를 5 분, 10 분 간격으로 null이 아닐 때까지 반복 실행해 보면 될 듯.
                // 기다리는 간격 서버 과부하 없게 잘 조정하는 과정 필요!
                WebDriverWait wait = new WebDriverWait(driver, Duration.ofMinutes(15));
                wait.until(ExpectedConditions.presenceOfElementLocated(By.className("txt_lineup")));

                // 홈팀과 원정팀의 선발 라인업 포메이션 정보를 문자열로 크롤링
                String homeForm = getForm(homeElement);
                String awayForm = getForm(awayElement);

                // 홈팀과 원정팀의 감독 정보를 담은 element
                WebElement DirectorsElement = driver.findElement(By.className("substitute_coach"));
                // 홈팀과 원정팀의 감독 정보를 문자열로 크롤링
                String homeDirector = getDirector(DirectorsElement)[0];
                String awayDirector = getDirector(DirectorsElement)[1];

                // 홈팀 선수 리스트와 원정팀 선수 리스트 정보를 크롤링해 Player 객체 List로 저장
                // 이때 포메이션대로 getPlayers 함수에서 반환한 전체 선수 리스트를 분할해 저장
                String[] homeFormNum = homeForm.split("-");
                ArrayList<List<Player>> homePlayers = new ArrayList<>();
                ArrayList<Player> homeTotalPlayer = getPlayers(homeElement, fixture.getHomeTeam());
                int startIndex = 0;
                int endIndex = Integer.parseInt(homeFormNum[0]) + 1;
                homePlayers.add(homeTotalPlayer.subList(startIndex, endIndex));
                for(int i = 1; i < homeFormNum.length; i++){
                    startIndex = endIndex;
                    endIndex = startIndex + Integer.parseInt(homeFormNum[i]);
                    homePlayers.add(homeTotalPlayer.subList(startIndex, endIndex));
                }

                String[] awayFormNum = awayForm.split("-");
                ArrayList<List<Player>> awayPlayers = new ArrayList<>();
                ArrayList<Player> awayTotalPlayer = getPlayers(awayElement, fixture.getAwayTeam());
                startIndex = 0;
                endIndex = Integer.parseInt(awayFormNum[0]) + 1;
                awayPlayers.add(awayTotalPlayer.subList(startIndex, endIndex));
                for(int i = 1; i < awayFormNum.length; i++){
                    startIndex = endIndex;
                    endIndex = startIndex + Integer.parseInt(awayFormNum[i]);
                    awayPlayers.add(awayTotalPlayer.subList(startIndex, endIndex));
                }

                // 각 팀의 후보 선수 리스트를 가져오기 위한 후보 선수 정보가 담긴 Webelement > 그 중 li 요소 찾음
                List<WebElement> homeBenchElement = driver.findElements(By.className("list_substitute")).get(2).findElements(By.cssSelector("li"));
                List<WebElement> awayBenchElement = driver.findElements(By.className("list_substitute")).get(3).findElements(By.cssSelector("li"));

                // 홈팀과 원정팀의 후보 선수 리스트를 크롤링해 Player 객체 List로 저장
                ArrayList<Player> homeBenchPlayers = getBenchPlayers(homeBenchElement, fixture.getHomeTeam());
                ArrayList<Player> awayBenchPlayers = getBenchPlayers(awayBenchElement, fixture.getAwayTeam());

                // 위에서 크롤링해 온 정보 바탕으로 홈팀 TeamLineup 클래스 객체 build
                TeamLineup homeTeamLineup =
                        TeamLineup.builder()
                                .team(fixture.getHomeTeam())
                                .form(homeForm)
                                .players(homePlayers)
                                .director(homeDirector)
                                .benchPlayers(homeBenchPlayers)
                                .build();
                // 위에서 크롤링해 온 정보 바탕으로 원정팀 TeamLineup 클래스 객체 build
                TeamLineup awayTeamLineup =
                        TeamLineup.builder()
                                .team(fixture.getAwayTeam())
                                .form(awayForm)
                                .players(awayPlayers)
                                .director(awayDirector)
                                .benchPlayers(awayBenchPlayers)
                                .build();

                // 위에서 만든 홈팀 / 원정팀 TeamLineup 객체들 포함해 경기의 matchLineup 객체 build
                matchLineup =
                        MatchLineup.builder()
                                .id(fixture.getId())
                                .homeTeam(fixture.getHomeTeam())
                                .awayTeam(fixture.getAwayTeam())
                                .homeTeamForm(homeForm)
                                .awayTeamForm(awayForm)
                                .homeTeamLineup(homeTeamLineup)
                                .awayTeamLineup(awayTeamLineup)
                                .build();
            } catch (Exception e) {
                Logger.getGlobal().log(Level.INFO, "lineup driver 로딩 시간 초과");
                matchLineup = null;
            }
            finally {
                WebDriverUtil.quit(driver);
            }
        }
        return matchLineup;
    }
    // 팀별 포메이션 정보 반환하는 함수
    String getForm(WebElement element){ return element.findElement(By.className("txt_lineup")).getText(); }

    // 팀별 감독 정보 String 배열로 반환하는 함수
    String[] getDirector(WebElement element){
        String homeDirector = element.findElements(By.className("item_substitute")).get(0).getText();
        String awayDirector = element.findElements(By.className("item_substitute")).get(1).getText();
        return new String[]{homeDirector, awayDirector};
    }

    // 매개변수로 주어진 WebElement에서 선수 리스트를 가져오는 함수
    ArrayList<Player> getPlayers(WebElement element, EplTeams teamName){
        // 반환할 Player 객체 리스트 생성
        ArrayList<Player> players = new ArrayList<>();
        // 주어진 매개변수 Element에서 선수 명단이 있는 Element 찾아 가져옴
        List<WebElement> playersElement = element.findElements(By.className("txt_name"));
        // 각 팀 선수 인원 수만큼 선수 명단 Element에서 필요한 선수 정보 가져와 Player 객체로 build -> players list에 추가
        for (int i = 0; i < 11; i++){
            String playerInfo = playersElement.get(i).getText();
            players.add(Player.builder()
                    .id(UUID.randomUUID())
                    .team(teamName)
                    .number(Integer.parseInt(playerInfo.replaceAll("[^0-9]", "")))
                    .name(playerInfo.replaceAll("[^가-힣]", ""))
                    .build());
        }
        return players;
    }

    // 매개변수로 주어진 WebElement에서 후보 선수 리스트를 가져오는 함수
    ArrayList<Player> getBenchPlayers(List<WebElement> elements, EplTeams teamName){
        // 반환할 Player 객체 리스트 생성
        ArrayList<Player> benchPlayers = new ArrayList<>();
        for(int i = 0; i < elements.size(); i++) {
            String playerNum = elements.get(i).findElement(By.className("number_g")).getText();
            String playerName = elements.get(i).findElement(By.className("txt_name")).getText();
            benchPlayers.add(Player.builder()
                    .id(UUID.randomUUID())
                    .team(teamName)
                    .number(Integer.parseInt(playerNum.replaceAll("[^0-9]", "")))
                    .name(playerName)
                    .build());
        }
        return benchPlayers;
    }
}
