package KickIt.server.domain.lineupPrediction.service;

import KickIt.server.domain.fixture.dto.ResponsePlayerInfo;
import KickIt.server.domain.lineup.service.MatchLineupService;
import KickIt.server.domain.lineupPrediction.dto.LineupPredictionDto;
import KickIt.server.domain.lineup.dto.MatchLineupDto;
import KickIt.server.domain.lineupPrediction.entity.LineupPrediction;
import KickIt.server.domain.lineupPrediction.entity.LineupPredictionRepository;
import KickIt.server.domain.lineupPrediction.entity.PredictionPlayer;
import KickIt.server.domain.teams.entity.Player;
import KickIt.server.domain.teams.entity.SquadRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.net.http.HttpClient;
import java.util.*;

@Service
public class LineupPredictionService {
    @Autowired
    private LineupPredictionRepository lineupPredictionRepository;
    @Autowired
    private SquadRepository squadRepository;
    @Autowired
    private MatchLineupService matchLineupService;

    @Transactional
    public HttpStatus saveLineupPredictions(LineupPrediction lineupPrediction){
        // member id와 fixture id로 중복 검사해서 중복 데이터 존재 -> 저장하지 x.
        if(lineupPredictionRepository.findByMemberAndFixture(lineupPrediction.getMember().getId(), lineupPrediction.getFixture().getId()).isPresent()){
            return HttpStatus.CONFLICT;
        }
        // member id와 fixture id로 중복 검사해서 중복 데이터 없음 -> 새로 저장
        else{
            try{
                lineupPrediction.setLastUpdated();
                lineupPredictionRepository.save(lineupPrediction);
            }
            catch (Exception e){
                return HttpStatus.INTERNAL_SERVER_ERROR;
            }
            finally {
                return HttpStatus.OK;
            }
        }
    }

