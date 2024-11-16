package KickIt.server.domain.teams.service;

import KickIt.server.domain.teams.dto.RankingDto;
import KickIt.server.domain.teams.entity.Ranking;
import KickIt.server.domain.teams.entity.RankingRepository;
import KickIt.server.global.common.crawler.RankingCrawler;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class RankingService {
    @Autowired
    RankingRepository rankingRepository;
    @Autowired
    RankingCrawler rankingCrawler;
    @Autowired
    TeamNameConvertService teamNameConvertService;

    @Transactional
    // 현재 시즌 랭킹 조회
    public RankingDto.RankingResponse inquireRanking(){
        Ranking newestRank = rankingRepository.findNewestRank().orElse(null);
        // 아직 랭킹 데이터 없음 => 크롤링
        if(newestRank == null){
            // 크롤링해 온 랭킹 정보
            List<Ranking> rankingList = saveRankingCrawl();
            if(rankingList == null){ return null; } // null인 경우 변환 절차 없이 반환

            // 가져온 랭킹 정보 dto class로 변환해 반환
            List<RankingDto.RankInfo> rankings = new ArrayList<>();
            for(Ranking ranking : rankingList){
                rankings.add(RankingDto.RankInfo.builder()
                        .ranking(ranking.getTeamRank())
                        .teamUrl(ranking.getSquad().getLogoImg())
                        .teamName(teamNameConvertService.convertToKrName(ranking.getSquad().getTeam()))
                        .totalMatches(ranking.getMatchCount())
                        .wins(ranking.getWinCount())
                        .draws(ranking.getDrawCount())
                        .losses(ranking.getLoseCount())
                        .points(ranking.getPoints())
                        .leagueCategory(findLeagueCategory(ranking.getTeamRank()))
                        .build());
            }
            RankingDto.RankingResponse response = RankingDto.RankingResponse.builder()
                    .rankings(rankings).build();
            return response;
        }
        // 가장 최근 데이터 1 개와 현재 시간 비교
        long duration = Duration.between(ZonedDateTime.ofInstant(rankingRepository.findNewestRank().get().getLastUpdated().atZone(ZoneId.of("Asia/Seoul")).toInstant(), ZoneId.of("Asia/Seoul")), ZonedDateTime.ofInstant(LocalDateTime.now().atZone(ZoneId.of("Asia/Seoul")).toInstant(), ZoneId.of("Asia/Seoul"))).toMinutes();
        Logger.getGlobal().log(Level.INFO, String.format("현재 시간: %s, 업데이트 시간: %s, 차이: %s", ZonedDateTime.ofInstant(LocalDateTime.now().atZone(ZoneId.of("Asia/Seoul")).toInstant(), ZoneId.of("Asia/Seoul")), ZonedDateTime.ofInstant(rankingRepository.findNewestRank().get().getLastUpdated().atZone(ZoneId.of("Asia/Seoul")).toInstant(), ZoneId.of("Asia/Seoul")), duration));
        // 가장 최근에 저장된 데이터가 1분 이상 차이 나지 않으면 DB에 있는 값 그대로 반환
        if (duration < 1){
            // 가장 최근 시즌 정보
            String season = newestRank.getSquad().getSeason();
            // 해당 시즌 정보 가져옴
            List<Ranking> rankingList = rankingRepository.findBySeason(season);
            Logger.getGlobal().log(Level.INFO, rankingList.size() + "");
            // dto class 대로 변환해 반환
            List<RankingDto.RankInfo> rankings = new ArrayList<>();
            for(Ranking ranking : rankingList){
                rankings.add(RankingDto.RankInfo.builder()
                        .ranking(ranking.getTeamRank())
                        .teamUrl(ranking.getSquad().getLogoImg())
                        .teamName(teamNameConvertService.convertToKrName(ranking.getSquad().getTeam()))
                        .totalMatches(ranking.getMatchCount())
                        .wins(ranking.getWinCount())
                        .draws(ranking.getDrawCount())
                        .losses(ranking.getLoseCount())
                        .points(ranking.getPoints())
                        .leagueCategory(findLeagueCategory(ranking.getTeamRank()))
                        .build());
            }
            RankingDto.RankingResponse response = RankingDto.RankingResponse.builder()
                    .rankings(rankings).build();
            return response;
        }
        // 가자 최근에 저장된 데이터가 1분 이상 차이 나면 크롤링해 DB에 업데이트 후 반환
        else{
            // 크롤링해 온 랭킹 정보
            List<Ranking> rankingList = saveRankingCrawl();
            if(rankingList == null){ return null; } // null인 경우 변환 절차 없이 반환

            // 가져온 랭킹 정보 dto class로 변환해 반환
            List<RankingDto.RankInfo> rankings = new ArrayList<>();
            for(Ranking ranking : rankingList){
                rankings.add(RankingDto.RankInfo.builder()
                        .ranking(ranking.getTeamRank())
                        .teamUrl(ranking.getSquad().getLogoImg())
                        .teamName(teamNameConvertService.convertToKrName(ranking.getSquad().getTeam()))
                        .totalMatches(ranking.getMatchCount())
                        .wins(ranking.getWinCount())
                        .draws(ranking.getDrawCount())
                        .losses(ranking.getLoseCount())
                        .points(ranking.getPoints())
                        .leagueCategory(findLeagueCategory(ranking.getTeamRank()))
                        .build());
            }
            RankingDto.RankingResponse response = RankingDto.RankingResponse.builder()
                    .rankings(rankings).build();
            return response;
        }
    }

    // 현재 랭킹 데이터 크롤링해 DB에 저장
    @Transactional
    public List<Ranking> saveRankingCrawl(){
        List<Ranking> rankingList = rankingCrawler.getRanking();
        if(rankingList == null){
            return null;
        }
        // 전체 데이터 삭제 후 저장
        rankingRepository.deleteAll();

        for(Ranking ranking : rankingList){
            rankingRepository.save(ranking);
        }
        return rankingList;
    }

    public int findLeagueCategory(int rank){
        return switch (rank){
            case 1, 2, 3, 4 -> 1;
            case 5 -> 2;
            case 18, 19, 20 -> 3;
            default -> 0;
        };
    }
}
