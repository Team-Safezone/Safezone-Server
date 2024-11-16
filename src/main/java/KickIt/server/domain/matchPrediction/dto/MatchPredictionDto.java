package KickIt.server.domain.matchPrediction.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public class MatchPredictionDto {
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    // 경기 예측 조회 Response
    public static class MatchPredictionInquireResponse{
        InquiredScorePrediction scorePredictions; // 우승팀 예측 데이터
        InquiredLineupPrediction lineupPredictions; // 선발라인업 예측 데이터
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    // 경기 예측 조회 Response 구성 요소 - 선발라인업 예측
    public static class InquiredLineupPrediction{
        int homePercentage; // 홈팀의 1순위 포메이션 예측 비율(백분율)
        int awayPercentage; // 원정팀의 1순위 포메이션 예측 비율(백분율)
        int homeFormation; // 홈팀의 1순위 예상 포메이션
        int awayFormation; // 원정팀의 1순위 예상 포메이션
        Boolean isParticipated; // 사용자가 우승팀 예측을 했는지 여부
        int participant; // 예측에 참여한 사람의 수
        Boolean isPredictionSuccessful; // 사용자가 예측을 성공했는지 여부 -> 예측 종료 시에만 전송
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    // 경기 예측 조회 Response 구성 요소 - 우승팀 예측
    public static class InquiredScorePrediction{
        int homePercentage; // 홈팀의 예측 우승 확률(백분율)
        Boolean isParticipated; // 사용자가 우승팀 예측을 했는지 여부
        int participant; // 예측에 참여한 사람의 수
        Boolean isPredictionSuccessful; // 사용자가 예측 성공했는지 여부 -> 예측 종료 시에만 전송
    }
}
