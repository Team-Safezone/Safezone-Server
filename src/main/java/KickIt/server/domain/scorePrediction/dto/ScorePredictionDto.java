package KickIt.server.domain.scorePrediction.dto;

import KickIt.server.domain.member.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

public class ScorePredictionDto {
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    // 우승팀 예측 저장 / 수정 Request
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

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    // 우승팀 예측 수정 Response
    public static class ScorePredictionEditResponse{
        int participant; // 예측에 참여한 사람 수
        int homeTeamScore; // 나의 홈팀 예상 스코어
        int awayTeamScore;  // 나의 원정팀 예상 스코어

        int avgHomeTeamScore; // 평균 홈팀 예상 스코어
        int avgAwayTeamScore; // 평균 원정팀 예상 스코어
        // 사용자의 예측 성공 & 실패 여부
        // 홈팀 예측 성공 여부 + 원정팀 예측 성공 여부
        List<Boolean> userPrediction;
        // 평균 예측 성공 & 실패 여부
        // 홈팀 예측 성공 여부 + 원정팀 예측 성공 여부
        List<Boolean> avgPrediction;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    // 우승팀 예측 조회 Response
    public static class ScorePredictionInquireResponse{
        int homeTeamScore; // 나의 홈팀 예상 스코어
        int awayTeamScore;  // 나의 원정팀 예상 스코어
    }
}
