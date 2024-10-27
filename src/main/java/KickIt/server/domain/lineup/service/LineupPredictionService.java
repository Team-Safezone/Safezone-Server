package KickIt.server.domain.lineup.service;

import KickIt.server.domain.lineup.dto.LineupPredictionDto;
import KickIt.server.domain.lineup.entity.LineupPrediction;
import KickIt.server.domain.lineup.entity.LineupPredictionRepository;
import KickIt.server.domain.lineup.entity.PredictionPlayer;
import KickIt.server.domain.teams.entity.Player;
import KickIt.server.domain.teams.entity.PlayerRepository;
import KickIt.server.domain.teams.entity.SquadRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.sound.sampled.Line;
import java.util.ArrayList;
import java.util.List;

@Service
public class LineupPredictionService {
    @Autowired
    private LineupPredictionRepository lineupPredictionRepository;
    @Autowired
    private SquadRepository squadRepository;

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
            lineupPredictionRepository.save(lineupPrediction);
        }
    }

    public LineupPredictionDto.LineUpPredictionEditResponse editLineupPredictions(LineupPrediction lineupPrediction){
        LineupPrediction currentPrediction = lineupPredictionRepository.findByMemberAndFixture(lineupPrediction.getMember().getMemberId(), lineupPrediction.getFixture().getId()).get();
        Long memberId = lineupPrediction.getMember().getMemberId();
        Long fixtureId = lineupPrediction.getFixture().getId();

        // 기존에 예측한 홈팀 라인업 정보 DB에서 가져와 구성
        LineupPredictionDto.ResponseLineup currentHomePrediction = LineupPredictionDto.ResponseLineup.builder()
                .goalkeeper(convertToPlayerInfo(lineupPredictionRepository.findFilteredPlayersByMemberAndFixture(memberId, fixtureId, 0, 0)))
                .defenders(convertToPlayerInfo(lineupPredictionRepository.findFilteredPlayersByMemberAndFixture(memberId, fixtureId, 1, 0)))
                .midfielders(convertToPlayerInfo(lineupPredictionRepository.findFilteredPlayersByMemberAndFixture(memberId, fixtureId, 2, 0)))
                .strikers(convertToPlayerInfo(lineupPredictionRepository.findFilteredPlayersByMemberAndFixture(memberId, fixtureId, 3, 0)))
                .build();

        // 기존에 예측한 원정팀 라인업 정보 DB에서 가져와 구성
        LineupPredictionDto.ResponseLineup currentAwayPrediction = LineupPredictionDto.ResponseLineup.builder()
                .goalkeeper(convertToPlayerInfo(lineupPredictionRepository.findFilteredPlayersByMemberAndFixture(memberId, fixtureId, 0, 1)))
                .defenders(convertToPlayerInfo(lineupPredictionRepository.findFilteredPlayersByMemberAndFixture(memberId, fixtureId, 1, 1)))
                .midfielders(convertToPlayerInfo(lineupPredictionRepository.findFilteredPlayersByMemberAndFixture(memberId, fixtureId, 2, 1)))
                .strikers(convertToPlayerInfo(lineupPredictionRepository.findFilteredPlayersByMemberAndFixture(memberId, fixtureId, 3, 1)))
                .build();

        LineupPredictionDto.LineUpPredictionEditResponse response = LineupPredictionDto.LineUpPredictionEditResponse.builder()
                .participant(lineupPredictionRepository.findByFixture(fixtureId).size())
                .userHomeFormation(currentPrediction.getHomeTeamForm())
                .userHomePrediction(currentHomePrediction)
                .userAwayFormation(currentPrediction.getAwayTeamForm())
                .userAwayPrediction(currentAwayPrediction)
                .avgHomeFormation(lineupPredictionRepository.findAvgHomeTeamForm(fixtureId))
                .avgAwayFormation(lineupPredictionRepository.findAvgAwayTeamForm(fixtureId))
                .build();

        currentPrediction.getPlayers().clear();
        lineupPredictionRepository.save(currentPrediction);

        currentPrediction.getPlayers().addAll(lineupPrediction.getPlayers());
        lineupPredictionRepository.save(currentPrediction);

        return response;
    }

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
