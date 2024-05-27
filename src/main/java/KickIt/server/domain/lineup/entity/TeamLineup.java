package KickIt.server.domain.lineup.entity;

import KickIt.server.domain.teams.EplTeams;
import KickIt.server.domain.teams.entity.Player;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

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
    // 0 번째 배열은 골키퍼 포함
    private ArrayList<List<Player>> players;
    // 감독 이름
    private String director;
    // 후보 선수 리스트
    private ArrayList<Player> benchPlayers;
}
