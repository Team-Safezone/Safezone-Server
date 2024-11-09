package KickIt.server.domain.heartRate.entity;

import KickIt.server.domain.fixture.entity.Fixture;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor
public class TeamHeartRate {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fixture_id", nullable = false)
    private Fixture fixture;

    private String teamType;

    private int heartRateDate;
    private int heartRate;

    public TeamHeartRate(Fixture fixture, String teamType, int heartRateDate, int heartRate) {
        this.fixture = fixture;
        this.teamType = teamType;
        this.heartRateDate = heartRateDate;
        this.heartRate = heartRate;
    }
}
