package KickIt.server.global.common.crawler;

import KickIt.server.domain.fixture.entity.Fixture;
import KickIt.server.domain.teamInfo.Teaminfo;
import KickIt.server.domain.teams.EplTeams;
import KickIt.server.global.util.WebDriverUtil;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.util.ObjectUtils;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

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
                    EplTeams[] teamName = getTeamName(row);

                    Teaminfo teaminfo = Teaminfo.builder()
                            .ranking(getRanking(row))
                            .team(teamName[0])
                            .logoImg(getLogoImg(row))
                            .build();
                    teaminfoList.add(teaminfo);
                }

            } finally {
                WebDriverUtil.quit(driver);
            }
        }

        return teaminfoList;

    }



    String getRanking(WebElement row){
        String rankingAll = row.findElement(By.className("td_rank")).getText();
        String rankingInt = rankingAll.replaceAll("[^0-9].*","");
        return rankingInt;
    }

    EplTeams[] getTeamName(WebElement row) {
        String team = row.findElement(By.className("txt_name")).getText();
        return new EplTeams[]{EplTeams.valueOfKrName(team)};
    }

    String getLogoImg(WebElement row) {
        try {
            WebElement logoImg = row.findElement(By.cssSelector(".td_name .wrap_thumb img"));
            String logoUrl = logoImg.getAttribute("src");
            return logoUrl;
        } catch (Exception e) {
            Logger.getGlobal().log(Level.WARNING, "등록된 로고가 없습니다.: " + e.toString());
            return null;
        }
    }

}
