package KickIt.server.domain.heartRate.entity;

import KickIt.server.domain.fixture.entity.Fixture;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor
public class TeamHeartRateStatistics {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fixture_id", nullable = false)
    private Fixture fixture;

    private String teamType;

    private int minBPM;
    private int avgBPM;
    private int maxBPM;

    public TeamHeartRateStatistics(Fixture fixture, String teamType, int minBPM, int avgBPM, int maxBPM) {
        this.fixture = fixture;
        this.teamType = teamType;
        this.minBPM = minBPM;
        this.avgBPM = avgBPM;
        this.maxBPM = maxBPM;
    }
}
