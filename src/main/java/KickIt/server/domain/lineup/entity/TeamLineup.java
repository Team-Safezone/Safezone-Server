package KickIt.server.domain.lineup.entity;

import KickIt.server.domain.teams.entity.Player;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
//각 팀별 선발 라인업을 담을 class TeamLineup
public class TeamLineup {
    // 라인업 id
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    // 팀
    private String team;
    // 포메이션
    private String form;
    // 선수 리스트
    // 0 번째 배열은 골키퍼 포함
    @OneToMany(mappedBy = "teamLineup", cascade = CascadeType.ALL)
    private List<Player> players;
    // 감독 이름
    private String director;
    // 후보 선수 리스트
    @OneToMany(mappedBy = "teamLineup", cascade = CascadeType.ALL)
    private ArrayList<Player> benchPlayers;
}
