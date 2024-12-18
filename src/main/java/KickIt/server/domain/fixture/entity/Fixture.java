package KickIt.server.domain.fixture.entity;

import KickIt.server.domain.diary.entity.DiaryReport;
import KickIt.server.domain.heartRate.entity.*;
import KickIt.server.domain.realtime.entity.RealTime;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

// 한 경기의 정보를 담을 class Fixture
@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Fixture {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "fixtureIdSeq")
    @SequenceGenerator(name="fixtureIdSeq", sequenceName = "fixtureIdSeq", allocationSize = 50, initialValue = 1)
    @Column(columnDefinition = "BIGINT")
    private Long id; // 경기 고유 id

    @Column(nullable=false)
    private String season; // 시즌 정보

    @Column(nullable=false)
    private Timestamp date; // 경기 날짜 및 시간

    @Column(nullable = false)
    private String homeTeam; // 홈팀 이름

    @Column(nullable = false)
    private String awayTeam; // 원정팀 이름

    @Column
    private Integer homeTeamScore; // 홈팀 점수

    @Column
    private Integer awayteamScore; // 원정팀 점수

    @Column(nullable=false)
    private int round; // 라운드 정보

    // 경기 상태 (종료, 진행 중, 진행 예정)
    // (전) 경기 종료: 0 전반전: 1 하프 타임: 2 후반전: 3 경기 전: 4 연기(예외): 5
    // ! 변경됨 ! (현재) 경기 예정: 0 경기 중: 1 휴식 시간: 2 경기 종료: 3 경기 연기: 4
    // 연기, 취소, 우천 등 예외는 5로 처리하되 경기 데이터 저장하기로 변경!
    @Column(name = "status", nullable=false)
    private int status;

    @Column(name="stadium", columnDefinition = "VARCHAR(32)", nullable = false)
    private String stadium;

    @Column(name = "lineupUrl")
    private String lineupUrl; // 선발 라인업 크롤링해 올 경기 상세 페이지 Url 정보

    @OneToMany(mappedBy = "fixture", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder. Default
    private List<HeartRate> heartRateArrayList = new ArrayList<>();

    @OneToMany(mappedBy = "fixture", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder. Default
    private List<HeartRateStatistics> heartRateStatisticsArrayList = new ArrayList<>();

    @OneToMany(mappedBy = "fixture", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder. Default
    private List<TeamHeartRate> teamHeartRateArrayList = new ArrayList<>();

    @OneToMany(mappedBy = "fixture", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder. Default
    private List<TeamHeartRateStatistics> teamHeartRateStatisticsArrayList = new ArrayList<>();

    @OneToMany(mappedBy = "fixture", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder. Default
    private List<FixtureHeartRateStatistics> fixtureHeartRateStatisticsArrayList = new ArrayList<>();

    @OneToMany(mappedBy = "fixture", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder. Default
    private List<RealTime> realTimeArrayList = new ArrayList<>();

}





