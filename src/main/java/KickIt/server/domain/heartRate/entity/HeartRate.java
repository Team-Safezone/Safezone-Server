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
@Setter
public class HeartRate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private List<Integer> heartRate;
    private List<String> heartRateDate;

    private Long member_id;
    private Long fixture_id;

}
