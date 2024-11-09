package KickIt.server.domain.realtime.entity;

import KickIt.server.domain.fixture.entity.Fixture;
import KickIt.server.global.util.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class RealTime extends BaseEntity {
    // 타임라인 id(PK)
    @Id
    @GeneratedValue
    private Long id;

    // 경기 id (FK)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fixture_id", nullable = false)
    private Fixture fixture;

    @Column(nullable = false)
    private int eventCode;

    @Column(nullable = false, length = 3)
    private String time;

    @Column(nullable = false, length = 30)
    private String eventTime;

    @Column(nullable = false, length = 15)
    private String eventName;

    @Column(length = 20)
    private String player1;

    @Column(length = 20)
    private String player2;

    @Column(length = 10)
    private String teamName;

    private String teamUrl;

}