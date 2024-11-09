package KickIt.server.domain.realtime.entity;

import KickIt.server.domain.fixture.entity.Fixture;
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
public class RealTime {
    // 경기 고유 id
    @Id
    @GeneratedValue
    private Long id;

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

    @Column(length = 50)
    private String teamUrl;

}