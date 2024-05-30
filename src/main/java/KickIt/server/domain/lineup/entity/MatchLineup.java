package KickIt.server.domain.lineup.entity;

import KickIt.server.domain.teams.EplTeams;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
// 경기별 선발 라인업 정보를 담을 class MatchLineup
public class MatchLineup {
    // 경기 고유 id
    private UUID id;
    // 홈팀
    private EplTeams homeTeam;
    // 원정팀
    private EplTeams awayTeam;
    // 홈팀 포메이션
    private String homeTeamForm;
    // 원정팀 포메이션
    private String awayTeamForm;
    // 홈팀 선발 라인업
    private TeamLineup homeTeamLineup;
    // 원정팀 선발 라인업
    private TeamLineup awayTeamLineup;

}