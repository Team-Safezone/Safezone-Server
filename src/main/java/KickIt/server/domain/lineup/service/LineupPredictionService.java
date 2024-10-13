package KickIt.server.domain.lineup.service;

import KickIt.server.domain.lineup.dto.LineupPredictionDto;
import KickIt.server.domain.lineup.entity.LineupPrediction;
import KickIt.server.domain.lineup.entity.LineupPredictionRepository;
import KickIt.server.domain.lineup.entity.PredictionPlayer;
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

        LineupPredictionDto.LineUpPredictionEditResponse response = LineupPredictionDto.LineUpPredictionEditResponse.builder()
                .homeFormation(lineupPrediction.getHomeTeamForm())
                .awayFormation(lineupPrediction.getAwayTeamForm())
                .homeGoalkeeper(new LineupPredictionDto.ResponsePlayerInfo(lineupPredictionRepository.findFilteredPlayersByMemberAndFixture(memberId, fixtureId, 0, 0).get(0).getPlayer()))
                .homeDefenders(convertToPlayerInfo(lineupPredictionRepository.findFilteredPlayersByMemberAndFixture(memberId, fixtureId, 1, 0)))
                .homeMidfielders(convertToPlayerInfo(lineupPredictionRepository.findFilteredPlayersByMemberAndFixture(memberId, fixtureId, 2, 0)))
                .homeStrikers(convertToPlayerInfo(lineupPredictionRepository.findFilteredPlayersByMemberAndFixture(memberId, fixtureId, 3, 0)))
                .awayGoalkeeper(new LineupPredictionDto.ResponsePlayerInfo(lineupPredictionRepository.findFilteredPlayersByMemberAndFixture(memberId, fixtureId, 0, 1).get(0).getPlayer()))
                .awayDefenders(convertToPlayerInfo(lineupPredictionRepository.findFilteredPlayersByMemberAndFixture(memberId, fixtureId, 1, 1)))
                .awayMidfielders(convertToPlayerInfo(lineupPredictionRepository.findFilteredPlayersByMemberAndFixture(memberId, fixtureId, 2, 1)))
                .awayStrikers(convertToPlayerInfo(lineupPredictionRepository.findFilteredPlayersByMemberAndFixture(memberId, fixtureId, 3, 1))).build();

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
}
