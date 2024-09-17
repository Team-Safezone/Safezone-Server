package KickIt.server.domain.teams.entity;

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
// 각 팀별 선수 목록을 나타내기 위한 PlayerRepository
public class Squad {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    // 시즌
    private String season;
    // 팀 이름
    private String team;
    // 팀 로고 url
    private String logoImg;
    // 공격수(Forward) 선수 리스트
    @OneToMany(mappedBy = "squad", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<Player> FWplayers;
    // 미드필더(Midfielder) 선수 리스트
    @OneToMany(mappedBy = "squad", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<Player> MFplayers;
    // 수비수(Defender) 선수 리스트
    @OneToMany(mappedBy = "squad", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<Player> DFplayers;
    // 골키퍼(Goalkeeper) 선수 리스트
    @OneToMany(mappedBy = "squad", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<Player> GKplayers;

    // FW Player 추가 및 양방향 관계 설정
    public void addFWPlayers(List<Player> players) {
        this.FWplayers = players;
        for(Player player : players){
            player.assignSquad(this);
        }
    }

    // MF Player 추가 및 양방향 관계 설정
    public void addMFPlayers(List<Player> players) {
        this.MFplayers = players;
        for(Player player : players){
            player.assignSquad(this);
        }
    }

    // DF Player 추가 및 양방향 관계 설정
    public void addDFPlayers(List<Player> players) {
        this.DFplayers = players;
        for(Player player : players){
            player.assignSquad(this);
        }
    }

    // GK Player 추가 및 양방향 관계 설정
    public void addGKPlayers(List<Player> players) {
        this.GKplayers = players;
        for(Player player : players){
            player.assignSquad(this);
        }
    }
}
