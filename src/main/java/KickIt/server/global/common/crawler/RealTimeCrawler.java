package KickIt.server.global.common.crawler;

import KickIt.server.domain.fixture.entity.Fixture;
import KickIt.server.domain.realtime.dto.RealTimeRepository;
import KickIt.server.domain.realtime.entity.RealTime;
import KickIt.server.domain.realtime.service.RealTimeService;
import KickIt.server.domain.teams.EplTeams;
import KickIt.server.domain.teams.entity.TeaminfoRepository;
import KickIt.server.global.util.WebDriverUtil;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static KickIt.server.global.common.crawler.RealTimeDataParser.*;


@Component
public class RealTimeCrawler {
    // 실시간 타임라인을 가져와 DB저장 함수

    private WebDriver driver;
    private boolean start = false;
    private final Set<String> previousList = new HashSet<>();
    private LocalDateTime startTime;
    private String extraTime;
    private Long matchId;
    private String season;
    private EplTeams homeTeamName;
    private EplTeams awayTeamName;
    private String homeTeamlogoUrl;
    private String awayTeamlogoUrl;
    private String homeTeamScore;
    private String awayTeamScore;

    private boolean isHomeEventPresent = false;
    private boolean isAwayEventPresent = false;

    private final RealTimeService realTimeService;
    private final TeaminfoRepository teaminfoRepository;

    List<RealTime> realTimeList = new ArrayList<>();


    @Autowired
    public RealTimeCrawler(RealTimeService realTimeService, TeaminfoRepository teaminfoRepository) {
        this.teaminfoRepository = teaminfoRepository;
        this.realTimeService = realTimeService;
    }

    public void initializeCrawler(Fixture fixture) {
            driver = WebDriverUtil.getChromeDriver();
            driver.get("https://sports.daum.net/" + fixture.getLineupUrl());
            matchId = fixture.getId();
            season = fixture.getSeason();
            homeTeamName = fixture.getHomeTeam();
            awayTeamName = fixture.getAwayTeam();
            homeTeamlogoUrl = teaminfoRepository.findByTeamNameAndSeason(homeTeamName, season);
            awayTeamlogoUrl = teaminfoRepository.findByTeamNameAndSeason(homeTeamName, season);

            System.out.println("homeTeamName = " + homeTeamName);
            System.out.println("awayTeamName = " + awayTeamName);
            System.out.println("homeTeamlogoUrl = " + homeTeamlogoUrl);
            System.out.println("awayTeamlogoUrl = " + awayTeamlogoUrl);
            System.out.println("season = " + season);
    }

    public RealTime crawling() {
        // 기본 설정
        RealTime realTime = RealTime.builder()
                .build();

        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofMinutes(15));

            try{
                // 타임 화면 나타날 때까지 대기
                //wait.until(ExpectedConditions.presenceOfElementLocated(By.className("sr-lmt-clock__time")));
                if(!start){
                    startTime = LocalDateTime.now();
                    start = true;
                    // 추가 시간이 없을 경우를 생각하여 0 으로 초기화
                    extraTime = "0";
                    System.out.println("경기 시작 시간" + dateToString(startTime));
                    return realTime;
                } else {

                }

            } catch(Exception e) {
                // 하프 타임 or 경기 종료
                System.out.println("실행 시작 화면 오류");
            }

            Thread.sleep(10000);

