package KickIt.server.domain.fixture.entity;

import KickIt.server.domain.teams.EplTeams;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.UUID;

// 한 경기의 정보를 담을 class Fixture
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Fixture {
    // 경기 고유 id
    private UUID id;
    // 시즌 정보
    private String season;
    // 경기 날짜 및 시간
    private Date date;
    // 홈팀 이름
    private EplTeams homeTeam;
    // 원정팀 이름
    private EplTeams awayTeam;
    // 홈팀 점수
    private Integer homeTeamScore;
    // 원정팀 점수
    private Integer awayteamScore;
    // 라운드 정보
    private int round;
    // 경기 상태 (종료, 진행 중, 진행 예정)
    // 경기 종료: 0 전반전: 1 후반전: 2 경기 전: 3 연기(예외): 4
    // 연기, 취소, 우천 등 예외는 3으로 처리하되 경기 데이터 저장하기로 변경!
    private int status;
}




