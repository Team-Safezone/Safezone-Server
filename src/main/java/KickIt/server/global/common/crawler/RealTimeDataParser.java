package KickIt.server.global.common.crawler;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// 크롤링 데이터 파싱
@Component
public class RealTimeDataParser {

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

        return "";
    }


}
