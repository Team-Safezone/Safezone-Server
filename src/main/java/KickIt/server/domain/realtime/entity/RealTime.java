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

    @Column(nullable = false, length = 5)
    private String time;

    @Column(nullable = false, length = 50)
    private String eventTime;

    @Column(nullable = false, length = 50)
    private String eventName;

    @Column(length = 50)
    private String player1;

    @Column(length = 50)
    private String player2;

    @Column(length = 50)
    private String teamName;

    private String teamUrl;

}