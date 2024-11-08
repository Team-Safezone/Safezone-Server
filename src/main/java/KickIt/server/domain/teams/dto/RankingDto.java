package KickIt.server.domain.teams.dto;

import KickIt.server.domain.teams.entity.Ranking;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

public class RankingDto {
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class RankingResponse{
        List<RankInfo> rankings;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class RankInfo{
        int ranking; // 순위
        String teamUrl; // 팀 엠블럼 주소
        String teamName; // 팀 명칭
        int totalMatches; // 각 팀이 치른 경기 횟수
        int wins; // 각 팀의 승리 횟수
        int draws; // 각 팀의 무승부 횟수
        int losses; // 각 팀의 패배 횟수
        int points; // 각 팀이 획득한 승점;
        // 리그 카테고리
        // 0: 디폴트
        // 1: 챔피언스리그 O
        // 2: 유로파리그 O
        // 3: 강등권 O
        int leagueCategory;
    }
}
