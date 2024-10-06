package KickIt.server.domain.heartRate.entity;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
public class HeartRate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private int heartRate;
    private int heartRateDate;

    private Long member_id;
    private Long fixture_id;

    public HeartRate(Long member_id, Long fixture_id, int heartRate, int heartRateDate) {
        this.member_id = member_id;
        this.fixture_id = fixture_id;
        this.heartRate = heartRate;
        this.heartRateDate = heartRateDate;
    }
}
