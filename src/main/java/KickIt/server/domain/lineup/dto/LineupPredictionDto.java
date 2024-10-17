package KickIt.server.domain.lineup.dto;

import KickIt.server.domain.lineup.entity.LineupPrediction;
import KickIt.server.domain.lineup.entity.LineupPredictionRepository;
import KickIt.server.domain.lineup.entity.PredictionPlayer;
import KickIt.server.domain.member.entity.Member;
import KickIt.server.domain.teams.entity.Player;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
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

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class LineUpPredictionEditResponse{
        private int participant;

        private int homeFormation;
        private int awayFormation;

        private ResponsePlayerInfo homeGoalkeeper;
        private List<ResponsePlayerInfo> homeDefenders;
        private List<ResponsePlayerInfo> homeMidfielders;
        private List<ResponsePlayerInfo> homeStrikers;

        private ResponsePlayerInfo awayGoalkeeper;
        private List<ResponsePlayerInfo> awayDefenders;
        private List<ResponsePlayerInfo> awayMidfielders;
        private List<ResponsePlayerInfo> awayStrikers;

        private ResponseLineup homeLineups;
        private ResponseLineup awayLineups;

        /*
        public LineUpPredictionEditResponse(LineupPrediction lineupPrediction){
            Long memberId = lineupPrediction.getMember().getMemberId();
            Long fixtureId = lineupPrediction.getFixture().getId();

            this.homeFormation = lineupPrediction.getHomeTeamForm();
            this.awayFormation = lineupPrediction.getAwayTeamForm();


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

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ResponsePlayerInfo{
        private String playerImgURL;
        private String playerName;
        private Integer playerNum;

        public ResponsePlayerInfo(Player player){
            this.playerImgURL = player.getProfileImg();
            this.playerName = player.getName();
            this.playerNum = player.getNumber();
        }
    }

    @Data
    private static class ResponseLineup{
        private ResponsePlayerInfo goalkeeper;
        private List<ResponsePlayerInfo> defenders;
        private List<ResponsePlayerInfo> midfielders;
        private List<ResponsePlayerInfo> strikers;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LineupSaveResponse{
        private int grade;
        private int point;
        public LineupSaveResponse(Member member){
            switch (member.getGrade()){
                case("탱탱볼"):
                    grade = 1;
                    break;
                case("브론즈 축구공"):
                    grade = 2;
                    break;
                case("실버 축구공"):
                    grade = 3;
                    break;
                case("골드 축구공"):
                    grade = 4;
                    break;
                case("다이아 축구공"):
                    grade = 5;
                    break;
            }
            // point는 이후에 추가
        }
    }
}
