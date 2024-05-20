package KickIt.server.domain.lineup.entity;

import KickIt.server.domain.teams.EplTeams;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
//각 팀별 선발 라인업을 담을 class TeamLineup
public class TeamLineup {
    // 팀
    private EplTeams team;
    // 포메이션
    private String form;
    // 선수 리스트
    private int[] players;
}
