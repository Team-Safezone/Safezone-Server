package KickIt.server.domain.matchPrediction.service;

import KickIt.server.domain.fixture.entity.Fixture;
import KickIt.server.domain.lineup.dto.MatchLineupDto;
import KickIt.server.domain.lineup.service.MatchLineupService;
import KickIt.server.domain.lineupPrediction.dto.LineupPredictionDto;
import KickIt.server.domain.lineupPrediction.entity.LineupPrediction;
import KickIt.server.domain.lineupPrediction.entity.LineupPredictionRepository;
import KickIt.server.domain.lineupPrediction.service.LineupPredictionService;
import KickIt.server.domain.matchPrediction.dto.MatchPredictionDto;
import KickIt.server.domain.member.entity.Member;
import KickIt.server.domain.scorePrediction.entity.ScorePrediction;
import KickIt.server.domain.scorePrediction.entity.ScorePredictionRepository;
import KickIt.server.domain.scorePrediction.service.ScorePredictionService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class MatchPredictionService {
    @Autowired
    ScorePredictionRepository scorePredictionRepository;
    @Autowired
    ScorePredictionService scorePredictionService;
    @Autowired
    LineupPredictionRepository lineupPredictionRepository;
    @Autowired
    LineupPredictionService lineupPredictionService;
    @Autowired
    MatchLineupService matchLineupService;

    @Transactional
    // 경기 예측 데이터 조회
    public MatchPredictionDto.MatchPredictionInquireResponse inquireMatchPrediction(Fixture fixture, Member member){
        /* 우승팀 예측 관련 데이터 조회 */
        // response에 들어갈 우승팀 예측 데이터
        MatchPredictionDto.InquiredScorePrediction scorePrediction;
        // 기존에 사용자가 진행한 우승팀 예측 데이터 (없는 경우 null)
        ScorePrediction userScorePrediction = scorePredictionRepository.findByFixtureAndMember(fixture.getId(), member.getId()).orElse(null);
        // 우승팀 예측 참여 사용자
        int scoreParticipant = scorePredictionRepository.findByFixture(fixture.getId()).size();

        // 우승팀 예측 종료 전인 경우 (경기 시작 전) -> isPredictionSuccessful 전송 X
        if(ZonedDateTime.ofInstant(LocalDateTime.now().atZone(ZoneId.of("Asia/Seoul")).toInstant(), ZoneId.of("Asia/Seoul")).isBefore(ZonedDateTime.ofInstant(fixture.getDate().toInstant(), ZoneId.of("Asia/Seoul")))){
            // 테스트 코드
            Logger.getGlobal().log(Level.INFO, String.format("우승팀 예측 종료 전: 현재 시간: %s, 경기 시간: %s", ZonedDateTime.ofInstant(LocalDateTime.now().atZone(ZoneId.of("Asia/Seoul")).toInstant(), ZoneId.of("Asia/Seoul")), ZonedDateTime.ofInstant(fixture.getDate().toInstant(), ZoneId.of("Asia/Seoul"))));
            // 사용자가 기존에 우승팀 예측 진행하지 않은 경우
            if(userScorePrediction == null){
                scorePrediction = MatchPredictionDto.InquiredScorePrediction.builder()
                        .homePercentage(figureHomeWinningPercent(fixture.getId(), scoreParticipant))
                        .isParticipated(false)
                        .participant(scoreParticipant)
                        .build();
            }
            // 사용자가 기존에 우승팀 예측 진행한 경우
            else{
                scorePrediction = MatchPredictionDto.InquiredScorePrediction.builder()
                        .homePercentage(figureHomeWinningPercent(fixture.getId(), scoreParticipant))
                        .isParticipated(true)
                        .participant(scoreParticipant)
                        .build();
            }
        }
        // 우승팀 예측 종료된 경우 (경기 시작 후) -> isPredictionSuccessful 포함
        else{
            // 테스트 코드
            Logger.getGlobal().log(Level.INFO, String.format("우승팀 예측 종료 후: 현재 시간: %s, 경기 시간: %s", ZonedDateTime.ofInstant(LocalDateTime.now().atZone(ZoneId.of("Asia/Seoul")).toInstant(), ZoneId.of("Asia/Seoul")), ZonedDateTime.ofInstant(fixture.getDate().toInstant(), ZoneId.of("Asia/Seoul"))));
            // 사용자가 기존에 우승팀 예측 진행하지 않은 경우
            if(userScorePrediction == null){
                scorePrediction = MatchPredictionDto.InquiredScorePrediction.builder()
                        .homePercentage(figureHomeWinningPercent(fixture.getId(), scoreParticipant))
                        .isParticipated(false)
                        .participant(scoreParticipant)
                        .build();
            }
            // 사용자가 기존에 우승팀 예측 진행한 경우
            else{
                // 아직 경기 결과 없는 경우 -> isPredictionSuccessful 제외
                if(fixture.getHomeTeamScore() == null ){
                    scorePrediction = MatchPredictionDto.InquiredScorePrediction.builder()
                            .homePercentage(figureHomeWinningPercent(fixture.getId(), scoreParticipant))
                            .isParticipated(true)
                            .participant(scoreParticipant)
                            .build();
                }
                // 경기 결과 있는 경우
                else{
                    scorePrediction = MatchPredictionDto.InquiredScorePrediction.builder()
                            .homePercentage(figureHomeWinningPercent(fixture.getId(), scoreParticipant))
                            .isParticipated(true)
                            .participant(scoreParticipant)
                            .isPredictionSuccessful((scorePredictionService.isScoreCorrect(userScorePrediction.getHomeTeamScore(), userScorePrediction.getAwayTeamScore(), fixture) == List.of(true, true) ? true : false))
                            .build();
                }
            }
        }

        /* 선발라인업 예측 관련 데이터 조회 */
        // response에 들어갈 선발 라인업 예측 데이터
        MatchPredictionDto.InquiredLineupPrediction lineupPrediction;
        // 기존에 사용자가 진행한 우승팀 예측 데이터 (없는 경우 null)
        LineupPrediction userLineupPrediction = lineupPredictionRepository.findByMemberAndFixture(member.getId(), fixture.getId()).orElse(null);
        // 선발라인업 예측 참여자 수
        int lineupParticipant = lineupPredictionRepository.findByFixture(fixture.getId()).size();
        // 홈팀, 원정팀 예측 1 순위 포메이션 (예측 참가자 0이라 없는 경우 null)
        Integer homeFormation = lineupPredictionRepository.findAvgHomeTeamForm(fixture.getId());
        Integer awayFormation = lineupPredictionRepository.findAvgAwayTeamForm(fixture.getId());

        // 선발라인업 예측 종료 전인 경우 (경기 시작 1시간 30분 전) -> isPredictionSuccessful 전송 X
        if(ZonedDateTime.ofInstant(LocalDateTime.now().atZone(ZoneId.of("Asia/Seoul")).toInstant(), ZoneId.of("Asia/Seoul")).isBefore(ZonedDateTime.ofInstant(fixture.getDate().toInstant().minus(1, ChronoUnit.HOURS).minus(30, ChronoUnit.MINUTES), ZoneId.of("Asia/Seoul")))){
            // 테스트 코드
            Logger.getGlobal().log(Level.INFO, String.format("선발라인업 예측 종료 전: 현재 시간: %s, 경기 시간 90분 전: %s", ZonedDateTime.ofInstant(LocalDateTime.now().atZone(ZoneId.of("Asia/Seoul")).toInstant(), ZoneId.of("Asia/Seoul")), ZonedDateTime.ofInstant(fixture.getDate().toInstant().minus(1, ChronoUnit.HOURS).minus(30, ChronoUnit.MINUTES), ZoneId.of("Asia/Seoul"))));
            // 아직 예측 참가자 0인 경우
            if(lineupParticipant == 0){
                lineupPrediction = MatchPredictionDto.InquiredLineupPrediction.builder()
                        .isParticipated(false)
                        .participant(lineupParticipant)
                        .build();
            }
            // 사용자가 기존에 선발라인업 예측 진행하지 않은 경우
            else if(userLineupPrediction == null){
                lineupPrediction = MatchPredictionDto.InquiredLineupPrediction.builder()
                        .homePercentage(figureFormationPercent(fixture.getId(), lineupParticipant, homeFormation, true))
                        .awayPercentage(figureFormationPercent(fixture.getId(), lineupParticipant, awayFormation, false))
                        .homeFormation(homeFormation)
                        .awayFormation(awayFormation)
                        .participant(lineupParticipant)
                        .isParticipated(false)
                        .build();
            }
            // 사용자가 기존에 선발라인업 예측 진행한 경우
            else{
                lineupPrediction = MatchPredictionDto.InquiredLineupPrediction.builder()
                        .homePercentage(figureFormationPercent(fixture.getId(), lineupParticipant, homeFormation, true))
                        .awayPercentage(figureFormationPercent(fixture.getId(), lineupParticipant, awayFormation, false))
                        .homeFormation(homeFormation)
                        .awayFormation(awayFormation)
                        .participant(lineupParticipant)
                        .isParticipated(true)
                        .build();
            }
        }
        // 선발라인업 예측 종료 후인 경우 (경기 시작 1시간 30분 전 후) -> isPredictionSuccessful 전송
        else{
            // 테스트 코드
            Logger.getGlobal().log(Level.INFO, String.format("선발라인업 예측 종료 후: 현재 시간: %s, 경기 시간 90분 전: %s", ZonedDateTime.ofInstant(LocalDateTime.now().atZone(ZoneId.of("Asia/Seoul")).toInstant(), ZoneId.of("Asia/Seoul")), ZonedDateTime.ofInstant(fixture.getDate().toInstant().minus(1, ChronoUnit.HOURS).minus(30, ChronoUnit.MINUTES), ZoneId.of("Asia/Seoul"))));
            // 아직 예측 참가자 0인 경우
            if(lineupParticipant == 0){
                lineupPrediction = MatchPredictionDto.InquiredLineupPrediction.builder()
                        .isParticipated(false)
                        .participant(lineupParticipant)
                        .build();
            }
            // 사용자가 기존에 선발라인업 예측 진행하지 않은 경우
            else if(userLineupPrediction == null){
                lineupPrediction = MatchPredictionDto.InquiredLineupPrediction.builder()
                        .homePercentage(figureFormationPercent(fixture.getId(), lineupParticipant, homeFormation, true))
                        .awayPercentage(figureFormationPercent(fixture.getId(), lineupParticipant, awayFormation, false))
                        .homeFormation(homeFormation)
                        .awayFormation(awayFormation)
                        .isParticipated(false)
                        .participant(lineupParticipant)
                        .build();
            }
            // 사용자가 기존에 선발라인업 예측 진행한 경우
            else{
                // 실제 선발라인업 정보 (크롤링 시도 후 아직 선발라인업 정보 없어 timeout 시 null)
                MatchLineupDto.MatchLineupResponse matchLineup = matchLineupService.findMatchLineupByFixture(fixture.getId());
                // 실제 선발 라인업 정보 없는 경우
                if(matchLineup == null){
                    lineupPrediction = MatchPredictionDto.InquiredLineupPrediction.builder()
                            .homePercentage(figureFormationPercent(fixture.getId(), lineupParticipant, homeFormation, true))
                            .awayPercentage(figureFormationPercent(fixture.getId(), lineupParticipant, awayFormation, false))
                            .homeFormation(homeFormation)
                            .awayFormation(awayFormation)
                            .isParticipated(true)
                            .participant(lineupParticipant)
                            .build();
                }
                // 실제 선발 라인업 정보 조회되는 경우
                else{
                    // 사용자가 예측한 홈팀 라인업 정보 실제와 일치하는지 체크할 수 있는 형태 객체로 생성
                    LineupPredictionDto.ResponseLineup homeLineup = LineupPredictionDto.ResponseLineup.builder()
                            .goalkeeper(lineupPredictionService.convertToPlayerInfo(lineupPredictionRepository.findFilteredPlayersByMemberAndFixture(member.getId(), fixture.getId(), 0, 0)).get(0))
                            .defenders(lineupPredictionService.convertToPlayerInfo(lineupPredictionRepository.findFilteredPlayersByMemberAndFixture(member.getId(), fixture.getId(), 1, 0)))
                            .midfielders(lineupPredictionService.convertToPlayerInfo(lineupPredictionRepository.findFilteredPlayersByMemberAndFixture(member.getId(), fixture.getId(), 2, 0)))
                            .strikers(lineupPredictionService.convertToPlayerInfo(lineupPredictionRepository.findFilteredPlayersByMemberAndFixture(member.getId(), fixture.getId(), 3, 0)))
                            .build();
                    // 사용자가 예측한 원정팀 라인업 정보 실제와 일치하는지 체크할 수 있는 형태 객체로 생성
                    LineupPredictionDto.ResponseLineup awayLineup = LineupPredictionDto.ResponseLineup.builder()
                            .goalkeeper(lineupPredictionService.convertToPlayerInfo(lineupPredictionRepository.findFilteredPlayersByMemberAndFixture(member.getId(), fixture.getId(), 0, 1)).get(0))
                            .defenders(lineupPredictionService.convertToPlayerInfo(lineupPredictionRepository.findFilteredPlayersByMemberAndFixture(member.getId(), fixture.getId(), 1, 1)))
                            .midfielders(lineupPredictionService.convertToPlayerInfo(lineupPredictionRepository.findFilteredPlayersByMemberAndFixture(member.getId(), fixture.getId(), 2, 1)))
                            .strikers(lineupPredictionService.convertToPlayerInfo(lineupPredictionRepository.findFilteredPlayersByMemberAndFixture(member.getId(), fixture.getId(), 3, 1)))
                            .build();

                    lineupPrediction = MatchPredictionDto.InquiredLineupPrediction.builder()
                            .homePercentage(figureFormationPercent(fixture.getId(), lineupParticipant, homeFormation, true))
                            .awayPercentage(figureFormationPercent(fixture.getId(), lineupParticipant, awayFormation, false))
                            .homeFormation(homeFormation)
                            .awayFormation(awayFormation)
                            .isParticipated(true)
                            .participant(lineupParticipant)
                            .isPredictionSuccessful(lineupPredictionService.isPredictionCorrect(homeFormation, awayFormation, homeLineup, awayLineup, matchLineup) == List.of(true, true)? true : false)
                            .build();
                }
            }
        }
        return MatchPredictionDto.MatchPredictionInquireResponse.builder()
                .lineupPredictions(lineupPrediction)
                .scorePredictions(scorePrediction)
                .build();
    }

    // 홈팀과 원정팀 예상 점수로 홈팀 우승 예측 백분율 구하는 method
    int figureHomeWinningPercent(Long fixtureId, Integer participant){
        // 만약 예측 진행한 사람이 0 인 경우 0 반환
        if(participant == 0) {return 0;}

        int homeWinningParticipants = scorePredictionRepository.findHomeWinningParticipants(fixtureId);
        int percent = homeWinningParticipants / participant * 100;
        return percent;
    }

    // 홈팀과 원정팀 예측 1순위 포메이션의 참여자 예측 백분율 구하는 method
    int figureFormationPercent(Long fixtureId, Integer participant, Integer formation, Boolean isHomeTeam){
        // 만약 예측 진행한 사람이 0 인 경우 0 반환
        if(participant == 0) { return 0;}

        int percent;
        if(isHomeTeam){
            percent = lineupPredictionRepository.findAvgHomeTeamFormParticipant(fixtureId, formation) / participant * 100;
        }
        else{
            percent = lineupPredictionRepository.findAvgAwayTeamFormParticipant(fixtureId, formation) / participant * 100;
        }
        return percent;
    }
}
