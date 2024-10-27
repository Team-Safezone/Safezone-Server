package KickIt.server.domain.lineup.dto;

import KickIt.server.domain.lineup.entity.LineupPrediction;
import KickIt.server.domain.lineup.entity.LineupPredictionRepository;
import KickIt.server.domain.lineup.entity.PredictionPlayer;
import KickIt.server.domain.member.entity.Member;
import KickIt.server.domain.teams.PlayerPosition;
import KickIt.server.domain.teams.entity.Player;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

// 선발 라인업 예측 DTO
@Component
public class LineupPredictionDto {
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    // 선발 라인업 예측 Request
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
    // 선발 라인업 수정 API가 호출 후 반환할 Response
    public static class LineUpPredictionEditResponse{
        private int participant;

        private int userHomeFormation;
        private int userAwayFormation;
        private ResponseLineup userHomePrediction;
        private ResponseLineup userAwayPrediction;

        private int avgHomeFormation;
        private ResponseLineup avgHomePrediction;
        private int avgAwayFormation;
        private ResponseLineup avgAwayPrediction;

        private List<Boolean> userPrediction;
        private List<Boolean> avgPrediction;
    }

    // RequestBody로 들어오는 선수 정보 형식
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
    // 선발라인업 예측 수정 API 호출 시 반환할 선수 정보 형태
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
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    // 선발라인업 예측 수정 API 호출 시 반환할 선발 라인업 정보 형태
    public static class ResponseLineup{
        private List<ResponsePlayerInfo> goalkeeper;
        private List<ResponsePlayerInfo> defenders;
        private List<ResponsePlayerInfo> midfielders;
        private List<ResponsePlayerInfo> strikers;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    // 선발라인업 예측 저장 API 호출 시 반환할 Response
    public static class LineupSaveResponse{
        private int grade;
        private int point;
        public LineupSaveResponse(Member member){
            // 나중에 grade 숫자로 바뀌면 수정할 것 !!!
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

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    // 선발 라인업 예측 조회 API 호출 시 반환할 Response
    public static class LineupInquireResponse{
        List<ResponsePlayerInfo2> homePlayers;
        List<ResponsePlayerInfo2> awayPlayers;
        InquiredLineupPrediction homePrediction;
        InquiredLineupPrediction awayPrediction;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    // 선발라인업 예측 조회 API 호출 시 반환할 선수 정보 형태
    public static class ResponsePlayerInfo2{
        private String playerImgURL;
        private String playerName;
        private Integer playerNum;
        private Integer playerPos;

        public ResponsePlayerInfo2(Player player){
            this.playerImgURL = player.getProfileImg();
            this.playerName = player.getName();
            this.playerNum = player.getNumber();
            this.playerPos = convertPositionToInt(player.getPosition());
        }

        Integer convertPositionToInt(PlayerPosition p){
            return switch (p) {
                case GK -> 0;
                case DF -> 1;
                case MF -> 2;
                case FW -> 3;
            };
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class InquiredLineupPrediction{
        private int formation;
        private ResponsePlayerInfo goalkeeper;
        private List<ResponsePlayerInfo> defenders;
        private List<ResponsePlayerInfo> midfielders;
        private List<ResponsePlayerInfo> strikers;

    }

}
