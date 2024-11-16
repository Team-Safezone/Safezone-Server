package KickIt.server.domain.lineupPrediction.dto;

import KickIt.server.domain.fixture.dto.ResponsePlayerInfo;
import KickIt.server.domain.lineup.dto.MatchLineupDto;
import KickIt.server.domain.member.entity.Member;
import KickIt.server.domain.teams.PlayerPosition;
import KickIt.server.domain.teams.entity.Player;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

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
    @Builder
    // 선발라인업 예측 수정 API 호출 시 반환할 선발 라인업 정보 형태
    public static class ResponseLineup{
        private ResponsePlayerInfo goalkeeper;
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
            grade = member.getGrade();
            point = member.getPoint();
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

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    // 선발 라인업 예측 결과 조회 API 호출 시 반환할 Response
    public static class LineupResultInquireResponse{
        private int participant;

        private String homeFormation;
        private MatchLineupDto.MatchPosPlayersDto homeLineups;
        private String awayFormation;
        private MatchLineupDto.MatchPosPlayersDto awayLineups;

        private int userHomeFormation;
        private ResponseLineup userHomePrediction;
        private int userAwayFormation;
        private ResponseLineup userAwayPrediction;

        private int avgHomeFormation;
        private ResponseLineup avgHomePrediction;
        private int avgAwayFormation;
        private ResponseLineup avgAwayPrediction;

        private List<Boolean> userPrediction;
        private List<Boolean> avgPrediction;
    }

}
