package KickIt.server.domain.home.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

public class HomeDto {
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    // 홈 화면 조회 Response
    public static class homeResponse{
        int gradePoint; // 사용자가 획득한 총 포인트 -> required
        homeMatchPredictionInfo matchPredictions; // 우승팀 예측 버튼 클릭 시 사용할 리스트
        homeDiaryInfo matchDiarys; // 축구 일기 쓰기 버튼 클릭 시 사용할 리스트
        List<String> favoriteImagesURL;// 사용자의 관심 있는 구단 이미지 URL 리스트 -> required
        List<homeMatchInfo> matches; // 사용자에게 관심 있을 경기 일정 리스트
    }

    @Data
    @Builder
    // 홈 화면 조회 시 보여지는 우승팀 예측 data class
    public static class homeMatchPredictionInfo{
        Long id;
        String matchDate;
        String matchTime;
        String homeTeamName;
        String awayTeamName;
        String homeTeamEmblemURL;
        String awayTeamEmblemURL;
    }

    @Data
    @Builder
    // 홈 화면 조회 시 보여지는 축구 일기 data class
    public static class homeDiaryInfo{
        Long diaryId;
        String matchDate;
        String matchTime;
        String homeTeamName;
        String awayTeamName;
        String homeTeamEmblemURL;
        String awayTeamEmblemURL;
    }

    @Data
    @Builder
    // 홈 화면 조회 시 보여지는 경기 일정 data class
    public static class homeMatchInfo{
        Long id;
        String homeTeamEmblemURL;
        String awayTeamEmblemURL;
        String homeTeamName;
        String awayTeamName;
        String matchDate;
        String matchTime;
        Integer homeTeamScore;
        Integer awayTeamScore;
        int round;
        int status;
        String stadium;
    }
}
