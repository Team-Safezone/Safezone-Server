package KickIt.server.domain.fixture.entity;

import KickIt.server.domain.teams.EplTeams;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.UUID;

// 한 경기의 정보를 담을 class Fixture
@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Fixture {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id; // 경기 고유 id

    @Column(nullable=false)
    private String season; // 시즌 정보

    @Column(name = "date_time", nullable=false)
    private Date date; // 경기 날짜 및 시간

    @Enumerated(EnumType.STRING)
    private EplTeams homeTeam; // 홈팀 이름

    @Enumerated(EnumType.STRING)
    private EplTeams awayTeam; // 원정팀 이름

    private Integer homeTeamScore; // 홈팀 점수
    private Integer awayteamScore; // 원정팀 점수

    @Column(nullable=false)
    private int round; // 라운드 정보

    // 경기 상태 (종료, 진행 중, 진행 예정)
    // 경기 종료: 0 전반전: 1 하프 타임: 2 후반전: 3 경기 전: 4 연기(예외): 5
    // 연기, 취소, 우천 등 예외는 5로 처리하되 경기 데이터 저장하기로 변경!
    @Column(name = "fixtureStatus", nullable=false)
    private int status;

    @Column(nullable=false)
    private String lineupUrl; // 선발 라인업 크롤링해 올 경기 상세 페이지 Url 정보
}




