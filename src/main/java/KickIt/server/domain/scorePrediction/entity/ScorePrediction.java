package KickIt.server.domain.scorePrediction.entity;

import KickIt.server.domain.fixture.entity.Fixture;
import KickIt.server.domain.lineupPrediction.entity.LineupPredictionId;
import KickIt.server.domain.member.entity.Member;
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
@IdClass(LineupPredictionId.class)
// 우승팀 예측하기 과업의 사용자 예측 데이터 entity
public class ScorePrediction {
    @Id
    @ManyToOne
    @JoinColumn(name = "fixture_id", nullable = false)
    // 경기 정보
    Fixture fixture;
    @Id
    @ManyToOne
    @JoinColumn(name = "member_id", nullable = false)
    // 사용자 정보
    Member member;

    // 사용자가 예측한 홈팀 점수
    Integer homeTeamScore;
    // 사용자가 예측한 원정팀 점수
    Integer awayTeamScore;
}
