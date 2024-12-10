package KickIt.server.domain.scorePrediction.service;

import KickIt.server.domain.fixture.entity.Fixture;
import KickIt.server.domain.fixture.entity.FixtureRepository;
import KickIt.server.domain.member.entity.Member;
import KickIt.server.domain.member.entity.MemberRepository;
import KickIt.server.domain.member.service.MemberService;
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
    @Autowired
    MemberService memberService;

    @Transactional
    // 우승팀 예측 저장
    public HttpStatus saveScorePrediction(ScorePrediction scorePrediction){
        // 중복 저장인지 확인하기 위해 사용자의 id와 경기 id로 DB에서 scorePrediction 데이터 조회
        ScorePrediction foundScorePrediction = scorePredictionRepository.findByFixtureAndMember(scorePrediction.getFixture().getId(), scorePrediction.getMember().getId()).orElse(null);
        /*
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
            finally {
                memberService.gainPoint(scorePrediction.getMember(), 1);
                return HttpStatus.OK;
            }
        }
        // 이미 저장된 데이터가 있는 경우 -> 저장 과정 없이 false 반환
        else{ return HttpStatus.CONFLICT; }
         */
        // 시연을 위해 무조건 저장!
        // 저장 시도
        try {
            scorePrediction.setLastUpdated();
            scorePredictionRepository.save(scorePrediction);
        }
        // 실패 시 false 반환
        catch (Exception e) { return HttpStatus.INTERNAL_SERVER_ERROR; }
        // 성공 시 true 반환
        finally {
            memberService.gainPoint(scorePrediction.getMember(), 1);
            return HttpStatus.OK;
        }
    }

    @Transactional
    // 우승팀 예측 수정
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
        // 경기 결과가 나온 경우
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

    public List<Boolean> isScoreCorrect(int homeTeamScore, int awayTeamScore, Fixture fixture){
        List<Boolean> response = new ArrayList<>();

        // 실제 점수와 예측 점수 비교
        if(homeTeamScore != fixture.getHomeTeamScore()){ response.add(false); }
        else{ response.add(true); }
        if(awayTeamScore != fixture.getAwayteamScore()){ response.add(false); }
        else{ response.add(true); }

        return response;
    }

    @Transactional
    // 우승팀 예측 조회
    public ScorePredictionDto.ScorePredictionInquireResponse inquireScorePrediction(Fixture fixture, Member member){
        ScorePrediction userPrediction = scorePredictionRepository.findByFixtureAndMember(fixture.getId(), member.getId()).orElse(null);
        // 사용자가 기존에 예측한 데이터 없는 경우
        if(userPrediction == null){
            return null;
        }
        // 사용자가 기존에 예측한 데이터 있는 경우 -> response 생성 후 return
        else{
            return ScorePredictionDto.ScorePredictionInquireResponse.builder()
                    .homeTeamScore(userPrediction.getHomeTeamScore())
                    .awayTeamScore(userPrediction.getAwayTeamScore())
                    .build();
        }
    }

    @Transactional
    // 우승팀 예측 결과 조회
    public ScorePredictionDto.ScorePredictionEditResponse inquireScorePredictionResult(Fixture fixture, Member member){
        // API 호출 시 전달할 response
        ScorePredictionDto.ScorePredictionEditResponse response;
        // 수정 전 기존 사용자의 우승팀 예측 정보 (존재하지 않는 경우 null)
        ScorePrediction currentScorePrediction = scorePredictionRepository.findByFixtureAndMember(fixture.getId(), member.getId()).orElse(null);

        // 예측 참여자 수
        int participant = scorePredictionRepository.findByFixture(fixture.getId()).size();
        // 평균 예측 홈팀 점수, 원정팀 점수
        int avgHomeTeamScore;
        int avgAwayTeamScore;

        // 아직 아무도 예측을 진행하지 않은 경우
        // 홈팀, 원정팀 점수 null -> 평균 값 계산 시 오류 발생하므로 따로 처리
        if(participant == 0){
            return ScorePredictionDto.ScorePredictionEditResponse.builder()
                    .participant(participant)
                    .build();
        }
        else{
            avgHomeTeamScore = scorePredictionRepository.findAvgHomeTeamScore(fixture.getId());
            avgAwayTeamScore = scorePredictionRepository.findAvgAwayTeamScore(fixture.getId());
        }

        // 사용자의 기존 예측 데이터가 있는지, 실제 경기 결과가 나왔는지에 따라 구성 필드 달라지므로 case 나누어 처리 함.
        // 사용자가 기존에 예측을 진행하지 않은 경우
        if(currentScorePrediction == null){
            // 사용자 예측 X, 경기 결과 X
            if(fixture.getHomeTeamScore() == null || fixture.getAwayteamScore() == null){
                return ScorePredictionDto.ScorePredictionEditResponse.builder()
                        .participant(participant)
                        .avgHomeTeamScore(avgHomeTeamScore)
                        .avgAwayTeamScore(avgAwayTeamScore)
                        .build();
            }
            // 사용자 예측 X, 경기 결과 O
            else{
                return ScorePredictionDto.ScorePredictionEditResponse.builder()
                        .participant(participant)
                        .avgHomeTeamScore(avgHomeTeamScore)
                        .avgAwayTeamScore(avgAwayTeamScore)
                        .avgPrediction(isScoreCorrect(avgHomeTeamScore, avgAwayTeamScore, fixture))
                        .build();
            }
        }
        // 사용자가 진행한 예측 데이터가 있는 경우
        else{
            // 사용자 예측 O, 경기 결과 X
            if(fixture.getHomeTeamScore() == null || fixture.getAwayteamScore() == null){
                return ScorePredictionDto.ScorePredictionEditResponse.builder()
                        .participant(participant)
                        .homeTeamScore(currentScorePrediction.getHomeTeamScore())
                        .awayTeamScore(currentScorePrediction.getAwayTeamScore())
                        .avgHomeTeamScore(avgHomeTeamScore)
                        .avgAwayTeamScore(avgAwayTeamScore)
                        .build();
            }
            // 사용자 예측 O, 경기 결과 O
            else{
                return ScorePredictionDto.ScorePredictionEditResponse.builder()
                        .participant(participant)
                        .homeTeamScore(currentScorePrediction.getHomeTeamScore())
                        .awayTeamScore(currentScorePrediction.getAwayTeamScore())
                        .avgHomeTeamScore(avgHomeTeamScore)
                        .avgAwayTeamScore(avgAwayTeamScore)
                        .userPrediction(isScoreCorrect(currentScorePrediction.getHomeTeamScore(), currentScorePrediction.getAwayTeamScore(), fixture))
                        .avgPrediction(isScoreCorrect(avgHomeTeamScore, avgAwayTeamScore, fixture))
                        .build();
            }
        }
    }
}
