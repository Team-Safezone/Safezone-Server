package KickIt.server.domain.fixture.dto;

import KickIt.server.domain.fixture.entity.Fixture;
import KickIt.server.domain.teams.EplTeams;
import lombok.*;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.UUID;

public class FixtureDto {
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    // fixture request
    public static class FixtureRequest {
        private Long id;
        private String season;
        private Date dateTime;
        private EplTeams homeTeam;
        private EplTeams awayTeam;
        private Integer homeTeamScore;
        private Integer awayteamScore;
        private int round;
        private int status;
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
    public static class FixtureResponse{
        private Long id;
        private String season;
        private String dateStr;
        private String timeStr;
        private String homeTeam;
        private String awayTeam;
        private Integer homeTeamScore;
        private Integer awayteamScore;
        private int round;
        private int status;

        // entity to dto
        public FixtureResponse(Fixture fixture){
            this.id = fixture.getId();
            this.season = fixture.getSeason();

            // Date로 받아올 때 Timezone에 의한 오차 생김 -> 한국 시간대로 변환
            TimeZone krTimeZone = TimeZone.getTimeZone("Asia/Seoul");
            int offset = krTimeZone.getOffset(fixture.getDate().getTime());
            Date date = new Date(fixture.getDate().getTime() + offset);
            // 가져온 Date를 yyyy-MM-dd와 HH:mm 두 개로 나누어 문자열로 반환
            this.dateStr = new SimpleDateFormat("yyyy-MM-dd").format(date);
            this.timeStr = new SimpleDateFormat("HH:mm").format(date);
            
            this.homeTeam = EplTeams.getKrName(fixture.getHomeTeam());
            this.awayTeam = EplTeams.getKrName(fixture.getAwayTeam());
            this.homeTeamScore = fixture.getHomeTeamScore();
            this.awayteamScore = fixture.getAwayteamScore();
            this.round = fixture.getRound();
            this.status = fixture.getStatus();
        }
    }
}
