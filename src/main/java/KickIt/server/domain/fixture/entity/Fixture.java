package KickIt.server.domain.fixture.entity;

import KickIt.server.domain.teams.EplTeams;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;

// 한 경기의 정보를 담을 class Fixture
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Fixture {
    // 경기 날짜 및 시간
    private Date date;
    // 경기 장소
    private String stadium;
    // 홈팀 이름
    private EplTeams homeTeam;
    // 원정팀 이름
    private EplTeams awayTeam;
    // 홈팀 점수
    private Integer homeTeamScore;
    // 원정팀 점수
    private Integer awayteamScore;

}




