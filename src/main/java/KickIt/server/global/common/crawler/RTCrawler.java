package KickIt.server.global.common.crawler;

import KickIt.server.domain.realtime.dto.RealTime;
import KickIt.server.domain.realtime.dto.RealTimeRepository;
import KickIt.server.global.util.WebDriverUtil;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class RTCrawler {
    // 실시간 타임라인을 가져와 출력하는 crawlingRT 함수
    @Autowired
    private RTCrawlingDataParser parser;

    @Autowired
    private RealTimeRepository realTimeRepository;

    public void startCrawling() {
        WebDriver driver = WebDriverUtil.getChromeDriver();
        driver.get("https://sports.daum.net/match/80074531");

        boolean eventEnd = false;
        boolean firstEnd = false;
        Set<String> previousList = new HashSet<>();

        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofMinutes(30));

            RealTime realTime = new RealTime.Builder()
                    .dateTime(parser.getDateTime())
                    .event("전반전 시작")
                    .build();
            realTimeRepository.save(realTime);

            while (!eventEnd) {
                Thread.sleep(10000);

                WebElement timeLine = wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("group_timeline")));
                List<WebElement> timeElements = timeLine.findElements(By.tagName("li"));

                for (WebElement li : timeElements) {
                    String liText = li.getText();
                    if (!previousList.contains(liText)) {
                        previousList.add(liText);
                        RealTime event = parser.parseEvent(liText, li);
                        realTimeRepository.save(event);

                        if (liText.equals("종료")) {
                            RealTime halfTime = new RealTime.Builder()
                                    .dateTime(parser.getDateTime())
                                    .event("하프타임")
                                    .build();
                            realTimeRepository.save(halfTime);
                            firstEnd = true;
                            break;
                        }

                        if (liText.contains("경기종료")) {
                            RealTime endTime = new RealTime.Builder()
                                    .dateTime(parser.getDateTime())
                                    .event("경기종료")
                                    .build();
                            realTimeRepository.save(endTime);
                            eventEnd = true;
                            break;
                        }
                    }
                }

                if (firstEnd) {
                    handleHalfTime(wait);
                    firstEnd = false;
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            WebDriverUtil.quit(driver);
        }
    }

    private void handleHalfTime(WebDriverWait wait) throws InterruptedException {
        System.out.println("전반전 종료. 15분 후 후반전 시작.(리스트 포함X)");
        Thread.sleep(15 * 60 * 1000);

        RealTime realTime = new RealTime.Builder()
                .dateTime(parser.getDateTime())
                .event("후반전 시작")
                .build();
        realTimeRepository.save(realTime);
    }

}


