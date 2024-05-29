package KickIt.server.global.common.crawler;

import KickIt.server.domain.teams.EplTeams;
import KickIt.server.domain.teams.PlayerPosition;
import KickIt.server.domain.teams.entity.Player;
import KickIt.server.domain.teams.entity.Squad;
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
                Thread.sleep(50);
                // 다음 스포츠의 팀 페이지에서 각 팀의 상세 페이지 url을 가져 온다.
                List<WebElement> teamPages = driver.findElements(By.cssSelector("div.cont_item > a.link_cont"));
                for (int i = 0; i < teamPages.size(); i++){
                    String href = teamPages.get(i).getAttribute("href");
                    // 가져온 상세 페이지 url에서 필요한 부분만 잘라내고, 뒤에 squad를 붙여 팀별 선수 명단 페이지 url을 만든다.
                    // 이후 해당 문자열을 teamPageUrls에 저장한다.
                    teamPageUrls.add(href.substring(0, href.length()-4) + "squad");
                }
                Thread.sleep(50);

                // EPL 공식 사이트의 랭킹 table에서 각 팀 페이지 주소를 가져와 teamPageUrls에 저장한다.
                try{
                    // flex layout element에서 stale error 발생 방지하기 위한 브라우저 크기 조절
                    driver.manage().window().setSize(new org.openqa.selenium.Dimension(1400, 800));
                    // EPL 공식 사이트 크롤링
                    driver.get("https://www.premierleague.com/home");

                    // 첫 방문 시 쿠키 허용을 위해 accept button 클릭 가능할 때까지 명시적으로 대기 -> 클릭
                    new WebDriverWait(driver, Duration.ofSeconds(30)).until(ExpectedConditions.elementToBeClickable(By.cssSelector("Button#onetrust-accept-btn-handler")));
                    driver.findElement(By.cssSelector("Button#onetrust-accept-btn-handler")).click();

                    // 본 페이지의 랭킹 테이블의 td.teams 모두 보일 때까지 100 밀리초 대기 + 명시적 대기
                    Thread.sleep(100);
                    new WebDriverWait(driver, Duration.ofSeconds(30)).until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.cssSelector("td.team > a")));

                    // 각 팀 페이지 url 가져와 저장
                    driver.findElements(By.cssSelector("td.team > a")).forEach(s -> officialPageUrls.add(s.getAttribute("href").replace("overview", "squad")));
                    officialPageUrls.forEach(s -> getPlayerImgs(imageUrls, driver, s));
                }
                // 예외 처리
                catch (Exception e){
                    Logger.getGlobal().log(Level.WARNING, e.toString());
                }

                // 위에서 가져온 각 팀별 상세 페이지 url을 크롤링 완료 후 squad 객체를 반환하는 getSquad 메소드에 매개변수로 전달한다.
                // 이후 결과로 전달 받은 sqaud 객체를 Map<EplTeam, Squad>로 만들어 크롤러가 결과로 반환할 seasonSquads에 추가한다.
                for (int i = 0; i < 20; i++){
                    // page url 제대로 크롤링했는지 확인하기 위한 코드. 추후 삭제 예정.
                    System.out.println(teamPageUrls.get(i));
                    Squad squad = getSquad(driver, teamPageUrls.get(i), imageUrls);
                    seasonSquads.put(squad.getTeam(), squad);
                }

                Thread.sleep(50);
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

    // 각 팀 페이지에 방문해 해당 팀 EplTeams를 key 값으로, 선수 이미지 목록을 담은 Map<선수 번호, 이미지 주소>를 value 값으로 map에 넣어 줌
    void getPlayerImgs(Map<EplTeams, Map<Integer, String>> map, WebDriver driver, String pageUrl){
        driver.get(pageUrl);
        // 크롤링해 온 영어 풀네임으로 EplTeams 반환 받음
        EplTeams team = EplTeams.valueOfEngName(driver.findElement(By.cssSelector("h2.club-header__team-name")).getText());

        // 팀의 선수들의 프로필 이미지 주소를 이후 다음 스포츠 페이지 크롤링 기반으로 Player 객체 build 할때 쉽게 찾아올 수 있도록
        // 선수 번호를 key 값으로 선수 프로필 이미지 주소 value를 찾아오는 map 만듦
        Map<Integer, String> playerImgs = new HashMap<>();
        List<String> numList = new ArrayList<>();
        List<String> urlList = new ArrayList<>();
        driver.findElements(By.cssSelector("div.stats-card__squad-number.u-hide-mob-l")).forEach(e -> numList.add(e.getText()));
        driver.findElements(By.cssSelector("img.statCardImg.statCardPlayer")).forEach(e -> urlList.add(e.getAttribute("src")));


        for(int i = 0; i < numList.size(); i++){
            // numList가 공백인 경우(선수 번호 없는 경우) Integer로 parsing 할 경우 오류 발생하므로 continue
            // (번호 없어 map으로 찾을 수 없으므로 이미지 주소 저장할 필요 없음)
            if(numList.get(i).equals("")){ continue; }
            // map 'playerImgs'에 (선수 이름, 크롤링한 프로필 이미지 주소) put
            playerImgs.put(Integer.parseInt(numList.get(i)), urlList.get(i));
        }
        // map 'map'에 (팀, 선수 이미지 주소들 담은 map'playerImgs') put
        map.put(team, playerImgs);
    }

    // 각 팀의 선수 상세 페이지를 크롤링해 팀 선수 명단인 Squad 객체를 반환하는 getSquad()
    Squad getSquad(WebDriver driver, String url, Map<EplTeams, Map<Integer, String>> imageUrl){
        driver.get(url);
        // 포지션별로 나누어진 선수 명단 정보가 있는 WebElements 찾아옴 (총 4 개- 0: 공격수 1: 미드필더 2: 수비수 3: 골키퍼)
        List<WebElement> squadElements = driver.findElements(By.className("list_member"));
        // 팀 이름 문자열 가져온 후 팀의 한국어 풀네임으로 EplTeams를 찾아 반환 받는다.
        EplTeams team = EplTeams.valueOfKrFullName(driver.findElement(By.cssSelector("div.basic_feature > div.cont_thumb > h3.tit_thumb ")).getText());
        /* team data 제대로 크롤링했는지 확인하기 위한 코드. 추후 삭제 예정. */
        Logger.getGlobal().log(Level.INFO, String.format("%s", team));
        // 포지션별 선수 명단 element마다 getPosPlayers 함수를 호출해, Player list를 반환 받는다.
        // 이후 team과 포지션별 선수 명단 데이터로 Squad 객체 teamSquad를 build 후 반환한다.
        Map<Integer, String> images = imageUrl.get(team);
        Squad teamSquad = Squad.builder()
                            .team(team)
                            .FWplayers(getPosPlayers(squadElements.get(0).findElements(By.tagName("li")), team, PlayerPosition.FW, images))
                            .MFplayers(getPosPlayers(squadElements.get(1).findElements(By.tagName("li")), team, PlayerPosition.MF, images))
                            .DFplayers(getPosPlayers(squadElements.get(2).findElements(By.tagName("li")), team, PlayerPosition.DF, images))
                            .GKplayers(getPosPlayers(squadElements.get(3).findElements(By.tagName("li")), team, PlayerPosition.GK, images))
                            .build();
        return teamSquad;
    }

    // 팀 선수 명단 내의 포지션 별 선수 리스트를 크롤링해 반환하는 함수 getPosPlayers()
    ArrayList<Player> getPosPlayers(List<WebElement> playerInfos, EplTeams team, PlayerPosition pos, Map<Integer, String> images){
        ArrayList<Player> posPlayers = new ArrayList<>();
        for(int i = 0; i < playerInfos.size(); i++){
            // 선수 이름 크롤링
            String name = playerInfos.get(i).findElement(By.cssSelector("strong.tit_thumb")).getText();
            // 선수 번호(No. 00 형태 문자열) 크롤링 후 0~9 사이 숫자가 아니면 모두 ""로 변환
            String numText = playerInfos.get(i).findElement(By.cssSelector("span.txt_thumb")).getText().replaceAll("[^0-9]", "");
            // 생성한 고유 id, 매개변수로 전달 받은 team과 position, 크롤링 해온 number과 name 데이터로 새로운 Player 객체를 build하고,
            // 포지션 별 선수 리스트 posPlayers에 추가한다.
            Integer num = numText.equals("")? null : Integer.parseInt(numText);
            // 공식 팀 페이지에서 이미지 주소들을 가져오지 못해 images가 null 인 경우 선수 개인 이미지 주소 null 처리,
            // 그렇지 않은 경우 images에서 num을 key 값으로 이미지 주소 찾아 와 저장.
            String img = images == null? null: images.get(num);
            Player player = Player.builder()
                    .id(UUID.randomUUID())
                    .team(team)
                    .number(num)
                    .name(name)
                    .position(pos)
                    // img가 null인 경우 epl 공식 사이트의 선수 이미지 없을 때의 sample image로, 가지고 있는 img 있는 경우 그 주소로 build
                    .profileImg(img == null ? "https://resources.premierleague.com/premierleague/photos/players/110x140/Photo-Missing.png" : img)
                    .build();
            /* 각 선수 정보 제대로 크롤링했는지 확인하기 위한 코드. 추후 삭제 예정. */
            Logger.getGlobal().log(Level.INFO, String.format("%s %s %s %s %s", player.getId(), player.getNumber(), player.getName(), player.getPosition().toString(), player.getProfileImg()));
            posPlayers.add(player);
        }
        return posPlayers;
    }
}
