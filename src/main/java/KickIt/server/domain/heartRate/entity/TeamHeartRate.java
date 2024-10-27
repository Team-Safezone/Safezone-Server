package KickIt.server.domain.heartRate.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor
public class TeamHeartRate {

    @Id
    @GeneratedValue
    private Long id;

    private Long fixtureId;

    private String teamType;

    private int heartRateDate;
    private int heartRate;

    public TeamHeartRate(Long fixtureId, String teamType, int heartRateDate, int heartRate) {
        this.fixtureId = fixtureId;
        this.teamType = teamType;
        this.heartRateDate = heartRateDate;
        this.heartRate = heartRate;
    }
}
