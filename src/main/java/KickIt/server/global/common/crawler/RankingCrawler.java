package KickIt.server.global.common.crawler;

import KickIt.server.domain.teams.entity.Ranking;
import KickIt.server.domain.teams.entity.Squad;
import KickIt.server.domain.teams.entity.SquadRepository;
import KickIt.server.domain.teams.service.TeamNameConvertService;
import KickIt.server.global.util.WebDriverUtil;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.lang.Integer.parseInt;

// 시즌별 랭킹 정보를 가지고 오는 crawler
// 기존 TeamInfoCrawler에서 Squad Crawler와 겹치는 부분은 제외하고 승-무-패 횟수 크롤링 부분 추가
// 차후 TeamInfoCrawler는 삭제 예정
@Component
public class RankingCrawler {
    @Autowired
    TeamNameConvertService teamNameConvertService;
    @Autowired
    SquadRepository squadRepository;

    public List<Ranking> getRanking() {
        WebDriver driver = WebDriverUtil.getChromeDriver();

        // 웹 페이지로 이동
        String url = "https://sports.daum.net/record/epl/team";

        List<Ranking> rankingList = new ArrayList<>();

        if (!ObjectUtils.isEmpty(driver)) {
            try {
                // 페이지 열고 타임 아웃 관련 처리
                driver.get(url);
                driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(15));

                List<WebElement> teaminfoRows = driver.findElements(By.cssSelector("tbody > tr"));
                for (WebElement row : teaminfoRows) {
                    if (teaminfoRows.indexOf(row) >= teaminfoRows.size()) {
                        Logger.getGlobal().log(Level.WARNING, "인덱스가 리스트 범위를 벗어났습니다: " + teaminfoRows.indexOf(row));
                        continue; // 혹은 다른 적절한 처리
                    }

                    String season = driver.findElement(By.className("emph_day")).getText();

                    String teamName = getTeamName(row);
                    Squad squad = squadRepository.findBySeasonAndTeam(season, teamNameConvertService.convertFromKrName(teamName)).orElse(null);
                    if(squad == null){ Logger.getGlobal().log(Level.INFO, String.format("squad 찾을 수 없음: season %s, teamname %s", season, teamName));}
                    else{
                        Ranking rank = Ranking.builder()
                                .squad(squad)
                                .teamRank(getRanking(row))
                                .matchCount(getMatchCount(row))
                                .winCount(getWinCount(row))
                                .drawCount(getDrawCount(row))
                                .loseCount(getLoseCount(row))
                                .points(getPoint(row))
                                .build();
                        rankingList.add(rank);
                    }
                }

            }
            catch (Exception e) {
                Logger.getGlobal().log(Level.INFO, "RankingCrawler 오류: " + e);
                rankingList = null;
            }
            finally {
                WebDriverUtil.quit(driver);
            }
        }

        return rankingList;

    }

    int getRanking(WebElement row){
        String rankingAll = row.findElement(By.className("td_rank")).getText();
        int rankingInt = parseInt(rankingAll.replaceAll("[^0-9].*",""));
        return rankingInt;
    }

    String getTeamName(WebElement row) {
        String team = row.findElement(By.className("txt_name")).getText();
        return team;
    }

    int getMatchCount(WebElement row){
        int matchCount = parseInt(row.findElements(By.tagName("td")).get(2).getText());
        return matchCount;
    }

    int getWinCount(WebElement row){
        int winCount = parseInt(row.findElements(By.tagName("td")).get(3).getText());
        return winCount;
    }

    int getDrawCount(WebElement row){
        int drawCount = parseInt(row.findElements(By.tagName("td")).get(4).getText());
        return drawCount;
    }

    int getLoseCount(WebElement row){
        int loseCount = parseInt(row.findElements(By.tagName("td")).get(5).getText());
        return loseCount;
    }

    int getPoint(WebElement row){
        int point = parseInt(row.findElements(By.tagName("td")).get(9).getText());
        return point;
    }
}
