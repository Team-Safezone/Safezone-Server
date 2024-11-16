package KickIt.server.global.common.crawler;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.Integer.parseInt;


// 크롤링 데이터 파싱
public class RealTimeDataParser {

    private static boolean isMatch2 = false;
    private static boolean isExtra = false;
    private static int gameTime = 45;
    private static int doneTime;

    // 로컬 시간 가져오기
    public static String getDateTime(){
        LocalDateTime now = LocalDateTime.now();

        String dateTime = now.format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss"));

        return dateTime;
    }

    // 경기 실제 시작
    public static String startMatch() {
        return "경기시작";
    }


    // 추가시간 숫자만 출력
    public static String getAddTime(String elements){
        String[] addTime = elements.split("′");

        return addTime[0];
    }

    // 추가시간 이벤트만 출력
    public static String getAddEvent(String elements){
        return "추가선언";
    }

    // VAR 결과, 어시스트 괄호 제거
    public static String rmBracket(String elements) {
        Pattern pattern = Pattern.compile("\\((.*?)\\)");
        Matcher varResult = pattern.matcher(elements);

        if (varResult.find()) {
            return varResult.group(1);
        }

        return null;
    }

    // 후반전
    public static void isHalf() {
        isMatch2 = true;
        isExtra = false;
    }

    // 추가 시간
    public static void isExtra() {
        isExtra = true;
    }

    // 전반? 후반? 추가시간?
    public static int whenHappen() {
        if(isMatch2 && !isExtra){
            return 3; //후반
        } else if (isExtra) {
            return 5; //추가시간
        } else {
            return 1; //전반
        }
    }

    // LocalDateTime을 String으로 변환
    public static String dateToString(LocalDateTime localDateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        return localDateTime.format(formatter);
    }


    // 심박수 비교 시간
    public static String compareTime(LocalDateTime startTime, String eventTime) {
        int minutesToAdd = 0;
        if(!isMatch2 && !isExtra || !isMatch2 && isExtra){
            // 전반전 or 전반전 추가시간
            minutesToAdd = parseInt(eventTime);
        } else if(isMatch2 && !isExtra || isMatch2 && isExtra){
            // 후반전
            minutesToAdd = parseInt(eventTime) - gameTime;
        }

        LocalDateTime updatedDateTime = startTime.plusMinutes(minutesToAdd);
        return dateToString(updatedDateTime);
    }

    // 추가시간 선언
    public static String extraTime(LocalDateTime startTime) {
        LocalDateTime extraTime = startTime.plusMinutes(gameTime);
        return dateToString(extraTime);
    }

    // 하프타임 시간 선언
    public static String halfTime(LocalDateTime startTime, String extraTime) {
        gameTime = gameTime + parseInt(extraTime) ;
        LocalDateTime halfTime = startTime.plusMinutes(gameTime);
        return dateToString(halfTime);
    }

    // 전반, 후반 추가시간 확인
    public static String isBeforeAfter() {
        if (!isMatch2 && isExtra || !isMatch2 && !isExtra) {
            return "45";
        } else if (isMatch2 && isExtra || isMatch2 && !isExtra) {
            return "90";
        }
        return " ";
    }

    // 종료 시간
    public static String isDone(String extraTime) {
        if (!isMatch2 && isExtra){
            doneTime = 45 + parseInt(extraTime);
            return Integer.toString(doneTime);
        } else if (isMatch2 && isExtra) {
            doneTime = 90 + parseInt(extraTime);
            return Integer.toString(doneTime);
        }
        return "0";
    }
}
