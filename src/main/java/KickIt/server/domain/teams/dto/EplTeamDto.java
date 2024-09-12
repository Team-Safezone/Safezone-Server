package KickIt.server.domain.teams.dto;

import KickIt.server.domain.teams.entity.EplTeam;
import lombok.*;

public class EplTeamDto {
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class EplTeamRequest{
        private String team;
        private String engName;
        private String krName;
        private String krFullName;

        public EplTeam toEntity(){
            EplTeam eplTeam = EplTeam.builder()
                    .team(this.team)
                    .engName(this.engName)
                    .krName(this.krName)
                    .krFullName(this.krFullName)
                    .build();
            return eplTeam;
        }
    }

    @Getter
    public static class EplTeamResponse{
        private String team;
        private String engName;
        private String krName;
        private String krFullName;

        public EplTeamResponse(EplTeam eplTeam){
            this.team = eplTeam.getTeam();
            this.engName = eplTeam.getEngName();
            this.krName = eplTeam.getKrName();
            this.krFullName = eplTeam.getKrFullName();
        }
    }
}
