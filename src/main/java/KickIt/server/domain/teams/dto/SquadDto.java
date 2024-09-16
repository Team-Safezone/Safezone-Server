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
        private List<Player> FWplayers;
        private List<Player> MFplayers;
        private List<Player> DFplayers;
        private List<Player> GKplayers;

        public Squad toEntity(){
            Squad squad = Squad.builder()
                    .season(this.season)
                    .team(this.team)
                    .logoImg(this.logoImg)
                    .FWplayers(this.FWplayers)
                    .MFplayers(this.MFplayers)
                    .DFplayers(this.DFplayers)
                    .GKplayers(this.GKplayers)
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
        private List<Player> FWplayers;
        private List<Player> MFplayers;
        private List<Player> DFplayers;
        private List<Player> GKplayers;

        public SquadResponse(Squad squad){
            this.id = squad.getId();
            this.season = squad.getSeason();
            this.team = squad.getTeam();
            this.logoImg = squad.getLogoImg();
            this.FWplayers = squad.getFWplayers();
            this.MFplayers = squad.getMFplayers();
            this.DFplayers = squad.getDFplayers();
            this.GKplayers = squad.getGKplayers();
        }
    }
}
