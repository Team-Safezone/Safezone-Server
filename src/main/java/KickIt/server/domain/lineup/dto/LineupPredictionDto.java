package KickIt.server.domain.lineup.dto;

import KickIt.server.domain.lineup.entity.LineupPrediction;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

public class LineupPredictionDto {
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    // fixture request
    public static class LineUpPredictionRequest{
        private int homeFormation;
        private int awayFormation;

        private RequestPlayerInfo homeGoalkeeper;
        private List<RequestPlayerInfo> homeDefenders;
        private List<RequestPlayerInfo> homeMidfielders;
        private List<RequestPlayerInfo> homeStrikers;

        private RequestPlayerInfo awayGoalkeeper;
        private List<RequestPlayerInfo> awayDefenders;
        private List<RequestPlayerInfo> awayMidfielders;
        private List<RequestPlayerInfo> awayStrikers;

        /*
        LineupPrediction toEntity(){
            LineupPrediction lineupPrediction = LineupPrediction.builder().build();
            return lineupPrediction;
        }
         */
    }

    public static class RequestPlayerInfo{
        private String playerName;
        private int playerNum;

        public String getPlayerName(){
            return playerName;
        }
        public int getPlayerNum(){
            return playerNum;
        }
    }
}
