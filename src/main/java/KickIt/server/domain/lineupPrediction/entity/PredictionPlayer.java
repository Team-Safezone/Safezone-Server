package KickIt.server.domain.lineupPrediction.entity;

import KickIt.server.domain.teams.entity.Player;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PredictionPlayer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "player_id")
    private Player player;

    // 0: 홈팀 1: 원정팀
    private int team;
    // 0: 골키퍼 1: 수비수 2: 미드필더 3: 공격수
    private int position;

    // 포지션별로 들어온 순서 저장
    private int location;
}
