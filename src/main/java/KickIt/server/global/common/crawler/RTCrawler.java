package KickIt.server.global.common.crawler;

import KickIt.server.domain.fixture.entity.Fixture;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class RTCrawler {

    // 실시간 타임라인을 가져와 출력하는 crawlingRT 함수
    public static void main(String[] args) {
        //웹 페이지 이동
        WebDriver driver = new ChromeDriver();
        // 임시 페이지 지정 이동
        driver.get("https://sports.daum.net/match/80074837");

        /*

        // 구현 수정 예정
        // 페이지 이동
        driver.get("https://sports.daum.net/" + fixture.getLineupUrl());

        지금은 실시간 랜덤 경기 정보 받아옴
        나중에 선호하는 팀에 대한 경기 정보 받아보는 코드 추가 필요

        */


        //이전에 저장된 정보
        Set<String> previousList = new HashSet<>();

        // 변경된 요소를 저장
        Set<String> newList = new HashSet<>();

        // 이벤트 종료 여부를 저장하는 변수 eventEnd
        boolean eventEnd = false;


        try {
            // 이벤트 업데이트 최소 시간 (30분)
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofMinutes(30));

            // "경기 시작 후 업데이트 됩니다." 화면이 없어질 때까지 대기
            wait.until(ExpectedConditions.not(ExpectedConditions.textToBe(By.className("desc_empty"), "경기 시작 후 업데이트됩니다.")));

            // 이벤트가 종료되지 않은 동안 반복
            while (!eventEnd) {
                // 10초마다 한번씩 실행
                Thread.sleep(10000);

                //10초에 한번씩 크롤링 체크(확인)
                LocalTime now = LocalTime.now();
                System.out.println(now);

                // list 찾기(타임 라인 이벤트가 list 형식으로 되어있음)
                WebElement timeLine = wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("group_timeline")));
                List<WebElement> timeElements = timeLine.findElements(By.tagName("li"));

                // 변경된 list 요소 탐색
                for (WebElement li : timeElements) {
                    String liText = li.getText();
                    if (!previousList.contains(liText)) {
                        newList.add(liText);
                        // 변경된 내용 출력
                        System.out.println(liText);
                        // 요소 저장
                        previousList.add(li.getText());
                    }
                }

                // 이벤트 종료 확인
                for (WebElement li : timeElements) {
                    String liText = li.getText();
                    if (liText.contains("경기종료")) {
                        // 경기 끝
                        eventEnd = true;
                    }
                }
            }
        } catch(InterruptedException e) {
            e.printStackTrace();
        } finally {
            // WebDriver 종료
            driver.quit();
        }

    }

}


