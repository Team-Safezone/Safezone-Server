package KickIt.server.domain.lineup.service;

import KickIt.server.domain.lineup.entity.LineupPrediction;
import KickIt.server.domain.lineup.entity.LineupPredictionRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.sound.sampled.Line;

@Service
public class LineupPredictionService {
    @Autowired
    private LineupPredictionRepository lineupPredictionRepository;
    @Transactional
    public void saveLineupPredictions(LineupPrediction lineupPrediction){
        // member id와 fixture id로 중복 검사해서 중복 데이터 존재 -> 업데이트
        if(lineupPredictionRepository.findByMemberAndFixture(lineupPrediction.getMember().getMemberId(), lineupPrediction.getFixture().getId()).isPresent()){
            LineupPrediction currentPrediction = lineupPredictionRepository.findByMemberAndFixture(lineupPrediction.getMember().getMemberId(), lineupPrediction.getFixture().getId()).get();
            currentPrediction.getPlayers().clear();
            currentPrediction.getPlayers().addAll(lineupPrediction.getPlayers());
            lineupPredictionRepository.save(currentPrediction);
        }
        // member id와 fixture id로 중복 검사해서 중복 데이터 없음 -> 새로 저장
        else{
            lineupPredictionRepository.save(lineupPrediction);
        }
    }
}
