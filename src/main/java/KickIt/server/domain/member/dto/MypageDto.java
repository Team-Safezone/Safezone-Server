package KickIt.server.domain.member.dto;

import KickIt.server.domain.teams.service.TeamNameConvertService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class MypageDto {
    String nickname;
    int goalCount;
    List<FavoriteTeamsUrl> favoriteTeamsUrl;

    @Getter
    @Setter
    public static class FavoriteTeamsUrl {
        String teamName;
        String teamUrl;

        public FavoriteTeamsUrl(String team, String logoImg) {
            this.teamName = team;
            this.teamUrl = logoImg;
        }
    }

}
