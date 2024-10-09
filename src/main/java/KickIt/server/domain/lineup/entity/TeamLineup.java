package KickIt.server.domain.lineup.entity;

import KickIt.server.domain.teams.entity.Player;
import com.fasterxml.jackson.annotation.JsonManagedReference;
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
    @ManyToMany(cascade = CascadeType.ALL)
    @JsonManagedReference
    @JoinTable(
            name = "lineup_players",  // 조인 테이블 이름
            joinColumns = @JoinColumn(name = "team_lineup_id"),  // TeamLineup의 조인 컬럼
            inverseJoinColumns = @JoinColumn(name = "player_id")  // Player의 조인 컬럼
    )
    private List<Player> players;

    // 후보 선수 리스트
    @ManyToMany(cascade = CascadeType.ALL)
    @JsonManagedReference
    @JoinTable(
            name = "lineup_bench",  // 조인 테이블 이름
            joinColumns = @JoinColumn(name = "team_lineup_id"),  // TeamLineup의 조인 컬럼
            inverseJoinColumns = @JoinColumn(name = "player_id")  // Player의 조인 컬럼
    )
    private List<Player> benchPlayers;
}
