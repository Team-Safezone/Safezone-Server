package KickIt.server.global.common.crawler;

import KickIt.server.domain.realtime.entity.RealTime;
import KickIt.server.global.util.WebDriverUtil;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static KickIt.server.global.common.crawler.RealTimeDataParser.*;


@Component
public class RealTimeCrawler {
    // 실시간 타임라인을 가져와 출력하는 crawlingRT 함수

    private WebDriver driver;
    private boolean start = false;

    public RealTimeCrawler(String getLineupUrl) {
        this.driver = WebDriverUtil.getChromeDriver();
        driver.get("https://sports.daum.net/match/" + getLineupUrl);

    }

    public RealTime startCrawling() {

        Set<String> previousList = new HashSet<>();

        // 기본 설정
        RealTime realTime = RealTime.builder()
                .build();


        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofMinutes(15));

            // 크롤링 시간과 전반전 시작 시간 비교용
            //System.out.println("전반전 시작 전 대기 시작(리스트 포함X): " + getDateTime());

            // 중계 화면 나타날 때까지 대기
            //wait.until(ExpectedConditions.presenceOfElementLocated(By.className("sr-lmt-clock__time")));

            if(!start){
                RealTime realStart = RealTime.builder()
                        .matchId(1234L)
                        .eventCode(0)
                        .eventTime(getDateTime())
                        .eventName(startMatch())
                        .build();
                start = true;
                System.out.println(realStart.toString());
                return realStart;
            }


            Thread.sleep(10000);

            WebElement timeLine = wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("group_timeline")));
            List<WebElement> timeElements = timeLine.findElements(By.tagName("li"));

            for (WebElement li : timeElements) {
                String eventText = li.getText();
                if (!previousList.contains(eventText)) {
                    previousList.add(eventText);

                    String[] elements = eventText.split("\n");
                    RealTime.RealTimeBuilder realTimeBuilder = RealTime.builder()
                            .matchId(1234L)
                            .eventTime(elements[0]);

                    if (eventText.contains("골")) {
                        List<WebElement> spans = li.findElements(By.tagName("span"));
                        for (WebElement span : spans) {
                            String ownGoal = span.getText();
                            if (ownGoal.contains("골")) {
                                String ownGoalClass = span.getAttribute("class");
                                if (ownGoalClass.contains("ico_goal_own")) {
                                    realTimeBuilder.eventName("자책골")
                                            .player1(elements[2])
                                            .player2(elements.length > 3 ? rmBracket(elements[3]) : "");
                                } else {
                                    realTimeBuilder.eventName(elements[1] + "!")
                                            .player1(elements[2])
                                            .player2(elements.length > 3 ? rmBracket(elements[3]) : "");
                                }
                                break;
                            }
                        }
                    } else if (eventText.contains("교체")) {
                        realTimeBuilder.eventName(elements[1])
                                .player1(elements[2])
                                .player2(elements[4]);
                    } else if (eventText.contains("경고") || eventText.contains("퇴장")) {
                        realTimeBuilder.eventName(elements[1])
                                .player1(elements[2]);
                    } else if (eventText.contains("VAR")) {
                        realTimeBuilder.eventName(elements[1])
                                .player1(elements[2])
                                .player2(rmBracket(elements[3]));
                    } else if (eventText.contains("추가시간")) {
                        realTimeBuilder
                                .eventCode(4)
                                .eventName(getAddEvent(elements[0]))
                                .player1(getAddTime(elements[0]));
                    } else if (eventText.contains("후반전")) {
                        realTimeBuilder
                                .eventCode(0)
                                .eventName(elements[0]);
                    } else if (eventText.equals("종료")) {
                        realTimeBuilder
                                .eventCode(2)
                                .eventName("하프타임");
                    } else if (eventText.contains("경기종료")) {
                        realTimeBuilder
                                .eventCode(6)
                                .eventName(elements[0]);
                    }

                    // 최종적으로 생성된 RealTime 객체를 반환
                    realTime = realTimeBuilder.build();
                    System.out.println(realTime.toString());
                }

            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return realTime;
    }

    public void quit() {
        WebDriverUtil.quit(driver);
    }
}



