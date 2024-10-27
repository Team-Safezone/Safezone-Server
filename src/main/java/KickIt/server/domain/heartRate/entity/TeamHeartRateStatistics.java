package KickIt.server.domain.heartRate.entity;

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

    private Long fixtureId;

    private String teamType;

    private int minBPM;
    private int avgBPM;
    private int maxBPM;

    public TeamHeartRateStatistics(Long fixtureId, String teamType, int minBPM, int avgBPM, int maxBPM) {
        this.fixtureId = fixtureId;
        this.teamType = teamType;
        this.minBPM = minBPM;
        this.avgBPM = avgBPM;
        this.maxBPM = maxBPM;
    }
}
