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
public class HeartRateStatistics {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long memberId;
    private Long fixtureId;

    private int lowHeartRate;
    private int highHeartRate;

    private String teamType;


    public HeartRateStatistics(Long memberId, Long fixtureId) {
        this.memberId = memberId;
        this.fixtureId = fixtureId;
    }

}
