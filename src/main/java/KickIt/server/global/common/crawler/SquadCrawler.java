package KickIt.server.global.common.crawler;

import KickIt.server.domain.teams.EplTeams;
import KickIt.server.domain.teams.PlayerPosition;
import KickIt.server.domain.teams.entity.Player;
import KickIt.server.domain.teams.entity.Squad;
import KickIt.server.global.util.WebDriverUtil;
import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.util.ObjectUtils;

import java.time.Duration;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

// 현재 시즌의 팀별 선수 명단을 크롤링하기 위한 SquadCrawler
public class SquadCrawler {
    Map<EplTeams, Squad> getTeamSquads(){
        WebDriver driver = WebDriverUtil.getChromeDriver();
        // 다음 스포츠의 프리미어리그 팀 페이지
        String pageUrl = "https://sports.daum.net/team/epl";
        // 현 시즌의 각 팀 선수 명단을 저장하고 있는 Map 객체 seasonSquads
        Map<EplTeams, Squad> seasonSquads = new HashMap<>();
        // 다음 스포츠 팀 페이지 주소를 저장할 리스트
        List<String> teamPageUrls = new ArrayList<>();
        //  English Premier league 공식 사이트 팀 페이지 주소를 저장할 리스트
        List<String> officialPageUrls = new ArrayList<>();
        //  팀별 선수 공식 이미지 주소를 저장할 hashmap
        Map<EplTeams, Map<Integer, String>> imageUrls = new HashMap<>();

        if (!ObjectUtils.isEmpty(driver)) {
            try{
                driver.get(pageUrl);
                driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
                // 다음 스포츠의 팀 페이지에서 각 팀의 상세 페이지 url을 가져 온다.
                List<WebElement> teamPages = driver.findElements(By.cssSelector("div.cont_item > a.link_cont"));
                for (int i = 0; i < teamPages.size(); i++){
                    String href = teamPages.get(i).getAttribute("href");
                    // 가져온 상세 페이지 url에서 필요한 부분만 잘라내고, 뒤에 squad를 붙여 팀별 선수 명단 페이지 url을 만든다.
                    // 이후 해당 문자열을 teamPageUrls에 저장한다.
                    teamPageUrls.add(href.substring(0, href.length()-4) + "squad");
                }
                driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));

                WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(60));
                try{
                    driver.get("https://www.premierleague.com");

                    wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("Button#onetrust-accept-btn-handler")));
                    driver.findElement(By.cssSelector("Button#onetrust-accept-btn-handler")).click();
                    driver.manage().addCookie(new Cookie("eplOfficial", "accept"));
                }
                catch (Exception e){
                    Logger.getGlobal().log(Level.WARNING, e.toString());
                }
                try{
                    driver.get("https://www.premierleague.com");
                    driver.manage().getCookieNamed("eplOfficial");

                    wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("td.team")));
                    driver.findElements(By.cssSelector("td.team > a")).forEach(s -> officialPageUrls.add(s.getAttribute("href").replace("overview", "squad")));
                    /* 가져온 EPL 공식 사이트 팀별 url 출력하는 코드 추후 삭제 예정 */
                    officialPageUrls.forEach(s -> Logger.getGlobal().log(Level.INFO, s));
                }
                catch(Exception e){
                    Logger.getGlobal().log(Level.WARNING, e.toString());
                }

                for (int i = 0; i < teamPages.size(); i++){

                }

                /*
                // 위에서 가져온 각 팀별 상세 페이지 url을 크롤링 완료 후 squad 객체를 반환하는 getSquad 메소드에 매개변수로 전달한다.
                // 이후 결과로 전달 받은 sqaud 객체를 Map<EplTeam, Squad>로 만들어 크롤러가 결과로 반환할 seasonSquads에 추가한다.
                for (int i = 0; i < 20; i++){
                    // page url 제대로 크롤링했는지 확인하기 위한 코드. 추후 삭제 예정.
                    System.out.println(teamPageUrls.get(i));
                    Squad squad = getSquad(driver, teamPageUrls.get(i));
                    seasonSquads.put(squad.getTeam(), squad);
                }
                */

                Thread.sleep(10);
                driver.quit();
            }
            // 예외 처리
            catch (Exception e){
                Logger.getGlobal().log(Level.INFO, e.toString());
                // 문제 있는 경우 null 반환
                seasonSquads = null;
                driver.quit();
            }
        }
        driver.quit();
        return seasonSquads;
    }

    // 각 팀의 선수 상세 페이지를 크롤링해 팀 선수 명단인 Squad 객체를 반환하는 getSquad()
    Squad getSquad(WebDriver driver, String url){
        driver.get(url);
        // 포지션별로 나누어진 선수 명단 정보가 있는 WebElements 찾아옴 (총 4 개- 0: 공격수 1: 미드필더 2: 수비수 3: 골키퍼)
        List<WebElement> squadElements = driver.findElements(By.className("list_member"));
        // 팀 이름 문자열 가져온 후 팀의 한국어 풀네임으로 EplTeams를 찾아 반환 받는다.
        EplTeams team = EplTeams.valueOfKrFullName(driver.findElement(By.cssSelector("div.basic_feature > div.cont_thumb > h3.tit_thumb ")).getText());
        /* team data 제대로 크롤링했는지 확인하기 위한 코드. 추후 삭제 예정. */
        Logger.getGlobal().log(Level.INFO, String.format("%s", team));
        // 포지션별 선수 명단 element마다 getPosPlayers 함수를 호출해, Player list를 반환 받는다.
        // 이후 team과 포지션별 선수 명단 데이터로 Squad 객체 teamSquad를 build 후 반환한다.
        Squad teamSquad = Squad.builder()
                            .team(team)
                            .FWplayers(getPosPlayers(squadElements.get(0).findElements(By.tagName("li")), team, PlayerPosition.FW))
                            .MFplayers(getPosPlayers(squadElements.get(1).findElements(By.tagName("li")), team, PlayerPosition.MF))
                            .DFplayers(getPosPlayers(squadElements.get(2).findElements(By.tagName("li")), team, PlayerPosition.DF))
                            .GKplayers(getPosPlayers(squadElements.get(3).findElements(By.tagName("li")), team, PlayerPosition.GK))
                            .build();
        return teamSquad;
    }

    // 팀 선수 명단 내의 포지션 별 선수 리스트를 크롤링해 반환하는 함수 getPosPlayers()
    ArrayList<Player> getPosPlayers(List<WebElement> playerInfos, EplTeams team, PlayerPosition pos){
        ArrayList<Player> posPlayers = new ArrayList<>();
        for(int i = 0; i < playerInfos.size(); i++){
            // 선수 이름 크롤링
            String name = playerInfos.get(i).findElement(By.cssSelector("strong.tit_thumb")).getText();
            // 선수 번호(No. 00 형태 문자열) 크롤링 후 0~9 사이 숫자가 아니면 모두 ""로 변환
            String num = playerInfos.get(i).findElement(By.cssSelector("span.txt_thumb")).getText().replaceAll("[^0-9]", "");
            // 생성한 고유 id, 매개변수로 전달 받은 team과 position, 크롤링 해온 number과 name 데이터로 새로운 Player 객체를 build하고,
            // 포지션 별 선수 리스트 posPlayers에 추가한다.
            Player player = Player.builder()
                    .id(UUID.randomUUID())
                    .team(team)
                    .number(num.equals("")? null : Integer.parseInt(num))
                    .name(name)
                    .position(pos)
                    .build();
            /* 각 선수 정보 제대로 크롤링했는지 확인하기 위한 코드. 추후 삭제 예정. */
            //Logger.getGlobal().log(Level.INFO, String.format("%s %s %s %s", player.getId(), player.getNumber(), player.getName(), player.getPosition().toString()));
            posPlayers.add(player);
        }
        return posPlayers;
    }
}
