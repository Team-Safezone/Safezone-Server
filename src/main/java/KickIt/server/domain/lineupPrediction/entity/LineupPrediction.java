package KickIt.server.domain.lineupPrediction.entity;

import KickIt.server.domain.fixture.entity.Fixture;
import KickIt.server.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@IdClass(LineupPredictionId.class)
// 사용자가 예측한 경기 선발 라인업 정보 entity
public class LineupPrediction {
    // 복합 기본 키 사용
    // 사용자 정보
    @Id
    @ManyToOne
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;
    // 복합 기본 키 사용
    // 경기 정보
    @Id
    @ManyToOne
    @JoinColumn(name = "fixture_id", nullable = false)
    private Fixture fixture;

    // 수정 시각을 저장하는 필드
    @Column(name = "last_updated", nullable = false)
    private LocalDateTime lastUpdated;

    @PrePersist
    @PreUpdate
    public void setLastUpdated() {
        this.lastUpdated = LocalDateTime.now();
    }

    // 양팀 포메이션 정보
    // 0: 4-3-3 포메이션
    // 1: 4-2-3-1 포메이션
    // 2: 4-4-2 포메이션
    // 3: 3-4-3 포메이션
    // 4: 4-5-1 포메이션
    // 5: 3-5-2 포메이션

    // 홈팀 포메이션
    private int homeTeamForm;
    // 원정팀 포메이션
    private int awayTeamForm;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinTable(
            name = "lineup_prediction_players",  // 하나의 조인 테이블
            joinColumns = {
                    @JoinColumn(name = "lineup_prediction_member_id", referencedColumnName = "member_id"),
                    @JoinColumn(name = "lineup_prediction_fixture_id", referencedColumnName = "fixture_id")
            },
            inverseJoinColumns = {
                    @JoinColumn(name = "player_id"),
            }
    )
    private List<PredictionPlayer> players;
}
