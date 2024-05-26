package KickIt.server.global.common.crawler;

import KickIt.server.domain.realtime.RealTime;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;



public class RTCrawler {

    // 실시간 타임라인을 가져와 출력하는 crawlingRT 함수
    public static void main(String[] args) {
        //웹 페이지 이동
        WebDriver driver = new ChromeDriver();
        // 임시 페이지 지정 이동
        driver.get("https://sports.daum.net/match/80075525");

        /*

        // 구현 수정 예정
        // 페이지 이동
        driver.get("https://sports.daum.net/" + fixture.getLineupUrl());

        지금은 실시간 랜덤 경기 정보 받아옴
        나중에 선호하는 팀에 대한 경기 정보 받아보는 코드 추가 필요

        */


        // 이벤트 종료 여부를 저장하는 eventEnd
        boolean eventEnd = false;
        boolean firstEnd = false;

        // 이전에 저장된 정보
        Set<String> previousList = new HashSet<>();

        // 저장할 리스트
        List<RealTime> timeLineList = new ArrayList<>();

        try {
            // 이벤트 업데이트 최소 시간 (30분)
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofMinutes(30));

            // 크롤링 시간과 전반전 시작 시간 비교용
            System.out.println("전반전 시작 전 대기 시작: " + getDateTime());

            // 중계 화면 나타날 때까지 대기
            //wait.until(ExpectedConditions.presenceOfElementLocated(By.className("sr-lmt-clock__time")));

            // 비교용
            System.out.println("전반전 시작 시간: " + getDateTime());

            // 전반전 시작 시간
            RealTime realTime = RealTime.builder()
                    .dateTime(getDateTime())
                    .build();

            timeLineList.add(realTime);

            // 이벤트가 종료되지 않은 동안 반복
            while (!eventEnd) {
                // 10초마다 한번씩 실행
                Thread.sleep(10000);

                // list 찾기(타임 라인 이벤트가 list 형식으로 되어있음)
                WebElement timeLine = wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("group_timeline")));
                List<WebElement> timeElements = timeLine.findElements(By.tagName("li"));


