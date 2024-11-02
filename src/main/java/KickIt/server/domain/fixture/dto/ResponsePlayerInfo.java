package KickIt.server.domain.fixture.dto;

import KickIt.server.domain.teams.entity.Player;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResponsePlayerInfo {
    private String playerImgURL;
    private String playerName;
    private Integer playerNum;

    public ResponsePlayerInfo(Player player){
        this.playerImgURL = player.getProfileImg();
        this.playerName = player.getName();
        this.playerNum = player.getNumber();
    }
}
