package KickIt.server.domain.scorePrediction.service;

import KickIt.server.domain.fixture.entity.Fixture;
import KickIt.server.domain.fixture.entity.FixtureRepository;
import KickIt.server.domain.scorePrediction.dto.ScorePredictionDto;
import KickIt.server.domain.scorePrediction.entity.ScorePrediction;
import KickIt.server.domain.scorePrediction.entity.ScorePredictionRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ScorePredictionService {
    @Autowired
    ScorePredictionRepository scorePredictionRepository;
    @Autowired
    FixtureRepository fixtureRepository;

    @Transactional
    public HttpStatus saveScorePrediction(ScorePrediction scorePrediction){
        // 중복 저장인지 확인하기 위해 사용자의 id와 경기 id로 DB에서 scorePrediction 데이터 조회
        ScorePrediction foundScorePrediction = scorePredictionRepository.findByFixtureAndMember(scorePrediction.getFixture().getId(), scorePrediction.getMember().getId()).orElse(null);
        // 새로 저장하는 데이터인 경우 -> 저장 진행
        if(foundScorePrediction == null){
            // 저장 시도
            try {
                scorePrediction.setLastUpdated();
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

    @Transactional
    public ScorePredictionDto.ScorePredictionEditResponse editScorePrediction(ScorePrediction scorePrediction){
        // 해당 예측의 경기 id
        Long fixtureId = scorePrediction.getFixture().getId();
        // 수정 전 기존 사용자의 우승팀 예측 정보 먼저 가져오기
        ScorePrediction currentScorePrediction = scorePredictionRepository.findByFixtureAndMember(fixtureId, scorePrediction.getMember().getId()).orElse(null);
        // 수정 전 기존에 사용자가 예측한 홈팀 점수
        Integer currentHomeTeamScore = currentScorePrediction.getHomeTeamScore();
        // 수정 전 기존에 사용자가 예측한 원정팀 점수
        Integer currentAwayTeamScore = currentScorePrediction.getAwayTeamScore();

        // 수정된 결과 저장
        ScorePrediction editedScorePrediction = ScorePrediction.builder()
                .fixture(currentScorePrediction.getFixture())
                .member(currentScorePrediction.getMember())
                .homeTeamScore(scorePrediction.getHomeTeamScore())
                .awayTeamScore(scorePrediction.getAwayTeamScore())
                .build();
        editedScorePrediction.setLastUpdated();
        scorePredictionRepository.save(editedScorePrediction);

        // API 호출 시  반환할 response
        ScorePredictionDto.ScorePredictionEditResponse response;

        // 경기 결과가 아직 나오지 않은 경우
        // 필드 구성에서 예측 성공 여부는 제외
        if(scorePrediction.getFixture().getHomeTeamScore() == null || scorePrediction.getFixture().getAwayteamScore() == null){
            response = ScorePredictionDto.ScorePredictionEditResponse.builder()
                    .participant(scorePredictionRepository.findByFixture(scorePrediction.getFixture().getId()).size())
                    .homeTeamScore(currentHomeTeamScore)
                    .awayTeamScore(currentAwayTeamScore)
                    .avgHomeTeamScore(scorePredictionRepository.findAvgHomeTeamScore(fixtureId))
                    .avgAwayTeamScore(scorePredictionRepository.findAvgAwayTeamScore(fixtureId))
                    .build();
        }
        // 경기 결과가 아직 나오지 않은 경우
        else{
            int avgHomeTeamScore = scorePredictionRepository.findAvgHomeTeamScore(fixtureId);
            int avgAwayTeamScore = scorePredictionRepository.findAvgAwayTeamScore(fixtureId);
            response = ScorePredictionDto.ScorePredictionEditResponse.builder()
                    .participant(scorePredictionRepository.findByFixture(scorePrediction.getFixture().getId()).size())
                    .homeTeamScore(currentHomeTeamScore)
                    .awayTeamScore(currentAwayTeamScore)
                    .avgHomeTeamScore(avgHomeTeamScore)
                    .avgAwayTeamScore(avgAwayTeamScore)
                    .userPrediction(isScoreCorrect(editedScorePrediction.getHomeTeamScore(), editedScorePrediction.getAwayTeamScore(), scorePrediction.getFixture()))
                    .avgPrediction(isScoreCorrect(avgHomeTeamScore, avgAwayTeamScore, scorePrediction.getFixture()))
                    .build();
        }
        return response;
    }

    List<Boolean> isScoreCorrect(int homeTeamScore, int awayTeamScore, Fixture fixture){
        List<Boolean> response = new ArrayList<>();

        // 실제 점수와 예측 점수 비교
        if(homeTeamScore != fixture.getHomeTeamScore()){ response.add(false); }
        else{ response.add(true); }
        if(awayTeamScore != fixture.getAwayteamScore()){ response.add(false); }
        else{ response.add(true); }

        return response;
    }
}
