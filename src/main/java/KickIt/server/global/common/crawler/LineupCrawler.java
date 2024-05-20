package KickIt.server.global.common.crawler;

import KickIt.server.domain.fixture.entity.Fixture;
import KickIt.server.domain.lineup.entity.MatchLineup;
import KickIt.server.domain.teams.EplTeams;
import KickIt.server.domain.teams.entity.Player;
import KickIt.server.global.util.WebDriverUtil;
import ch.qos.logback.core.joran.sanity.Pair;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.springframework.util.ObjectUtils;

import java.lang.reflect.Array;
import java.time.Duration;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

// 경기 선발 라인업을 크롤링하는 LineupCrawler
public class LineupCrawler {
    void getLineup(Fixture fixture){
        WebDriver driver = WebDriverUtil.getChromeDriver();
        String pageUrl = "https://sports.daum.net/" + fixture.getLineupUrl() + "?tab=lineup";
        if (!ObjectUtils.isEmpty(driver)) {
            // 페이지 열고 타임 아웃 관련 처리
            driver.get(pageUrl);
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));

            // 선발 라인업 정보 전체 포함하는 WebElement
            WebElement homeElement = driver.findElement(By.className("lineup_vs1"));
            WebElement awayElement = driver.findElement(By.className("lineup_vs2"));

            String homeForm = homeElement.findElement(By.className("txt_lineup")).getText();
            String awayForm = awayElement.findElement(By.className("txt_lineup")).getText();

            EplTeams homeTeam = EplTeams.valueOfKrName(homeElement.findElement(By.className("tit_team")).getText());
            EplTeams awayTeam = EplTeams.valueOfKrName(awayElement.findElement(By.className("tit_team")).getText());

            List<WebElement> homePlayersElement = homeElement.findElements(By.className("txt_name"));
            List<WebElement> awayPlayersElement = awayElement.findElements(By.className("txt_name"));

            ArrayList<Player> homePlayers = new ArrayList<>();
            ArrayList<Player> awayPlayers = new ArrayList<>();

            for (int i = 0; i < 11; i++){
                String homePlayer = homePlayersElement.get(i).getText();
                String awayPlayer = awayPlayersElement.get(i).getText();
                homePlayers.add(Player.builder()
                        .id(UUID.randomUUID())
                        .team(homeTeam)
                        .number(Integer.parseInt(homePlayer.replaceAll("[^0-9]", "")))
                        .name(homePlayer.replaceAll("[^가-힣]", ""))
                        .build());

                awayPlayers.add(Player.builder()
                        .id(UUID.randomUUID())
                        .team(awayTeam)
                        .number(Integer.parseInt(awayPlayer.replaceAll("[^0-9]", "")))
                        .name(awayPlayer.replaceAll("[^가-힣]", ""))
                        .build());
            }

            Logger.getGlobal().log(Level.INFO, homeForm);
            Logger.getGlobal().log(Level.INFO, awayForm);

            for (int i = 0; i < 11; i++){
                Logger.getGlobal().log(Level.INFO, String.format("%s %s %s %s", homePlayers.get(i).getId(), homePlayers.get(i).getTeam(), homePlayers.get(i).getNumber(), homePlayers.get(i).getName()));
            }
            for (int i = 0; i < 11; i++){
                Logger.getGlobal().log(Level.INFO, String.format("%s %s %s %s", awayPlayers.get(i).getId(), awayPlayers.get(i).getTeam(), awayPlayers.get(i).getNumber(), awayPlayers.get(i).getName()));
            }
        }
        driver.quit();
    }
}
