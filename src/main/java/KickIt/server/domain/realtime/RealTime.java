package KickIt.server.domain.realtime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RealTime {
    // 이벤트 발생시간(크롤링 발생 시간 + 전반전, 후반전 시작 시간)
    private String dateTime;
    // 타임라인 시간
    private String timeLine;
    // 발생 이벤트
    private String event;
    // 첫 번째 정보
    private String inform1;
    // 두 번째 정보
    private String inform2;

    /*
    // 골 넣은 선수
    private String goalPlayer;
    // 어시스트 선수
    private String assiPlayer;
    // 경고 받은 선수
    private String warnPlayer;
    // 두번째 경고 받은 선수
    private String warnPlayer2;
    // 퇴장 선수
    private String exitPlayer;
    // 들어오는 선수
    private String inPlayer;
    // 나가는 선수
    private String outPlayer;
    // VAR 판독
    private String var;
    // VAR 판독 결과
    private String varResult;
    // 전반전 추가시간
    private String addTime;
    // 후반전 추가시간
    private String addTime2;
    // 종료
    private String finish;
    // 경기 종료
    private String finishAll;

     */

}