package KickIt.server.domain.teams.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "ranking")
// 시즌 별 프리미어리그 팀 순위 정보
public class Ranking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    // id(자동 생성)
    private long id;
    // 팀 정보(내부에 시즌 정보 포함)
    @OneToOne
    Squad squad;
    // 순위 정보
    int teamRank;
    // 팀 경기 횟수
    int matchCount;
    // 승리 횟수
    int winCount;
    // 무승부 횟수
    int drawCount;
    // 패배 횟수
    int loseCount;
    // 승점
    int points;

    // 수정 시각을 저장하는 필드
    @Column(name = "last_updated", nullable = false)
    private LocalDateTime lastUpdated;

    @PrePersist
    @PreUpdate
    public void setLastUpdated() {
        this.lastUpdated = LocalDateTime.now();
    }
}
