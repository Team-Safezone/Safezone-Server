package KickIt.server.domain.lineupPrediction.entity;

import java.io.Serializable;
import java.util.Objects;

// LineupPrediction Entity class의 복합 키(경기 id + 사용자 id) 사용을 위한 클래스
// ScorePrediction Entity에도 사용
public class LineupPredictionId implements Serializable {
    private Long member;  // member_id
    private Long fixture;   // fixture_id

    // 기본 생성자
    public LineupPredictionId() {}

    // 파라미터 생성자
    public LineupPredictionId(Long member, Long fixture) {
        this.member = member;
        this.fixture = fixture;
    }

    @Override
    public int hashCode() {
        return Objects.hash(member, fixture);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        LineupPredictionId that = (LineupPredictionId) obj;
        return Objects.equals(member, that.member) && Objects.equals(fixture, that.fixture);
    }
}
