package KickIt.server.global.common.crawler;

import KickIt.server.domain.fixture.entity.Fixture;
import KickIt.server.domain.realtime.RealTime;
import KickIt.server.global.util.WebDriverUtil;
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
    List<RealTime> getRealTime(String year, String month, String day){
        //웹 페이지 이동
        WebDriver driver = WebDriverUtil.getChromeDriver();
        // 임시 페이지 지정 이동
        driver.get("https://sports.daum.net/match/80085243?tab=cast");

        /*

        // 구현 수정 예정
        // 페이지 이동
        driver.get("https://sports.daum.net/" + fixture.getLineupUrl() + "?tab=cast");

        지금은 실시간 랜덤 경기 정보 받아옴
        나중에 선호하는 팀에 대한 경기 정보 받아보는 코드 추가 필요

        */


        // 이벤트 종료 여부를 저장하는 eventEnd
        boolean eventEnd = false;

        // 이전에 저장된 정보
        Set<String> previousList = new HashSet<>();

        // 저장할 리스트
        List<RealTime> timeLineList = new ArrayList<>();

        try {
            // 이벤트 업데이트 최소 시간 (30분)
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofMinutes(30));

            System.out.println("크롤링 시작 시간: " + getDateTime());

            // "경기 시작 후 업데이트 됩니다." 화면이 없어질 때까지 대기
            wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector("box_comp.box_relay .empty_data .desc_empty")));
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
                        if(liText.contains("0′")){
                            realTime = RealTime.builder()
                                    .dateTime(getDateTime())
                                    .timeLine(elements[0])
                                    .build();
                            System.out.println(realTime.getDateTime() + " " + realTime.getTimeLine());
                        }

                        // timeline 리스트에 추가
                        if (liText.contains("골")) {
                            // 어시스트 있을 때
                            if(elements.length > 3) {
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
                                System.out.println(realTime.getDateTime() + " " +  realTime.getTimeLine() + " " +  realTime.getEvent() + " "
                                        + realTime.getGoalPlayer());
                            }

                        }

                        if(liText.contains("교체")){
                            realTime = RealTime.builder()
                                    .dateTime(getDateTime())
                                    .timeLine(elements[0])
                                    .event(elements[1])
                                    .inPlayer(elements[2])
                                    .outPlayer(elements[4])
                                    .build();

                            System.out.println(realTime.getDateTime() + " " +  realTime.getTimeLine() + " " +  realTime.getEvent() + " "
                                    + realTime.getInPlayer() + " " + realTime.getOutPlayer());
                        }


                        if(liText.contains("경고")){
                            realTime = RealTime.builder()
                                    .dateTime(getDateTime())
                                    .timeLine(elements[0])
                                    .event(elements[1])
                                    .warnPlayer(elements[2])
                                    .build();

                            System.out.println(realTime.getDateTime() + " " +  realTime.getTimeLine() + " " +  realTime.getEvent() + " "
                                    + realTime.getWarnPlayer());

                            }


                        if(liText.contains("퇴장")){
                            realTime = RealTime.builder()
                                    .dateTime(getDateTime())
                                    .timeLine(elements[0])
                                    .event(elements[1])
                                    .exitPlayer(elements[2])
                                    .build();

                            System.out.println(realTime.getDateTime() + " " +  realTime.getTimeLine() + " " +  realTime.getEvent() + " "
                                    + realTime.getExitPlayer());

                        }

                        if(liText.contains("VAR")){
                            realTime = RealTime.builder()
                                    .dateTime(getDateTime())
                                    .timeLine(elements[0])
                                    .event(elements[2])
                                    .varResult(elements[3])
                                    .build();

                            System.out.println(realTime.getDateTime() + " " +  realTime.getTimeLine() + " " +  realTime.getEvent() + " "
                                    + realTime.getVarResult());

                        }

                        if(liText.contains("추가시간")){
                            realTime = RealTime.builder()
                                    .dateTime(getDateTime())
                                    .event(elements[0])
                                    .build();

                            System.out.println(realTime.getDateTime() + " " +  realTime.getTimeLine() + " " +  realTime.getEvent());


                        }

                        if(liText.contains("후반전")){
                            realTime = RealTime.builder()
                                    .dateTime(getDateTime())
                                    .event(elements[0])
                                    .build();

                            System.out.println(realTime.getDateTime() + " " +  realTime.getTimeLine() + " " +  realTime.getEvent());

                        }

                        if(liText.contains("종료")){
                            realTime = RealTime.builder()
                                    .dateTime(getDateTime())
                                    .event(elements[0])
                                    .build();

                            System.out.println(realTime.getDateTime() + " " +  realTime.getTimeLine() + " " +  realTime.getEvent());

                        }


                        if(liText.contains("경기종료")){
                            realTime = RealTime.builder()
                                    .dateTime(getDateTime())
                                    .event(elements[0])
                                    .build();

                            eventEnd = true;

                            System.out.println(realTime.getDateTime() + " " +  realTime.getTimeLine() + " " +  realTime.getEvent());

                        }

                        timeLineList.add(realTime);
                    }
                }

            }
        } catch(InterruptedException e) {
            e.printStackTrace();
        } finally {
            // WebDriver 종료
            driver.quit();
        }

        return timeLineList;
    }

    // 로컬 시간 가져오기
    public static String getDateTime(){
        LocalDateTime now = LocalDateTime.now();

        String dateTime = now.format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss"));

        return dateTime;
    }

}


