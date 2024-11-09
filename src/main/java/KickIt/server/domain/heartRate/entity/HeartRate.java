package KickIt.server.domain.heartRate.entity;


import KickIt.server.domain.fixture.entity.Fixture;
import KickIt.server.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@NoArgsConstructor
public class HeartRate {

    // 심박수 데이터 저장
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private int heartRateDate;
    @Column(nullable = false)
    private int heartRate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fixture_id", nullable = false)
    private Fixture fixture;

    public HeartRate(Member member, Fixture fixture, int heartRate, int heartRateDate) {
        this.member = member;
        this.fixture = fixture;
        this.heartRate = heartRate;
        this.heartRateDate = heartRateDate;
    }

}
