package KickIt.server.global.common.crawler;

import KickIt.server.domain.realtime.dto.RealTime;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// 크롤링 데이터 파싱
@Component
public class RTCrawlingDataParser {

    // ***** RealTime *****
    public RealTime parseEvent(String eventText, WebElement li) {
        String[] elements = eventText.split("\n");
        RealTime.Builder realTimeBuilder = new RealTime.Builder()
                .dateTime(getDateTime())
                .timeLine(elements[0]);

        if (eventText.contains("골")) {
            List<WebElement> spans = li.findElements(By.tagName("span"));
            for (WebElement span : spans) {
                String ownGoal = span.getText();
                if (ownGoal.contains("골")) {
                    String ownGoalClass = span.getAttribute("class");
                    if (ownGoalClass.contains("ico_goal_own")) {
                        realTimeBuilder.event("자책골")
                                .inform1(elements[2])
                                .inform2(elements.length > 3 ? rmBracket(elements[3]) : "");
                    } else {
                        realTimeBuilder.event(elements[1] + "!")
                                .inform1(elements[2])
                                .inform2(elements.length > 3 ? rmBracket(elements[3]) : "");
                    }
                    break;
                }
            }
        } else if (eventText.contains("교체")) {
            realTimeBuilder.event(elements[1])
                    .inform1(elements[2])
                    .inform2(elements[4]);
        } else if (eventText.contains("경고") || eventText.contains("퇴장")) {
            realTimeBuilder.event(elements[1])
                    .inform1(elements[2]);
        } else if (eventText.contains("VAR")) {
            realTimeBuilder.event(elements[1])
                    .inform1(elements[2])
                    .inform2(rmBracket(elements[3]));
        } else if (eventText.contains("추가시간")) {
            realTimeBuilder.timeLine(getAddTime(elements[0]))
                    .event(getAddEvent(elements[0]));
        } else if (eventText.contains("후반전")) {
            realTimeBuilder.event(elements[0]);
        } else if (eventText.equals("종료")) {
            realTimeBuilder.event("하프타임");
        } else if (eventText.contains("경기종료")) {
            realTimeBuilder.event(elements[0]);
        }

        return realTimeBuilder.build();
    }

    // 로컬 시간 가져오기
    public static String getDateTime(){
        LocalDateTime now = LocalDateTime.now();

        String dateTime = now.format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss"));

        return dateTime;
    }

    // 추가시간 숫자만 출력
    public static String getAddTime(String elements){
        String[] addTime = elements.split("′");

        return addTime[0];
    }

    // 추가시간 이벤트만 출력
    public static String getAddEvent(String elements){
        String[] addEvent = elements.split("′");

        return addEvent[1];
    }

    // VAR 결과, 어시스트 괄호 제거
    public static String rmBracket(String elements) {
        Pattern pattern = Pattern.compile("\\((.*?)\\)");
        Matcher varResult = pattern.matcher(elements);

        if (varResult.find()) {
            return varResult.group(1);
        }

        return "";
    }


}
