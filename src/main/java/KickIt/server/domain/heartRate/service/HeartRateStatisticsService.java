package KickIt.server.domain.heartRate.service;

import KickIt.server.domain.fixture.entity.Fixture;
import KickIt.server.domain.heartRate.dto.HeartRateDto;
import KickIt.server.domain.heartRate.entity.HeartRateStatisticsRepository;
import KickIt.server.domain.heartRate.entity.HeartRateStatistics;
import KickIt.server.domain.member.entity.Member;
import KickIt.server.domain.member.entity.MemberRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HeartRateStatisticsService {

    private final HeartRateStatisticsRepository heartRateStatisticsRepository;
    private final HeartRateParser heartRateParser;
    private final MemberRepository memberRepository;
    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    public HeartRateStatisticsService(HeartRateStatisticsRepository heartRateStatisticsRepository, HeartRateParser heartRateParser, MemberRepository memberRepository) {
        this.heartRateStatisticsRepository = heartRateStatisticsRepository;
        this.heartRateParser = heartRateParser;
        this.memberRepository = memberRepository;
    }

    // 사용자 Id
    public Long getMemberId(String email) {
        return memberRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("해당 이메일을 가진 사용자가 없습니다."))
                .getId();
    }

    // min, max 가져오기
    public List<Integer> getMinMax(Long memberId, Long fixtureId) {
        List<Integer> heartRate = heartRateStatisticsRepository.getUserHeartRate(memberId, fixtureId);
        List<Integer> minAvgMaxList = heartRateParser.minAvgMaxInt(heartRate);

        return minAvgMaxList;
    }

    public int calculateAvgHeartRate(Long memberId) {
        List<Integer> getHeartRate = heartRateStatisticsRepository.getHeartRate(memberId);
        int avgHeartRate = heartRateParser.avgInt(getHeartRate);

        return avgHeartRate;
    }

    // 통계 저장
    public void saveStatistics(String email, HeartRateDto heartRateDTO) {
        Long memberId = getMemberId(email);
        Long fixtureId = heartRateDTO.getMatchId();

        // 중복 처리
        if (heartRateStatisticsRepository.findByMemberIdAndFixtureId(memberId, fixtureId).isEmpty()) {
            // 객체 생성
            HeartRateStatistics heartRateStatistics = new HeartRateStatistics(memberId, fixtureId);
            heartRateStatisticsRepository.save(heartRateStatistics);

            // min, avg, max 업데이트
            List<Integer> minAvgMax = getMinMax(memberId, fixtureId);
            if(!minAvgMax.isEmpty()) {
                heartRateStatisticsRepository.updateHeartRate(memberId, fixtureId, minAvgMax.get(0), minAvgMax.get(1), minAvgMax.get(2));
            } else {
                System.out.println("해당 경기에 심박수를 측정하지 않았습니다.");
            }

            // 사용자의 선호팀이 홈 팀 인지, 어웨이 팀 인지, 아예 다른 팀인지
            String teamType = heartRateParser.getTeamType(memberId, fixtureId);
            if(teamType.equals("others")) {
                // 선호 팀이 아닌경우 추가 처리 필요
                heartRateStatisticsRepository.updateTeamType(memberId, fixtureId, "others");
            } else {
                heartRateStatisticsRepository.updateTeamType(memberId, fixtureId, teamType);
            }

            // 사용자 심박수 평균값 업데이트
            int avgHeartRate = calculateAvgHeartRate(memberId);
            heartRateStatisticsRepository.updateAvg(memberId, avgHeartRate);

        }

    }


}