            WebElement timeLine = wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("group_timeline")));
            List<WebElement> timeElements = timeLine.findElements(By.tagName("li"));

            for (WebElement li : timeElements) {
                String eventText = li.getText();
                if (!previousList.contains(eventText)) {
                    previousList.add(eventText);

                    isHomeEventPresent = isChildClassPresent(li,"game_info.game_vs1","txt_g");
                    isAwayEventPresent = isChildClassPresent(li,"game_info.game_vs2", "txt_g");

                    String[] elements = eventText.split("\n");
                    RealTime.RealTimeBuilder realTimeBuilder = RealTime.builder()
                            .matchId(matchId);

                    if (isHomeEventPresent) {
                        realTimeBuilder
                                .teamName(teamNameToString(homeTeamName))
                                .teamUrl(homeTeamlogoUrl);
                    } else if (isAwayEventPresent) {
                        realTimeBuilder
                                .teamName(teamNameToString(awayTeamName))
                                .teamUrl(awayTeamlogoUrl);
                    }

                    if (eventText.contains("골")) {
                        List<WebElement> spans = li.findElements(By.tagName("span"));
                        for (WebElement span : spans) {
                            String ownGoal = span.getText();
                            if (ownGoal.contains("골")) {
                                String ownGoalClass = span.getAttribute("class");
                                if (ownGoalClass.contains("ico_goal_own")) {
                                    realTimeBuilder
                                            .eventCode(whenHappen())
                                            .eventTime(compareTime(startTime, elements[0]))
                                            .eventName("자책골")
                                            .player1(elements[2])
                                            .player2(elements.length > 3 ? rmBracket(elements[3]) : "");
                                    realTime = realTimeBuilder.build();
                                    System.out.println("realTime = " + realTime);
                                    realTimeService.saveEvent(realTime);
                                    return realTime;
                                } else {
                                    realTimeBuilder
                                            .eventCode(whenHappen())
                                            .eventTime(compareTime(startTime, elements[0]))
                                            .eventName(elements[1] + "!")
                                            .player1(elements[2])
                                            .player2(elements.length > 3 ? rmBracket(elements[3]) : "");
                                    realTime = realTimeBuilder.build();
                                    System.out.println("realTime = " + realTime);
                                    realTimeService.saveEvent(realTime);
                                    return realTime;
                                }
                            }
                        }
                    } else if (eventText.contains("교체")) {
                        realTimeBuilder
                                .eventCode(whenHappen())
                                .eventTime(compareTime(startTime, elements[0]))
                                .eventName(elements[1])
                                .player1(elements[2])
                                .player2(elements[4]);
                        realTime = realTimeBuilder.build();
                        System.out.println("realTime = " + realTime);
                        realTimeService.saveEvent(realTime);
                        return realTime;
                    } else if (eventText.contains("경고") || eventText.contains("퇴장")) {
                        realTimeBuilder
                                .eventCode(whenHappen())
                                .eventTime(compareTime(startTime, elements[0]))
                                .eventName(elements[1])
                                .player1(elements[2]);
                        realTime = realTimeBuilder.build();
                        System.out.println("realTime = " + realTime);
                        realTimeService.saveEvent(realTime);
                        return realTime;
                    } else if (eventText.contains("VAR")) {
                        realTimeBuilder
                                .eventCode(whenHappen())
                                .eventTime(compareTime(startTime, elements[0]))
                                .eventName(elements[2])
                                .player1(rmBracket(elements[3]));
                        realTime = realTimeBuilder.build();
                        System.out.println("realTime = " + realTime);
                        realTimeService.saveEvent(realTime);
                        return realTime;
                    } else if (eventText.contains("추가시간")) {
                        isExtra();
                        realTimeBuilder
                                .eventCode(4)
                                .eventTime(extraTime(startTime))
                                .eventName(getAddEvent(elements[0]))
                                .player1(getAddTime(elements[0]));
                        extraTime = getAddTime(elements[0]);
                        realTime = realTimeBuilder.build();
                        System.out.println("realTime = " + realTime);
                        realTimeService.saveEvent(realTime);
                        return realTime;
                    } else if (eventText.equals("종료")) {
                        getHalfScore();
                        realTimeBuilder
                                .eventCode(2)
                                .eventName("하프타임")
                                .eventTime(halfTime(startTime, extraTime))
                                .player1(homeTeamScore)
                                .player2(awayTeamScore);
                        start = false;
                        realTime = realTimeBuilder.build();
                        System.out.println("realTime = " + realTime);
                        realTimeService.saveEvent(realTime);
                        return realTime;
                    } else if (eventText.contains("경기종료")) {
                        getHalfScore();
                        realTimeBuilder
                                .eventCode(6)
                                .eventTime(halfTime(startTime, extraTime))
                                .eventName(elements[0])
                                .player1(homeTeamScore)
                                .player2(awayTeamScore);
                        realTime = realTimeBuilder.build();
                        System.out.println("realTime = " + realTime);
                        realTimeService.saveEvent(realTime);
                        return realTime;
                    }

                }

            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        if (realTime.getEventName() != null) {
            realTimeList.add(realTime);

        }
        isHomeEventPresent = false;
        isAwayEventPresent = false;

        return realTime;
    }

    // 홈팀, 어웨이팀 구분
    public boolean isChildClassPresent(WebElement parentElement, String parentClass, String childClass) {
        List<WebElement> childElements = parentElement.findElements(By.cssSelector("." + parentClass + " ." + childClass));
        return !childElements.isEmpty();
    }

    public void getHalfScore() {
        List<WebElement> scoreList = driver.findElements(By.cssSelector(".num_team"));
        WebElement score1 = scoreList.get(0);
        WebElement score2 = scoreList.get(0);

        homeTeamScore = score1.getText();
        awayTeamScore = score2.getText();
    }
    // 크롤링 멈추기
    public void quit() {
        WebDriverUtil.quit(driver);
    }

    /*
    디자인 변경으로 처리 안하게 됨
    else if (eventText.contains("후반전")) {
                        isHalf();
                        realTimeBuilder
                                .compareTime(dateToString(startTime))
                                .eventCode(whenHappen())
                                .eventName(elements[0]);
                        realTime = realTimeBuilder.build();
                        System.out.println("realTime = " + realTime);
                        return realTime;
                        //realTimeService.saveEvent(realTime);

     else if (eventText.contains("0′")) {
                        realTimeBuilder
                                .eventCode(0)
                                .compareTime(compareTime(startTime, "0"))
                                .eventTime("0")
                                .eventName("경기시작");
                        realTime = realTimeBuilder.build();
                        System.out.println("realTime = " + realTime);
                        return realTime;
                        //realTimeService.saveEvent(realTime);
                    }
     */
}
