package KickIt.server.global.common.crawler;

import KickIt.server.domain.teams.entity.Teaminfo;
import KickIt.server.global.util.WebDriverUtil;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.lang.Integer.parseInt;


@Component
public class TeaminfoCrawler {

    public List<Teaminfo> getTeaminfo(int season) {
        WebDriver driver = WebDriverUtil.getChromeDriver();


        // 웹 페이지로 이동
        String url = "https://sports.daum.net/record/epl/team?season=" + season;

        List<Teaminfo> teaminfoList = new ArrayList<>();

        if (!ObjectUtils.isEmpty(driver)) {
            try {
                // 페이지 열고 타임 아웃 관련 처리
                driver.get(url);
                driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(20));

                List<WebElement> teaminfoRows = driver.findElements(By.cssSelector("tbody > tr"));
                for (WebElement row : teaminfoRows) {
                    if (teaminfoRows.indexOf(row) >= teaminfoRows.size()) {
                        Logger.getGlobal().log(Level.WARNING, "인덱스가 리스트 범위를 벗어났습니다: " + teaminfoRows.indexOf(row));
                        continue; // 혹은 다른 적절한 처리
                    }

                    String teamName = getTeamName(row);
                    Teaminfo teaminfo = Teaminfo.builder()
                            .ranking(getRanking(row))
                            .team(teamName)
                            .logoUrl(getLogoUrl(row))
                            .season(getSeason(driver))
                            .build();
                    teaminfoList.add(teaminfo);
                }

            } finally {
                WebDriverUtil.quit(driver);
            }
        }

        return teaminfoList;

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

    String getLogoUrl(WebElement row) {
        try {
            WebElement logoImg = row.findElement(By.cssSelector(".td_name .wrap_thumb img"));
            String logoUrl = logoImg.getAttribute("src");
            return logoUrl;
        } catch (Exception e) {
            Logger.getGlobal().log(Level.WARNING, "등록된 로고가 없습니다.: " + e.toString());
            return null;
        }
    }

    String getSeason(WebDriver driver){
        WebElement element = driver.findElement(By.cssSelector(".emph_day.txt_num"));
        String seasonInfo = element.getText();
        return seasonInfo;
    }

}
