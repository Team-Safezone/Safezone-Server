package KickIt.server.domain.teams.dto;

import KickIt.server.domain.teams.entity.Player;
import KickIt.server.domain.teams.entity.Squad;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

public class SquadDto {
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class SquadRequest{
        private String season;
        private String team;
        private String logoImg;
        private List<Player> players;

        public Squad toEntity(){
            Squad squad = Squad.builder()
                    .season(this.season)
                    .team(this.team)
                    .logoImg(this.logoImg)
                    .players(this.players)
                    .build();
            return squad;
        }
    }

    @Getter
    public static class SquadResponse{
        private Long id;
        private String season;
        private String team;
        private String logoImg;
        private List<Player> players;

        public SquadResponse(Squad squad){
            this.id = squad.getId();
            this.season = squad.getSeason();
            this.team = squad.getTeam();
            this.logoImg = squad.getLogoImg();
            this.players = squad.getPlayers();
        }
    }

    @Data
    @Builder
    // 프리미어리그 팀, Url 조회 API 호출 시 반환할 Response class
    public static class EplNameUrlResponse{
        private String teamUrl;
        private String teamName;
    }
}
