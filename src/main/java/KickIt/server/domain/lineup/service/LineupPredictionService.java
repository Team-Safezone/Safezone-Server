package KickIt.server.domain.lineup.service;

import KickIt.server.domain.lineup.dto.LineupPredictionDto;
import KickIt.server.domain.lineup.dto.MatchLineupDto;
import KickIt.server.domain.lineup.entity.*;
import KickIt.server.domain.teams.entity.Player;
import KickIt.server.domain.teams.entity.SquadRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.sound.sampled.Line;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class LineupPredictionService {
    @Autowired
    private LineupPredictionRepository lineupPredictionRepository;
    @Autowired
    private SquadRepository squadRepository;
    @Autowired
    private MatchLineupService matchLineupService;

    @Transactional
    public void saveLineupPredictions(LineupPrediction lineupPrediction){
        // member id와 fixture id로 중복 검사해서 중복 데이터 존재 -> 저장하지 x.
        if(lineupPredictionRepository.findByMemberAndFixture(lineupPrediction.getMember().getMemberId(), lineupPrediction.getFixture().getId()).isPresent()){
            /*
            LineupPrediction currentPrediction = lineupPredictionRepository.findByMemberAndFixture(lineupPrediction.getMember().getMemberId(), lineupPrediction.getFixture().getId()).get();
            currentPrediction.getPlayers().clear();
            currentPrediction.getPlayers().addAll(lineupPrediction.getPlayers());
            lineupPredictionRepository.save(currentPrediction);
             */
        }
        // member id와 fixture id로 중복 검사해서 중복 데이터 없음 -> 새로 저장
        else{
            lineupPrediction.setLastUpdated();
            lineupPredictionRepository.save(lineupPrediction);
        }
    }

    public LineupPredictionDto.LineUpPredictionEditResponse editLineupPredictions(LineupPrediction lineupPrediction){
        LineupPrediction currentPrediction = lineupPredictionRepository.findByMemberAndFixture(lineupPrediction.getMember().getMemberId(), lineupPrediction.getFixture().getId()).get();
        Long memberId = lineupPrediction.getMember().getMemberId();
        Long fixtureId = lineupPrediction.getFixture().getId();

        // 이후 기존 정보 반환할 수 있게 정보 수정 전 저장
        // 반환하는 평균 통계는 현재의 수정 사항 합산한 것이어야 하기 때문에 필요한 과정 (먼저 수정 후 저장 -> response 생성)
        int originalHomeFormation = currentPrediction.getHomeTeamForm();
        int originalAwayFormation = currentPrediction.getAwayTeamForm();

        // 기존에 예측한 홈팀 라인업 정보 DB에서 가져와 구성
        LineupPredictionDto.ResponseLineup currentHomePrediction = LineupPredictionDto.ResponseLineup.builder()
                .goalkeeper(convertToPlayerInfo(lineupPredictionRepository.findFilteredPlayersByMemberAndFixture(memberId, fixtureId, 0, 0)).get(0))
                .defenders(convertToPlayerInfo(lineupPredictionRepository.findFilteredPlayersByMemberAndFixture(memberId, fixtureId, 1, 0)))
                .midfielders(convertToPlayerInfo(lineupPredictionRepository.findFilteredPlayersByMemberAndFixture(memberId, fixtureId, 2, 0)))
                .strikers(convertToPlayerInfo(lineupPredictionRepository.findFilteredPlayersByMemberAndFixture(memberId, fixtureId, 3, 0)))
                .build();

        // 기존에 예측한 원정팀 라인업 정보 DB에서 가져와 구성
        LineupPredictionDto.ResponseLineup currentAwayPrediction = LineupPredictionDto.ResponseLineup.builder()
                .goalkeeper(convertToPlayerInfo(lineupPredictionRepository.findFilteredPlayersByMemberAndFixture(memberId, fixtureId, 0, 1)).get(0))
                .defenders(convertToPlayerInfo(lineupPredictionRepository.findFilteredPlayersByMemberAndFixture(memberId, fixtureId, 1, 1)))
                .midfielders(convertToPlayerInfo(lineupPredictionRepository.findFilteredPlayersByMemberAndFixture(memberId, fixtureId, 2, 1)))
                .strikers(convertToPlayerInfo(lineupPredictionRepository.findFilteredPlayersByMemberAndFixture(memberId, fixtureId, 3, 1)))
                .build();

        currentPrediction.getPlayers().clear();
        lineupPredictionRepository.save(currentPrediction);

        LineupPrediction finalPrediction = LineupPrediction.builder()
                .member(currentPrediction.getMember())
                .fixture(currentPrediction.getFixture())
                .homeTeamForm(lineupPrediction.getHomeTeamForm())
                .awayTeamForm(lineupPrediction.getAwayTeamForm())
                .players(new ArrayList<>())
                .build();

        finalPrediction.getPlayers().addAll(lineupPrediction.getPlayers());
        lineupPrediction.setLastUpdated();
        lineupPredictionRepository.save(finalPrediction);

        // 평균 홈 포메이션 값
        Integer avgHomeFormation = lineupPredictionRepository.findAvgHomeTeamForm(fixtureId);
        // 평균 원정팀 포메이션 값
        Integer avgAwayFormation = lineupPredictionRepository.findAvgAwayTeamForm(fixtureId);
        // 평균 홈팀 선발라인업 값
        LineupPredictionDto.ResponseLineup avgHomePrediction = findAvgHomeTeamLineup(fixtureId, avgHomeFormation);
        // 평균 원정팀 선발라인업 값
        LineupPredictionDto.ResponseLineup avgAwayPrediction = findAvgAwayTeamLineup(fixtureId, avgAwayFormation);
        // 실제 경기 선발라인업 결과
        MatchLineupDto.MatchLineupResponse foundMatchLineup = matchLineupService.findMatchLineupByFixture(fixtureId);

        LineupPredictionDto.LineUpPredictionEditResponse response = LineupPredictionDto.LineUpPredictionEditResponse.builder()
                .participant(lineupPredictionRepository.findByFixture(fixtureId).size())
                .userHomeFormation(originalHomeFormation)
                .userHomePrediction(currentHomePrediction)
                .userAwayFormation(originalAwayFormation)
                .userAwayPrediction(currentAwayPrediction)
                .avgHomeFormation(avgHomeFormation)
                .avgAwayFormation(avgAwayFormation)
                .avgHomePrediction(avgHomePrediction)
                .avgAwayPrediction(avgAwayPrediction)
                .userPrediction(foundMatchLineup == null? null : isPredictionCorrect(finalPrediction.getHomeTeamForm(), finalPrediction.getAwayTeamForm(), currentHomePrediction, currentAwayPrediction, foundMatchLineup))
                .avgPrediction(foundMatchLineup == null? null : isPredictionCorrect(avgHomeFormation, avgAwayFormation, avgHomePrediction, avgAwayPrediction, foundMatchLineup))
                .build();

        return response;
    }

    // 사용자와 평균 선발라인업 예측이 정확한지 확인 후 반환
    // 이미 선발 라인업 결과 존재할 때만 호출됨
    List<Boolean> isPredictionCorrect(int homeFormation, int awayFormation, LineupPredictionDto.ResponseLineup homeLineup, LineupPredictionDto.ResponseLineup awayLineup, MatchLineupDto.MatchLineupResponse matchLineup){
        List<Boolean> response = new ArrayList<>();

        // 홈팀과 원정팀의 첫 줄 미드 필더와 두 번째 줄 미드필더 미리 합쳐 줌
        // 홈팀 미드필더
        List<MatchLineupDto.MatchPlayerDto> homeMidfielders = new ArrayList<>();
        homeMidfielders.addAll(matchLineup.getHomeLineups().getMidfielders());
        if(matchLineup.getHomeLineups().getSecondMidFielders() != null){
            homeMidfielders.addAll(matchLineup.getHomeLineups().getSecondMidFielders());
        }
        // 원정팀 미드필더
        List<MatchLineupDto.MatchPlayerDto> awayMidfielders = new ArrayList<>();
        awayMidfielders.addAll(matchLineup.getAwayLineups().getMidfielders());
        if(matchLineup.getAwayLineups().getSecondMidFielders() != null){
            awayMidfielders.addAll(matchLineup.getAwayLineups().getSecondMidFielders());
        }

        // 홈팀 비교 진행
        // 만약 홈팀 포메이션이 다른 경우 false
        if(!convertFormation(homeFormation).equals(matchLineup.getHomeFormation())){
            Logger.getGlobal().log(Level.INFO, "홈팀 포메이션 다름");
            response.add(false);
        }
        // 홈팀 골키퍼가 다른 경우 false
        else if(!isPosPredictionAllCorrect(matchLineup.getHomeLineups().getGoalkeeper(), Collections.singletonList(homeLineup.getGoalkeeper()))){
            Logger.getGlobal().log(Level.INFO, "홈팀 골키퍼 다름");
            response.add(false);
        }
        // 홈팀 수비수가 다른 경우 false
        else if(!isPosPredictionAllCorrect(matchLineup.getHomeLineups().getDefenders(), homeLineup.getDefenders())){
            Logger.getGlobal().log(Level.INFO, "홈팀 수비수 다름");
            response.add(false);
        }
        // 홈팀 미드필더가 다른 경우 false
        else if(!isPosPredictionAllCorrect(homeMidfielders, homeLineup.getMidfielders())){
            Logger.getGlobal().log(Level.INFO, "홈팀 미드필더 다름");
            response.add(false);
        }
        // 홈팀 공격수가 다른 경우 false
        else if(!isPosPredictionAllCorrect(matchLineup.getHomeLineups().getStrikers(), homeLineup.getStrikers())){
            Logger.getGlobal().log(Level.INFO, "홈팀 공격수 다름");
            response.add(false);
        }
        // 전부 맞았다면 true
        else{
            Logger.getGlobal().log(Level.INFO, "홈팀 true");
            response.add(true);
        }

        // 원정팀 비교 진행
        // 만약 원정팀 포메이션이 다른 경우 false
        if(!convertFormation(awayFormation).equals(matchLineup.getAwayFormation())){
            Logger.getGlobal().log(Level.INFO, "원정팀 포메이션 다름");
            response.add(false);
        }
        // 원정팀 골키퍼가 다른 경우 false
        else if(!isPosPredictionAllCorrect(matchLineup.getAwayLineups().getGoalkeeper(), Collections.singletonList(awayLineup.getGoalkeeper()))){
            Logger.getGlobal().log(Level.INFO, "원정팀 골키퍼 다름");
            response.add(false);
        }
        // 원정팀 수비수가 다른 경우 false
        else if(!isPosPredictionAllCorrect(matchLineup.getAwayLineups().getDefenders(), awayLineup.getDefenders())){
            Logger.getGlobal().log(Level.INFO, "원정팀 수비수 다름");
            response.add(false);
        }
        else if(!isPosPredictionAllCorrect(awayMidfielders, awayLineup.getMidfielders())){
            Logger.getGlobal().log(Level.INFO, "원정팀 미드필더 다름");
            response.add(false);
        }
        // 원정팀 공격수가 다른 경우 false
        else if(!isPosPredictionAllCorrect(matchLineup.getAwayLineups().getStrikers(), awayLineup.getStrikers())){
            Logger.getGlobal().log(Level.INFO, "원정팀 공격수 다름");
            response.add(false);
        }
        // 전부 맞았다면 true
        else{
            Logger.getGlobal().log(Level.INFO, "원정팀 true");
            response.add(true);
        }
         return response;
    }

    // 선발 라인업 0-5 번 x-x-x-x 형태로 변환하는 메소드
    String convertFormation(int formation){
        return switch (formation){
            case 0 -> "4-3-3";
            case 1 -> "4-2-3-1";
            case 2 -> "4-4-2";
            case 3 -> "3-4-3";
            case 4 -> "4-5-1";
            case 5 -> "3-5-2";
            default -> "";
        };
    }

    // MatchLineupDTO.MatchLineupResponse의 Formation의 각 포지션 선수 정보와 LineupPredictionDTO.ResponseLineup의 각 포지션 선수 정보 일치하는지 비교
    // 예상 라인업과 실제 라인업의 각 선수 비교하는 작업 반복할 때 호출
    Boolean isPosPredictionAllCorrect(List<MatchLineupDto.MatchPlayerDto> realPosPlayers, List<LineupPredictionDto.ResponsePlayerInfo> predictedPostPlayers){
        for(int i = 0; i < realPosPlayers.size(); i++) {
            if (!Objects.equals(realPosPlayers.get(i).getPlayerNum(), predictedPostPlayers.get(i).getPlayerNum())) {
                Logger.getGlobal().log(Level.INFO, String.format("번호 다름 %s %s", realPosPlayers.get(i).getPlayerNum(), predictedPostPlayers.get(i).getPlayerNum()));
                return false;
            }
            if(realPosPlayers.get(i).getPlayerNum() == null){
                if(!realPosPlayers.get(i).getPlayerName().equals(predictedPostPlayers.get(i).getPlayerName())){
                    Logger.getGlobal().log(Level.INFO, String.format("이름 다름 %s %s", realPosPlayers.get(i).getPlayerName(), predictedPostPlayers.get(i).getPlayerName()));
                    return false;
                }
            }
        }
        return true;
    }

    // 모든 사용자 통계 가져와 가장 많이 예측한 홈팀 평균 선발 라인업 정보 DB에서 가져와 구성
    LineupPredictionDto.ResponseLineup findAvgHomeTeamLineup(Long fixtureId, Integer formation){
        // 포메이션에 따른 각각 포지션의 선수 수 배정
        int dfSize, mfSize, fwSize;
        switch (formation){
            case 0:
                dfSize = 4; mfSize = 3; fwSize = 3;
                break;
            case 1, 4:
                dfSize = 4; mfSize = 5; fwSize = 1;
                break;
            case 2:
                dfSize = 4; mfSize = 4; fwSize = 2;
                break;
            case 3:
                dfSize = 3; mfSize = 4; fwSize = 3;
                break;
            case 5:
                dfSize = 3; mfSize = 5; fwSize = 2;
                break;
            default:
                dfSize = 0; mfSize = 0; fwSize = 0;
        }
        List<LineupPredictionDto.ResponsePlayerInfo> defenders = new ArrayList<>();
        for (int i = 0; i < dfSize; i++){
            defenders.add(new LineupPredictionDto.ResponsePlayerInfo(lineupPredictionRepository.findAvgHomePlayer(fixtureId, formation, 1, i)));
        }
        List<LineupPredictionDto.ResponsePlayerInfo> midfielders = new ArrayList<>();
        for (int i = 0; i < mfSize; i++){
            midfielders.add(new LineupPredictionDto.ResponsePlayerInfo(lineupPredictionRepository.findAvgHomePlayer(fixtureId, formation, 2, i)));
        }
        List<LineupPredictionDto.ResponsePlayerInfo> strikers = new ArrayList<>();
        for (int i = 0; i < fwSize; i++){
            strikers.add(new LineupPredictionDto.ResponsePlayerInfo(lineupPredictionRepository.findAvgHomePlayer(fixtureId, formation, 3, i)));
        }
        LineupPredictionDto.ResponseLineup avgHomeTeamLineup = LineupPredictionDto.ResponseLineup.builder()
                .goalkeeper(new LineupPredictionDto.ResponsePlayerInfo(lineupPredictionRepository.findAvgHomePlayer(fixtureId, formation, 0, 0)))
                .defenders(defenders)
                .midfielders(midfielders)
                .strikers(strikers)
                .build();
        return avgHomeTeamLineup;
    }

    // 모든 사용자 통계 가져와 가장 많이 예측한 원정팀 평균 선발 라인업 정보 DB에서 가져와 구성
    LineupPredictionDto.ResponseLineup findAvgAwayTeamLineup(Long fixtureId, Integer formation){
        // 포메이션에 따른 각각 포지션의 선수 수 배정
        int dfSize, mfSize, fwSize;
        switch (formation){
            case 0:
                dfSize = 4; mfSize = 3; fwSize = 3;
                break;
            case 1, 4:
                dfSize = 4; mfSize = 5; fwSize = 1;
                break;
            case 2:
                dfSize = 4; mfSize = 4; fwSize = 2;
                break;
            case 3:
                dfSize = 3; mfSize = 4; fwSize = 3;
                break;
            case 5:
                dfSize = 3; mfSize = 5; fwSize = 2;
                break;
            default:
                dfSize = 0; mfSize = 0; fwSize = 0;
        }
        List<LineupPredictionDto.ResponsePlayerInfo> defenders = new ArrayList<>();
        for (int i = 0; i < dfSize; i++){
            defenders.add(new LineupPredictionDto.ResponsePlayerInfo(lineupPredictionRepository.findAvgAwayPlayer(fixtureId, formation, 1, i)));
        }
        List<LineupPredictionDto.ResponsePlayerInfo> midfielders = new ArrayList<>();
        for (int i = 0; i < mfSize; i++){
            midfielders.add(new LineupPredictionDto.ResponsePlayerInfo(lineupPredictionRepository.findAvgAwayPlayer(fixtureId, formation, 2, i)));
        }
        List<LineupPredictionDto.ResponsePlayerInfo> strikers = new ArrayList<>();
        for (int i = 0; i < fwSize; i++){
            strikers.add(new LineupPredictionDto.ResponsePlayerInfo(lineupPredictionRepository.findAvgAwayPlayer(fixtureId, formation, 3, i)));
        }
        LineupPredictionDto.ResponseLineup avgAwayTeamLineup = LineupPredictionDto.ResponseLineup.builder()
                .goalkeeper(new LineupPredictionDto.ResponsePlayerInfo(lineupPredictionRepository.findAvgAwayPlayer(fixtureId, formation, 0, 0)))
                .defenders(defenders)
                .midfielders(midfielders)
                .strikers(strikers)
                .build();
        return avgAwayTeamLineup;
    }

    // 사용자  or 평균 예측 선발라인업이 정확한지 실제 선발 라인업과 비교해 결과 반환하는 함수

    private List<LineupPredictionDto.ResponsePlayerInfo> convertToPlayerInfo(List<PredictionPlayer> players){
        List<LineupPredictionDto.ResponsePlayerInfo> convertedPlayers = new ArrayList<>();
        for (PredictionPlayer player : players){
            convertedPlayers.add(new LineupPredictionDto.ResponsePlayerInfo(player.getPlayer()));
        }
        return convertedPlayers;
    }

    public LineupPredictionDto.LineupInquireResponse inquireLineupPrediction(Long fixtureId, Long memberId, String homeTeam, String awayTeam, String season){
        LineupPrediction lineupPrediction = lineupPredictionRepository.findByMemberAndFixture(memberId, fixtureId).orElse(null);

        // memberId와 fixtureId로 조회한 선발 라인업 예측 data 존재하지 않는 경우
        // null 반환 후 이후 controller 상에서 예외 처리
        if(lineupPrediction == null){
            return null;
        }

        // memberId와 fixtureId로 조회한 선발 라인업 예측 data 존재하는 경우

        // 시즌, 팀으로 찾아온 홈팀 / 원정팀 전체 선수 명단
        List<Player> homePlayers = squadRepository.findBySeasonAndTeam(season, homeTeam).get().getPlayers();
        List<Player> awayPlayers = squadRepository.findBySeasonAndTeam(season, awayTeam).get().getPlayers();

        // API 명세서에 기재된 형식으로 홈팀 선수 정보 바꾸어 리스트 생성
        List<LineupPredictionDto.ResponsePlayerInfo2> homePlayerInfos = new ArrayList<>();
        for(Player player : homePlayers){
            homePlayerInfos.add(new LineupPredictionDto.ResponsePlayerInfo2(player));
        }
        // API 명세서에 기재된 형식으로 원정팀 선수 정보 바꾸어 리스트 생성
        List<LineupPredictionDto.ResponsePlayerInfo2> awayPlayerInfos = new ArrayList<>();
        for(Player player : awayPlayers){
            awayPlayerInfos.add(new LineupPredictionDto.ResponsePlayerInfo2(player));
        }

        // 사용자가 예측한 기존 홈팀 선발 라인업 정보
        LineupPredictionDto.InquiredLineupPrediction homeLineup = LineupPredictionDto.InquiredLineupPrediction.builder()
                .formation(lineupPrediction.getHomeTeamForm())
                .goalkeeper(convertToPlayerInfo(lineupPredictionRepository.findFilteredPlayersByMemberAndFixture(memberId, fixtureId, 0, 0)).get(0))
                .defenders(convertToPlayerInfo(lineupPredictionRepository.findFilteredPlayersByMemberAndFixture(memberId, fixtureId, 1, 0)))
                .midfielders(convertToPlayerInfo(lineupPredictionRepository.findFilteredPlayersByMemberAndFixture(memberId, fixtureId, 2, 0)))
                .strikers(convertToPlayerInfo(lineupPredictionRepository.findFilteredPlayersByMemberAndFixture(memberId, fixtureId, 3, 0)))
                .build();

        // 사용자가 예측한 기존 원정팀 선발 라인업 정보
        LineupPredictionDto.InquiredLineupPrediction awayLineup = LineupPredictionDto.InquiredLineupPrediction.builder()
                .formation(lineupPrediction.getAwayTeamForm())
                .goalkeeper(convertToPlayerInfo(lineupPredictionRepository.findFilteredPlayersByMemberAndFixture(memberId, fixtureId, 0, 1)).get(0))
                .defenders(convertToPlayerInfo(lineupPredictionRepository.findFilteredPlayersByMemberAndFixture(memberId, fixtureId, 1, 1)))
                .midfielders(convertToPlayerInfo(lineupPredictionRepository.findFilteredPlayersByMemberAndFixture(memberId, fixtureId, 2, 1)))
                .strikers(convertToPlayerInfo(lineupPredictionRepository.findFilteredPlayersByMemberAndFixture(memberId, fixtureId, 3, 1)))
                .build();

        // 홈팀 / 원정팀 선수 명단과 사용자가 예측한 선발 라인업 데이터 response로 build
        LineupPredictionDto.LineupInquireResponse response = LineupPredictionDto.LineupInquireResponse.builder()
                .homePlayers(homePlayerInfos)
                .awayPlayers(awayPlayerInfos)
                .homePrediction(homeLineup)
                .awayPrediction(awayLineup)
                .build();
        return response;
    }

    // 현재 DB에서 전체 사용자 선발 라인업 예측 평균 데이터 조회
}
