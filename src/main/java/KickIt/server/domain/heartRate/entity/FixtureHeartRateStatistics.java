package KickIt.server.domain.heartRate.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor
public class FixtureHeartRateStatistics {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long fixtureId;

    private String startDate;
    private String endDate;

    private int minBPM;
    private int avgBPM;
    private int maxBPM;

    private int homeTeamViewerPercentage;

    public FixtureHeartRateStatistics(Long fixtureId) {
        this.fixtureId = fixtureId;
    }

}
