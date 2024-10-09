package KickIt.server.domain.heartRate.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Getter;

@Getter
@Entity
public class HeartRateStatistics {

    @Id
    @GeneratedValue
    private Long id;

    private Long memberId;
    private Long fixtureId;

    private String startDate;
    private String endDate;

    private int lowHeartRate;
    private int highHeartRate;

    private int minBPM;
    private int avgBPM;
    private int maxBPM;

    // 추가 작성 예정


    public HeartRateStatistics(Long memberId, Long fixtureId) {
        this.memberId = memberId;
        this.fixtureId = fixtureId;
    }

}
