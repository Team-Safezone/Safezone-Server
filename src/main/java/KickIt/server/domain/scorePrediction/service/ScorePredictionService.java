package KickIt.server.domain.scorePrediction.service;

import KickIt.server.domain.scorePrediction.entity.ScorePrediction;
import KickIt.server.domain.scorePrediction.entity.ScorePredictionRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class ScorePredictionService {
    @Autowired
    ScorePredictionRepository scorePredictionRepository;

    @Transactional
    public HttpStatus saveScorePrediction(ScorePrediction scorePrediction){
        // 중복 저장인지 확인하기 위해 사용자의 id와 경기 id로 DB에서 scorePrediction 데이터 조회
        ScorePrediction foundScorePrediction = scorePredictionRepository.findByFixtureAndMember(scorePrediction.getFixture().getId(), scorePrediction.getMember().getId()).orElse(null);
        // 새로 저장하는 데이터인 경우 -> 저장 진행
        if(foundScorePrediction == null){
            // 저장 시도
            try {
                scorePredictionRepository.save(scorePrediction);
            }
            // 실패 시 false 반환
            catch (Exception e) { return HttpStatus.INTERNAL_SERVER_ERROR; }
            // 성공 시 true 반환
            finally { return HttpStatus.OK; }
        }
        // 이미 저장된 데이터가 있는 경우 -> 저장 과정 없이 false 반환
        else{ return HttpStatus.CONFLICT; }
    }
}
