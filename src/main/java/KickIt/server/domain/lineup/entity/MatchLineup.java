package KickIt.server.domain.lineup.entity;

import KickIt.server.domain.fixture.entity.Fixture;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
// 경기별 선발 라인업 정보를 담을 class MatchLineup
public class MatchLineup {
    // 경기 선발 라인업 id
    @Id
    private Long id;
    // 경기
    // 경기 id를 경기 선발 라인업 id와 똑같게 설정
    @OneToOne
    @MapsId
    @JoinColumn(name = "fixture_id")
    private Fixture fixture;
    // 홈팀
    private String homeTeam;
    // 원정팀
    private String awayTeam;
    // 홈팀 포메이션
    private String homeTeamForm;
    // 원정팀 포메이션
    private String awayTeamForm;
    // 홈팀 선발 라인업
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "hometeam_lineup_id")
    private TeamLineup homeTeamLineup;
    // 원정팀 선발 라인업
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "awayteam_lineup_id")
    private TeamLineup awayTeamLineup;

}
