package KickIt.server.domain.fixture.dto;

import KickIt.server.domain.fixture.entity.Fixture;
import KickIt.server.domain.teams.EplTeams;
import lombok.*;

import java.util.Date;
import java.util.UUID;

public class FixtureDto {
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class FixtureRequest {
        private UUID id;
        private String season;
        private Date dateTime;
        private EplTeams homeTeam;
        private EplTeams awayTeam;
        private Integer homeTeamScore;
        private Integer awayteamScore;
        private int round;
        private int status;
        private String lineupUrl;

        public Fixture toEntity(){
            Fixture fixture = Fixture.builder()
                    .id(this.id)
                    .season(this.season)
                    .date(this.dateTime)
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

    @Getter
    public static class FixtureResponse{
        private UUID id;
        private String season;
        private Date dateTime;
        private String homeTeam;
        private String awayTeam;
        private Integer homeTeamScore;
        private Integer awayteamScore;
        private int round;
        private int status;
        private String lineupUrl;

        public FixtureResponse(Fixture fixture){
            this.id = fixture.getId();
            this.season = fixture.getSeason();
            this.dateTime = fixture.getDate();
            this.homeTeam = EplTeams.getKrName(fixture.getHomeTeam());
            this.awayTeam = EplTeams.getKrName(fixture.getAwayTeam());
            this.homeTeamScore = fixture.getHomeTeamScore();
            this.awayteamScore = fixture.getAwayteamScore();
            this.round = fixture.getRound();
            this.status = fixture.getStatus();
            this.lineupUrl = fixture.getLineupUrl();
        }
    }
}
