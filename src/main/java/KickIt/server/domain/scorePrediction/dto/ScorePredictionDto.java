package KickIt.server.domain.scorePrediction.dto;

import KickIt.server.domain.member.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public class ScorePredictionDto {
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    // 우승팀 예측 저장 Request
    public static class ScorePredictionSaveRequest{
        // 사용자가 예측한 홈팀 득점 점수
        int homeTeamScore;
        // 사용자가 예측한 원정팀 득점 점수
        int awayTeamScore;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    // 우승팀 예측 저장 Response
    public static class ScorePredictionSaveResponse{
        // 사용자 등급
        int grade;
        // 사용자가 현재까지 획득한 포인트
        int point;
        public ScorePredictionSaveResponse(Member member){
            this.grade = member.getGrade();
            this.point = member.getPoint();
        }
    }
}
