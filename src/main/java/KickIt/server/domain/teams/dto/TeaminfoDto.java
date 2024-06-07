package KickIt.server.domain.teams.dto;

import KickIt.server.domain.teams.entity.Teaminfo;
import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TeaminfoDto {
    private int ranking;
    private String team;
    private String logoUrl;

    public TeaminfoDto(Teaminfo teaminfo){
        this.ranking = teaminfo.getRanking();
        this.team = teaminfo.getTeam().toString();
        this.logoUrl = teaminfo.getLogoUrl();
    }
}