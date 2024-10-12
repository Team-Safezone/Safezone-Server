package KickIt.server.domain.heartRate.entity;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@NoArgsConstructor
public class HeartRate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private int heartRate;
    private int heartRateDate;

    private Long memberId;
    private Long fixtureId;

    public HeartRate(Long memberId, Long fixtureId, int heartRate, int heartRateDate) {
        this.memberId = memberId;
        this.fixtureId = fixtureId;
        this.heartRate = heartRate;
        this.heartRateDate = heartRateDate;
    }

}
