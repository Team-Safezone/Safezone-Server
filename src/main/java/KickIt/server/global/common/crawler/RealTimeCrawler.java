package KickIt.server.global.common.crawler;

import KickIt.server.domain.fixture.entity.Fixture;
import KickIt.server.domain.realtime.dto.RealTimeRepository;
import KickIt.server.domain.realtime.entity.RealTime;
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
    private EplTeams homeTeamName;
    private EplTeams awayTeamName;
    private String homeTeamlogoUrl;
    private String awayTeamlogoUrl;

    private boolean isHomeEventPresent = false;
    private boolean isAwayEventPresent = false;

    private final RealTimeRepository realTimeRepository;
    private final TeaminfoRepository teaminfoRepository;

    List<RealTime> realTimeList = new ArrayList<>();

    @Autowired
    public RealTimeCrawler(RealTimeRepository realTimeRepository, TeaminfoRepository teaminfoRepository) {
        this.teaminfoRepository = teaminfoRepository;
        this.realTimeRepository = realTimeRepository;
    }

    public void initializeCrawler(Fixture fixture) {
        this.driver = WebDriverUtil.getChromeDriver();
        driver.get("https://sports.daum.net/" + fixture.getLineupUrl());
    }



    public RealTime crawling(Fixture fixture) {

        // 크롤링 로직 구현
        initializeCrawler(fixture);

        // 기본 설정
        RealTime realTime = RealTime.builder()
                .build();

        homeTeamName = fixture.getHomeTeam();
        awayTeamName = fixture.getAwayTeam();

        System.out.println(homeTeamName);
        System.out.println(awayTeamName);

        System.out.println(fixture.getSeason());

        homeTeamlogoUrl = teaminfoRepository.findByTeamNameAndSeason(homeTeamName, fixture.getSeason());
        awayTeamlogoUrl = teaminfoRepository.findByTeamNameAndSeason(homeTeamName, fixture.getSeason());

        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(3));

            try{
                // 타임 화면 나타날 때까지 대기
                wait.until(ExpectedConditions.presenceOfElementLocated(By.className("sr-lmt-clock__time")));
            } catch(Exception e) {
                // 하프 타임 or 경기 종료
            }

            if(!start){
                startTime = LocalDateTime.now();
                start = true;
                System.out.println(dateToString(startTime));
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
                            .matchId(fixture.getId());

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
                                            .compareTime(compareTime(startTime, elements[0]))
                                            .eventName("자책골")
                                            .eventTime(elements[0])
                                            .player1(elements[2])
                                            .player2(elements.length > 3 ? rmBracket(elements[3]) : "");
                                } else {
                                    realTimeBuilder
                                            .eventCode(whenHappen())
                                            .compareTime(compareTime(startTime, elements[0]))
                                            .eventName(elements[1] + "!")
                                            .eventTime(elements[0])
                                            .player1(elements[2])
                                            .player2(elements.length > 3 ? rmBracket(elements[3]) : "");
                                }
                                break;
                            }
                        }
                    } else if (eventText.contains("교체")) {
                        realTimeBuilder
                                .eventCode(whenHappen())
                                .compareTime(compareTime(startTime, elements[0]))
                                .eventName(elements[1])
                                .eventTime(elements[0])
                                .player1(elements[2])
                                .player2(elements[4]);
                    } else if (eventText.contains("경고") || eventText.contains("퇴장")) {
                        realTimeBuilder
                                .eventCode(whenHappen())
                                .compareTime(compareTime(startTime, elements[0]))
                                .eventName(elements[1])
                                .eventTime(elements[0])
                                .player1(elements[2]);
                    } else if (eventText.contains("VAR")) {
                        realTimeBuilder
                                .eventCode(whenHappen())
                                .compareTime(compareTime(startTime, elements[0]))
                                .eventName(elements[1])
                                .eventTime(elements[0])
                                .player1(elements[2])
                                .player2(rmBracket(elements[3]));
                    } else if (eventText.contains("추가시간")) {
                        isExtra();
                        realTimeBuilder
                                .eventCode(4)
                                .compareTime(extraTime(startTime))
                                .eventName(getAddEvent(elements[0]))
                                .player1(getAddTime(elements[0]));
                        extraTime = getAddTime(elements[0]);
                    } else if (eventText.contains("후반전")) {
                        isHalf();
                        realTimeBuilder
                                .compareTime(dateToString(startTime))
                                .eventCode(whenHappen())
                                .eventName(elements[0]);
                    } else if (eventText.equals("종료")) {
                        realTimeBuilder
                                .eventCode(2)
                                .eventName("하프타임")
                                .compareTime(halfTime(startTime,extraTime));
                        start = false;
                        realTime = realTimeBuilder.build();
                    } else if (eventText.contains("경기종료")) {
                        realTimeBuilder
                                .eventCode(6)
                                .eventName(elements[0]);
                    } else if (eventText.contains("0′")) {
                        realTimeBuilder
                                .eventCode(0)
                                .compareTime(compareTime(startTime, "0"))
                                .eventTime("0")
                                .eventName("경기시작");
                    }

                    // 최종적으로 생성된 RealTime 객체를 반환
                    realTime = realTimeBuilder.build();
                    System.out.println(realTime.toString());

                }

            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        isHomeEventPresent = false;
        isAwayEventPresent = false;

        realTimeList.add(realTime);
        //realTimeRepository.save(realTime);
        return realTime;
    }

    // 홈팀, 어웨이팀 구분
    public boolean isChildClassPresent(WebElement parentElement, String parentClass, String childClass) {
        List<WebElement> childElements = parentElement.findElements(By.cssSelector("." + parentClass + " ." + childClass));
        return !childElements.isEmpty();
    }

    // 크롤링 멈추기
    public void quit() {
        WebDriverUtil.quit(driver);
    }

}