    public LineupPredictionDto.LineUpPredictionEditResponse editLineupPredictions(LineupPrediction lineupPrediction){
        LineupPrediction currentPrediction = lineupPredictionRepository.findByMemberAndFixture(lineupPrediction.getMember().getId(), lineupPrediction.getFixture().getId()).get();
        Long memberId = lineupPrediction.getMember().getId();
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
    public List<Boolean> isPredictionCorrect(int homeFormation, int awayFormation, LineupPredictionDto.ResponseLineup homeLineup, LineupPredictionDto.ResponseLineup awayLineup, MatchLineupDto.MatchLineupResponse matchLineup){
        List<Boolean> response = new ArrayList<>();

        // 홈팀과 원정팀의 첫 줄 미드 필더와 두 번째 줄 미드필더 미리 합쳐 줌
        // 홈팀 미드필더
        List<ResponsePlayerInfo> homeMidfielders = new ArrayList<>();
        homeMidfielders.addAll(matchLineup.getHomeLineups().getMidfielders());
        if(matchLineup.getHomeLineups().getSecondMidFielders() != null){
            homeMidfielders.addAll(matchLineup.getHomeLineups().getSecondMidFielders());
        }
        // 원정팀 미드필더
        List<ResponsePlayerInfo> awayMidfielders = new ArrayList<>();
        awayMidfielders.addAll(matchLineup.getAwayLineups().getMidfielders());
        if(matchLineup.getAwayLineups().getSecondMidFielders() != null){
            awayMidfielders.addAll(matchLineup.getAwayLineups().getSecondMidFielders());
        }

        // 홈팀 비교 진행
        // 만약 홈팀 포메이션이 다른 경우 false
        if(!convertFormation(homeFormation).equals(matchLineup.getHomeFormation())){
            response.add(false);
        }
        // 홈팀 골키퍼가 다른 경우 false
        else if(!isPosPredictionAllCorrect(matchLineup.getHomeLineups().getGoalkeeper(), Collections.singletonList(homeLineup.getGoalkeeper()))){
            response.add(false);
        }
        // 홈팀 수비수가 다른 경우 false
        else if(!isPosPredictionAllCorrect(matchLineup.getHomeLineups().getDefenders(), homeLineup.getDefenders())){
            response.add(false);
        }
        // 홈팀 미드필더가 다른 경우 false
        else if(!isPosPredictionAllCorrect(homeMidfielders, homeLineup.getMidfielders())){
            response.add(false);
        }
        // 홈팀 공격수가 다른 경우 false
        else if(!isPosPredictionAllCorrect(matchLineup.getHomeLineups().getStrikers(), homeLineup.getStrikers())){
            response.add(false);
        }
        // 전부 맞았다면 true
        else{
            response.add(true);
        }

        // 원정팀 비교 진행
        // 만약 원정팀 포메이션이 다른 경우 false
        if(!convertFormation(awayFormation).equals(matchLineup.getAwayFormation())){
            response.add(false);
        }
        // 원정팀 골키퍼가 다른 경우 false
        else if(!isPosPredictionAllCorrect(matchLineup.getAwayLineups().getGoalkeeper(), Collections.singletonList(awayLineup.getGoalkeeper()))){
            response.add(false);
        }
        // 원정팀 수비수가 다른 경우 false
        else if(!isPosPredictionAllCorrect(matchLineup.getAwayLineups().getDefenders(), awayLineup.getDefenders())){
            response.add(false);
        }
        else if(!isPosPredictionAllCorrect(awayMidfielders, awayLineup.getMidfielders())){
            response.add(false);
        }
        // 원정팀 공격수가 다른 경우 false
        else if(!isPosPredictionAllCorrect(matchLineup.getAwayLineups().getStrikers(), awayLineup.getStrikers())){
            response.add(false);
        }
        // 전부 맞았다면 true
        else{
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
    Boolean isPosPredictionAllCorrect(List<ResponsePlayerInfo> realPosPlayers, List<ResponsePlayerInfo> predictedPostPlayers){
        for(int i = 0; i < realPosPlayers.size(); i++) {
            if (!Objects.equals(realPosPlayers.get(i).getPlayerNum(), predictedPostPlayers.get(i).getPlayerNum())) {
                return false;
            }
            if(realPosPlayers.get(i).getPlayerNum() == null){
                if(!realPosPlayers.get(i).getPlayerName().equals(predictedPostPlayers.get(i).getPlayerName())){
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
        List<ResponsePlayerInfo> defenders = new ArrayList<>();
        for (int i = 0; i < dfSize; i++){
            defenders.add(new ResponsePlayerInfo(lineupPredictionRepository.findAvgHomePlayer(fixtureId, formation, 1, i)));
        }
        List<ResponsePlayerInfo> midfielders = new ArrayList<>();
        for (int i = 0; i < mfSize; i++){
            midfielders.add(new ResponsePlayerInfo(lineupPredictionRepository.findAvgHomePlayer(fixtureId, formation, 2, i)));
        }
        List<ResponsePlayerInfo> strikers = new ArrayList<>();
        for (int i = 0; i < fwSize; i++){
            strikers.add(new ResponsePlayerInfo(lineupPredictionRepository.findAvgHomePlayer(fixtureId, formation, 3, i)));
        }
        LineupPredictionDto.ResponseLineup avgHomeTeamLineup = LineupPredictionDto.ResponseLineup.builder()
                .goalkeeper(new ResponsePlayerInfo(lineupPredictionRepository.findAvgHomePlayer(fixtureId, formation, 0, 0)))
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
        List<ResponsePlayerInfo> defenders = new ArrayList<>();
        for (int i = 0; i < dfSize; i++){
            defenders.add(new ResponsePlayerInfo(lineupPredictionRepository.findAvgAwayPlayer(fixtureId, formation, 1, i)));
        }
        List<ResponsePlayerInfo> midfielders = new ArrayList<>();
        for (int i = 0; i < mfSize; i++){
            midfielders.add(new ResponsePlayerInfo(lineupPredictionRepository.findAvgAwayPlayer(fixtureId, formation, 2, i)));
        }
        List<ResponsePlayerInfo> strikers = new ArrayList<>();
        for (int i = 0; i < fwSize; i++){
            strikers.add(new ResponsePlayerInfo(lineupPredictionRepository.findAvgAwayPlayer(fixtureId, formation, 3, i)));
        }
        LineupPredictionDto.ResponseLineup avgAwayTeamLineup = LineupPredictionDto.ResponseLineup.builder()
                .goalkeeper(new ResponsePlayerInfo(lineupPredictionRepository.findAvgAwayPlayer(fixtureId, formation, 0, 0)))
                .defenders(defenders)
                .midfielders(midfielders)
                .strikers(strikers)
                .build();
        return avgAwayTeamLineup;
    }

    // 사용자  or 평균 예측 선발라인업이 정확한지 실제 선발 라인업과 비교해 결과 반환하는 함수
    public List<ResponsePlayerInfo> convertToPlayerInfo(List<PredictionPlayer> players){
        List<ResponsePlayerInfo> convertedPlayers = new ArrayList<>();
        for (PredictionPlayer player : players){
            convertedPlayers.add(new ResponsePlayerInfo(player.getPlayer()));
        }
        return convertedPlayers;
    }

    // 사용자 선발라인업 예측 조회를 위한 service
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

    // 전체 선발라인업 예측 결과 조회를 위한 service
    public LineupPredictionDto.LineupResultInquireResponse inquireLineupPredictionResult(Long fixtureId, Long memberId){
        // 실제 경기 선발라인업 결과
        MatchLineupDto.MatchLineupResponse foundMatchLineup = matchLineupService.findMatchLineupByFixture(fixtureId);
        // 사용자 예측 경기 선발라인업 결과
        LineupPrediction userPrediction = lineupPredictionRepository.findByMemberAndFixture(memberId, fixtureId).orElse(null);
        // 평균 홈 포메이션 값
        Integer avgHomeFormation = lineupPredictionRepository.findAvgHomeTeamForm(fixtureId);
        // 평균 원정팀 포메이션 값
        Integer avgAwayFormation = lineupPredictionRepository.findAvgAwayTeamForm(fixtureId);
        // 평균 홈팀 선발라인업 값
        LineupPredictionDto.ResponseLineup avgHomePrediction = avgHomeFormation == null? null : findAvgHomeTeamLineup(fixtureId, avgHomeFormation);
        // 평균 원정팀 선발라인업 값
        LineupPredictionDto.ResponseLineup avgAwayPrediction = avgAwayFormation == null? null: findAvgAwayTeamLineup(fixtureId, avgAwayFormation);

        // 선발라인업 예측 결과 조회 API의 결과로 반환할 response
        LineupPredictionDto.LineupResultInquireResponse response;

        // 만약 아직 아무도 예측 진행하지 않은 경우
        if(avgHomeFormation == null || avgAwayFormation == null){
            if(foundMatchLineup == null){
                response = LineupPredictionDto.LineupResultInquireResponse.builder()
                        .participant(lineupPredictionRepository.findByFixture(fixtureId).size())
                        .build();
            }
            else{
                // 실제 홈팀 선발라인업 정보 필요한 형식의 클래스 객체로 만들어 줌
                MatchLineupDto.MatchPosPlayersDto foundHomeLineup = new MatchLineupDto.MatchPosPlayersDto(foundMatchLineup.getHomeLineups().getGoalkeeper(), foundMatchLineup.getHomeLineups().getDefenders(), foundMatchLineup.getHomeLineups().getMidfielders(), foundMatchLineup.getHomeLineups().getSecondMidFielders(), foundMatchLineup.getHomeLineups().getStrikers());

                // 실제 원정팀 선발라인업 정보 필요한 형식의 클래스 객체로 만들어 줌
                MatchLineupDto.MatchPosPlayersDto foundAwayLineup = new MatchLineupDto.MatchPosPlayersDto(foundMatchLineup.getAwayLineups().getGoalkeeper(), foundMatchLineup.getAwayLineups().getDefenders(), foundMatchLineup.getAwayLineups().getMidfielders(),foundMatchLineup.getAwayLineups().getSecondMidFielders(), foundMatchLineup.getAwayLineups().getStrikers());

                response = LineupPredictionDto.LineupResultInquireResponse.builder()
                        .participant(lineupPredictionRepository.findByFixture(fixtureId).size())
                        .homeLineups(foundHomeLineup)
                        .awayLineups(foundAwayLineup)
                        .build();
            }
        }

        // 아직 선발라인업 결과가 나오지 않은 경우 -> 관련 항목 null 처리
        // 만약 사용자 예측 선발 라인업이 없는 경우 -> 관련 항목 null 처리
        // 각각 경우 나눠 수행
        else if(userPrediction == null){
            // 사용자 예측 X / 선발 라인업 결과 X
            if(foundMatchLineup == null){
                response = LineupPredictionDto.LineupResultInquireResponse.builder()
                        .participant(lineupPredictionRepository.findByFixture(fixtureId).size())
                        .avgHomeFormation(avgHomeFormation)
                        .avgHomePrediction(avgHomePrediction)
                        .avgAwayFormation(avgAwayFormation)
                        .avgAwayPrediction(avgAwayPrediction)
                        .build();
            }
            // 사용자 예측 X / 선발 라인업 결과 O
            else{
                // 실제 홈팀 선발라인업 정보 필요한 형식의 클래스 객체로 만들어 줌
                MatchLineupDto.MatchPosPlayersDto foundHomeLineup = new MatchLineupDto.MatchPosPlayersDto(foundMatchLineup.getHomeLineups().getGoalkeeper(), foundMatchLineup.getHomeLineups().getDefenders(), foundMatchLineup.getHomeLineups().getMidfielders(), foundMatchLineup.getHomeLineups().getSecondMidFielders(), foundMatchLineup.getHomeLineups().getStrikers());

                // 실제 원정팀 선발라인업 정보 필요한 형식의 클래스 객체로 만들어 줌
                MatchLineupDto.MatchPosPlayersDto foundAwayLineup = new MatchLineupDto.MatchPosPlayersDto(foundMatchLineup.getAwayLineups().getGoalkeeper(), foundMatchLineup.getAwayLineups().getDefenders(), foundMatchLineup.getAwayLineups().getMidfielders(),foundMatchLineup.getAwayLineups().getSecondMidFielders(), foundMatchLineup.getAwayLineups().getStrikers());

                response = LineupPredictionDto.LineupResultInquireResponse.builder()
                        .participant(lineupPredictionRepository.findByFixture(fixtureId).size())
                        .homeFormation(foundMatchLineup.getHomeFormation())
                        .homeLineups(foundHomeLineup)
                        .awayFormation(foundMatchLineup.getAwayFormation())
                        .awayLineups(foundAwayLineup)
                        .avgHomeFormation(avgHomeFormation)
                        .avgHomePrediction(avgHomePrediction)
                        .avgAwayFormation(avgAwayFormation)
                        .avgAwayPrediction(avgAwayPrediction)
                        .avgPrediction(isPredictionCorrect(avgHomeFormation, avgAwayFormation, avgHomePrediction, avgAwayPrediction, foundMatchLineup))
                        .build();
            }
        }
        else{
            // 사용자가 예측한 홈팀 선발라인업 정보 형식에 맞는 클래스 객체로 다시 생성
            LineupPredictionDto.ResponseLineup userHomeLineup = LineupPredictionDto.ResponseLineup.builder()
                    .goalkeeper(convertToPlayerInfo(lineupPredictionRepository.findFilteredPlayersByMemberAndFixture(memberId, fixtureId, 0, 0)).get(0))
                    .defenders(convertToPlayerInfo(lineupPredictionRepository.findFilteredPlayersByMemberAndFixture(memberId, fixtureId, 1, 0)))
                    .midfielders(convertToPlayerInfo(lineupPredictionRepository.findFilteredPlayersByMemberAndFixture(memberId, fixtureId, 2, 0)))
                    .strikers(convertToPlayerInfo(lineupPredictionRepository.findFilteredPlayersByMemberAndFixture(memberId, fixtureId, 3, 0))).build();

            // 사용자가 예측한 원정팀 선발라인업 정보 형식에 맞는 클래스 객체로 다시 생성
            LineupPredictionDto.ResponseLineup userAwayLineup = LineupPredictionDto.ResponseLineup.builder()
                    .goalkeeper(convertToPlayerInfo(lineupPredictionRepository.findFilteredPlayersByMemberAndFixture(memberId, fixtureId, 0, 1)).get(0))
                    .defenders(convertToPlayerInfo(lineupPredictionRepository.findFilteredPlayersByMemberAndFixture(memberId, fixtureId, 1, 1)))
                    .midfielders(convertToPlayerInfo(lineupPredictionRepository.findFilteredPlayersByMemberAndFixture(memberId, fixtureId, 2, 1)))
                    .strikers(convertToPlayerInfo(lineupPredictionRepository.findFilteredPlayersByMemberAndFixture(memberId, fixtureId, 3, 1))).build();

            // 사용자 예측 O / 선발 라인업 결과 X
            if(foundMatchLineup == null){
                response = LineupPredictionDto.LineupResultInquireResponse.builder()
                        .participant(lineupPredictionRepository.findByFixture(fixtureId).size())
                        .userHomeFormation(userPrediction.getHomeTeamForm())
                        .userHomePrediction(userHomeLineup)
                        .userAwayFormation(userPrediction.getAwayTeamForm())
                        .userAwayPrediction(userAwayLineup)
                        .avgHomeFormation(avgHomeFormation)
                        .avgHomePrediction(avgHomePrediction)
                        .avgAwayFormation(avgAwayFormation)
                        .avgAwayPrediction(avgAwayPrediction)
                        .build();
            }
            // 사용자 예측 O / 선발 라인업 결과 O
            else{
                // 실제 홈팀 선발라인업 정보 필요한 형식의 클래스 객체로 만들어 줌
                MatchLineupDto.MatchPosPlayersDto foundHomeLineup = new MatchLineupDto.MatchPosPlayersDto(foundMatchLineup.getHomeLineups().getGoalkeeper(), foundMatchLineup.getHomeLineups().getDefenders(), foundMatchLineup.getHomeLineups().getMidfielders(), foundMatchLineup.getHomeLineups().getSecondMidFielders(), foundMatchLineup.getHomeLineups().getStrikers());

                // 실제 원정팀 선발라인업 정보 필요한 형식의 클래스 객체로 만들어 줌
                MatchLineupDto.MatchPosPlayersDto foundAwayLineup = new MatchLineupDto.MatchPosPlayersDto(foundMatchLineup.getAwayLineups().getGoalkeeper(), foundMatchLineup.getAwayLineups().getDefenders(), foundMatchLineup.getAwayLineups().getMidfielders(),foundMatchLineup.getAwayLineups().getSecondMidFielders(), foundMatchLineup.getAwayLineups().getStrikers());

                response = LineupPredictionDto.LineupResultInquireResponse.builder()
                        .participant(lineupPredictionRepository.findByFixture(fixtureId).size())
                        .homeFormation(foundMatchLineup.getHomeFormation())
                        .homeLineups(foundHomeLineup)
                        .awayFormation(foundMatchLineup.getAwayFormation())
                        .awayLineups(foundAwayLineup)
                        .userHomeFormation(userPrediction.getHomeTeamForm())
                        .userHomePrediction(userHomeLineup)
                        .userAwayFormation(userPrediction.getAwayTeamForm())
                        .userAwayPrediction(userAwayLineup)
                        .avgHomeFormation(avgHomeFormation)
                        .avgHomePrediction(avgHomePrediction)
                        .avgAwayFormation(avgAwayFormation)
                        .avgAwayPrediction(avgAwayPrediction)
                        .userPrediction(isPredictionCorrect(userPrediction.getHomeTeamForm(), userPrediction.getAwayTeamForm(), userHomeLineup, userAwayLineup, foundMatchLineup))
                        .avgPrediction(isPredictionCorrect(avgHomeFormation, avgAwayFormation, avgHomePrediction, avgAwayPrediction, foundMatchLineup))
                        .build();
            }
        }
        return response;
    }
}
