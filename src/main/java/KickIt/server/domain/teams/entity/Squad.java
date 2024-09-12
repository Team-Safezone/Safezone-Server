package KickIt.server.domain.teams.entity;

import KickIt.server.domain.teams.EplTeams;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
// 각 팀별 선수 목록을 나타내기 위한 PlayerRepository
public class Squad {
    // 팀 이름
    private String team;
    // 팀 로고 url
    private String logoImg;
    // 공격수(Forward) 선수 리스트
    private ArrayList<Player> FWplayers;
    // 미드필더(Midfielder) 선수 리스트
    private ArrayList<Player> MFplayers;
    // 수비수(Defender) 선수 리스트
    private ArrayList<Player> DFplayers;
    // 골키퍼(Goalkeeper) 선수 리스트
    private ArrayList<Player> GKplayers;
}
