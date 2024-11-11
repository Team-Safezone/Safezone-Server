package KickIt.server.domain.fixture.dto;

import KickIt.server.domain.fixture.entity.Fixture;
import KickIt.server.domain.teams.service.SquadService;
import KickIt.server.domain.teams.service.TeamNameConvertService;
import lombok.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Component
public class FixtureDto {
    private final TeamNameConvertService teamNameConvertService;

    @Autowired
    public FixtureDto(TeamNameConvertService teamNameConvertService) {
        this.teamNameConvertService = teamNameConvertService;
    }

    @Autowired
    private SquadService squadService;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    // fixture request
    public static class FixtureRequest {
        private Long id;
        private String season;
        private Date dateTime;
        private String homeTeam;
        private String awayTeam;
        private Integer homeTeamScore;
        private Integer awayteamScore;
        private int round;
        private int status;
        private String stadium;
        private String lineupUrl;

        // dto to entity
        public Fixture toEntity(){
            Fixture fixture = Fixture.builder()
                    .id(this.id)
                    .season(this.season)
                    .date((Timestamp) this.dateTime)
                    .homeTeam(this.homeTeam)
                    .awayTeam(this.awayTeam)
                    .homeTeamScore(this.homeTeamScore)
                    .awayteamScore(this.awayteamScore)
                    .round(this.round)
                    .status(this.status)
                    .lineupUrl(this.lineupUrl)
                    .build();
            return fixture;
        }
    }

    // fixture response
    @Getter
    public class FixtureResponse{
        private Long id;
        private String season;
        private String dateStr;
        private String timeStr;
        private String homeTeamEmblemUrl;
        private String awayTeamEmblemUrl;
        private String homeTeam;
        private String awayTeam;
        private Integer homeTeamScore;
        private Integer awayteamScore;
        private int round;
        private int status;
        private String stadium;

        // entity to dto
        public FixtureResponse(Fixture fixture){
            this.id = fixture.getId();
            this.season = fixture.getSeason();
            
            // 가져온 Date를 yyyy-MM-dd와 HH:mm 두 개로 나누어 문자열로 반환
            this.dateStr = new SimpleDateFormat("yyyy-MM-dd").format(fixture.getDate());
            this.timeStr = new SimpleDateFormat("HH:mm").format(fixture.getDate());
            this.homeTeamEmblemUrl = squadService.getTeamLogoImg(fixture.getSeason(), fixture.getHomeTeam());
            this.awayTeamEmblemUrl = squadService.getTeamLogoImg(fixture.getSeason(), fixture.getAwayTeam());
            this.homeTeam = teamNameConvertService.convertToKrName(fixture.getHomeTeam());
            this.awayTeam = teamNameConvertService.convertToKrName(fixture.getAwayTeam());
            this.homeTeamScore = fixture.getHomeTeamScore();
            this.awayteamScore = fixture.getAwayteamScore();
            this.round = fixture.getRound();
            this.status = fixture.getStatus();
            this.stadium = fixture.getStadium();
        }
    }

    // 한달 경기 일정 정보 조회 response class
    // soccerTeamNames 항목 유무 매개변수로 받아옴 (해당 API 호출 시 팀 네임 함께 입력되었는지 여부)
    @Getter
    public class FixtureDateResponse{
        private String soccerSeason;
        private List<String> matchDates;
        private List<String> soccerTeamNames;

        public FixtureDateResponse(List<Fixture> fixtures, Boolean isTeamNameExist){
            matchDates = new ArrayList<>();
            for (Fixture fixture: fixtures){
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd");
                sdf.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
                this.matchDates.add(sdf.format(new Date(fixture.getDate().getTime())));
            }
            soccerSeason = fixtures.get(0).getSeason();
            if(!isTeamNameExist){
                soccerTeamNames = squadService.getSeasonSquads(soccerSeason);
            }
        }
    }

    @Data
    @Builder
    public static class DiaryFixtureResponse{
        private List<String> soccerTeamNames; // 해당 년도/월에 따른 프리미어리그 내의 전체 팀 이름 리스트
        private List<DiaryFixture> matches; // 경기 리스트
        private Boolean isLeftExist; // 왼쪽 달 경기 조회 가능 여부 (이전 달에 경기 있는지 체크)
        private Boolean isRightExist; // 오른쪽 달 경기 조회 가능 여부 (다음 달에 경기 있는지 체크)
    }

    @Data
    @Builder
    public static class DiaryFixture{
        private Long matchId; // 축구 경기 고유 ID
        private String matchDate; // 축구 경기 날짜
        private String matchTime; // 축구 경기 시간
        private String homeTeamEmblemURL; // 홈팀 로고 이미지 URL
        private String awayTeamEmblemUrl; // 원정팀 로고 이미지 URL
        private String homeTeamName; // 홈팀 이름
        private String awayTeamName; // 원정팀 이름
        private Integer homeTeamScore; // 홈팀 스코어
        private Integer awayTeamScore; // 원정팀 스코어
    }
}
