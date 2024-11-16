package KickIt.server.domain.heartRate.entity;

import KickIt.server.domain.fixture.entity.Fixture;
import KickIt.server.domain.member.entity.Member;
import KickIt.server.global.util.CreatedAt;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor
public class HeartRateStatistics extends CreatedAt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne
    @JoinColumn(name = "fixture_id", nullable = false)
    private Fixture fixture;

    private int lowHeartRate;
    private int highHeartRate;
    private int averageRate;

    private String teamType;

    public HeartRateStatistics(Member member, Fixture fixture) {
        this.member = member;
        this.fixture = fixture;
    }
}
