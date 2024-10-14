package KickIt.server.domain.heartRate.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
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

    private int fanQuantity;
    private int totalFanQuantity;

}
