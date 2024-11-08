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

    private int eventCode;
    private String time;
    private String eventTime;
    private String eventName;
    private String player1;
    private String player2;
    private String teamName;
    private String teamUrl;

}