                // 변경된 list 요소 탐색
                for (WebElement li : timeElements) {
                    String liText = li.getText();
                    if (!previousList.contains(liText)) {
                        // 찾아온 정보를 인덱스화 하여 개별로 저장
                        // [0]: 시간, [1]: 이벤트
                        String[] elements = liText.split("\n");

                        // 요소 저장
                        previousList.add(li.getText());

                        // 첫 시작
                        if (liText.contains("0′")) {
                            realTime = RealTime.builder()
                                    .dateTime(getDateTime())
                                    .timeLine(elements[0])
                                    .build();
                            System.out.println(realTime.getDateTime() + " " + realTime.getTimeLine());
                        }

                        // timeline 리스트에 추가
                        if (liText.contains("골")) {
                            // 어시스트 있을 때
                            if (elements.length > 3) {
                                realTime = RealTime.builder()
                                        .dateTime(getDateTime())
                                        .timeLine(elements[0])
                                        .event(elements[1])
                                        .goalPlayer(elements[2])
                                        .assiPlayer(elements[3])
                                        .build();
                                System.out.println(realTime.getDateTime() + " " + realTime.getTimeLine() + " " + realTime.getEvent() + " "
                                        + realTime.getGoalPlayer() + " " + realTime.getAssiPlayer());
                            }
                            // 어시스트 없을 때
                            else {
                                realTime = RealTime.builder()
                                        .dateTime(getDateTime())
                                        .timeLine(elements[0])
                                        .event(elements[1])
                                        .goalPlayer(elements[2])
                                        .build();
                                System.out.println(realTime.getDateTime() + " " + realTime.getTimeLine() + " " + realTime.getEvent() + " "
                                        + realTime.getGoalPlayer());
                            }

                        }

                        if (liText.contains("교체")) {
                            realTime = RealTime.builder()
                                    .dateTime(getDateTime())
                                    .timeLine(elements[0])
                                    .event(elements[1])
                                    .inPlayer(elements[2])
                                    .outPlayer(elements[4])
                                    .build();

                            System.out.println(realTime.getDateTime() + " " + realTime.getTimeLine() + " " + realTime.getEvent() + " "
                                    + realTime.getInPlayer() + " " + realTime.getOutPlayer());
                        }


                        if (liText.contains("경고")) {
                            realTime = RealTime.builder()
                                    .dateTime(getDateTime())
                                    .timeLine(elements[0])
                                    .event(elements[1])
                                    .warnPlayer(elements[2])
                                    .build();

                            System.out.println(realTime.getDateTime() + " " + realTime.getTimeLine() + " " + realTime.getEvent() + " "
                                    + realTime.getWarnPlayer());

                        }


                        if (liText.contains("퇴장")) {
                            realTime = RealTime.builder()
                                    .dateTime(getDateTime())
                                    .timeLine(elements[0])
                                    .event(elements[1])
                                    .exitPlayer(elements[2])
                                    .build();

                            System.out.println(realTime.getDateTime() + " " + realTime.getTimeLine() + " " + realTime.getEvent() + " "
                                    + realTime.getExitPlayer());

                        }

                        if (liText.contains("VAR")) {
                            realTime = RealTime.builder()
                                    .dateTime(getDateTime())
                                    .timeLine(elements[0])
                                    .event(elements[2])
                                    .varResult(elements[3])
                                    .build();

                            System.out.println(realTime.getDateTime() + " " + realTime.getTimeLine() + " " + realTime.getEvent() + " "
                                    + realTime.getVarResult());

                        }

                        if (liText.contains("추가시간") || liText.contains("후반전")) {
                            realTime = RealTime.builder()
                                    .dateTime(getDateTime())
                                    .event(elements[0])
                                    .build();

                            System.out.println(realTime.getDateTime() + " " + realTime.getTimeLine() + " " + realTime.getEvent());


                        }

                        if (liText.contains("종료")) {
                            realTime = RealTime.builder()
                                    .dateTime(getDateTime())
                                    .event(elements[0])
                                    .build();

                            System.out.println(realTime.getDateTime() + " " + realTime.getTimeLine() + " " + realTime.getEvent());

                            firstEnd = true;
                            break;
                        }


                        if (liText.contains("경기종료")) {
                            realTime = RealTime.builder()
                                    .dateTime(getDateTime())
                                    .event(elements[0])
                                    .build();

                            eventEnd = true;

                            System.out.println(realTime.getDateTime() + " " + realTime.getTimeLine() + " " + realTime.getEvent());

                        }

                        timeLineList.add(realTime);
                    }
                }
                if (firstEnd) {
                    // 휴식시간 대기
                    System.out.println("전반전 종료. 15분 후 후반전 시작.");
                    Thread.sleep(15 * 60 * 1000); // 15분 대기

                    // 후반전 시작 대기
                    System.out.println("후반전 시작 전 대기 시작: " + getDateTime());
                    wait.until(ExpectedConditions.presenceOfElementLocated(By.className("sr-lmt-clock__time")));

                    // 후반전 시작 시간 기록
                    System.out.println("후반전 시작 시간: " + getDateTime());
                    realTime = RealTime.builder()
                            .dateTime(getDateTime())
                            .build();
                    timeLineList.add(realTime);

                    // 후반전 시작 후 다시 크롤링 계속
                    firstEnd = false;

                }
            }
        } catch(InterruptedException e) {
            e.printStackTrace();
        } finally {
            // WebDriver 종료
            driver.quit();
        }
    }

    // 로컬 시간 가져오기
    public static String getDateTime(){
        LocalDateTime now = LocalDateTime.now();

        String dateTime = now.format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss"));

        return dateTime;
    }

}


