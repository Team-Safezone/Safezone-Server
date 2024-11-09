package KickIt.server.domain.heartRate.entity;

import KickIt.server.domain.fixture.entity.Fixture;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor
public class FixtureHeartRateStatistics {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fixture_id", nullable = false)
    private Fixture fixture;

    private String startDate;
    private String endDate;

    private int minBPM;
    private int avgBPM;
    private int maxBPM;

    private int homeTeamViewerPercentage;

    public FixtureHeartRateStatistics(Fixture fixture) {
        this.fixture =fixture;
    }

}
