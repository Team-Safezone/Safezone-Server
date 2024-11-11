package KickIt.server.domain.heartRate.service;

import KickIt.server.domain.fixture.entity.Fixture;
import KickIt.server.domain.fixture.entity.FixtureRepository;
import KickIt.server.domain.heartRate.dto.HeartRateDto;
import KickIt.server.domain.heartRate.entity.HeartRateRepository;
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
    private final HeartRateRepository heartRateRepository;
    private final FixtureRepository fixtureRepository;

    @Autowired
    public HeartRateStatisticsService(HeartRateStatisticsRepository heartRateStatisticsRepository, HeartRateParser heartRateParser, MemberRepository memberRepository, HeartRateRepository heartRateRepository, FixtureRepository fixtureRepository) {
        this.heartRateStatisticsRepository = heartRateStatisticsRepository;
        this.heartRateParser = heartRateParser;
        this.memberRepository = memberRepository;
        this.heartRateRepository = heartRateRepository;
        this.fixtureRepository = fixtureRepository;
    }

    // 사용자 Id
    public Long getMemberId(String email) {
        return memberRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("해당 이메일을 가진 사용자가 없습니다."))
                .getId();
    }

    // min, max 가져오기
    public List<Integer> getMinMax(Long memberId, Long fixtureId) {
        List<Integer> heartRate = heartRateRepository.getUserHeartRate(memberId, fixtureId);
        List<Integer> minAvgMaxList = heartRateParser.minAvgMaxInt(heartRate);

        return minAvgMaxList;
    }

    public int calculateAvgHeartRate(Long memberId) {
        List<Integer> getHeartRate = heartRateStatisticsRepository.getHeartRate(memberId);
        int avgHeartRate = heartRateParser.avgInt(getHeartRate);

        return avgHeartRate;
    }

    public int getMax(String email, Long fixtureId) {
        int max = 0;
        Long memberId = getMemberId(email);

        List<HeartRateStatistics> heartRateStatistics = heartRateStatisticsRepository.findByMember_IdAndFixture_Id(memberId, fixtureId);

        if(!heartRateStatistics.isEmpty()){
            max = heartRateStatisticsRepository.getMaxHeartRate(memberId, fixtureId);
        }
        return max;
    }

    // 통계 저장
    public void saveStatistics(String email, HeartRateDto heartRateDto) {
        Member member = getMember(email);
        Fixture fixture = getFixture(heartRateDto.getMatchId());
        Long memberId = member.getId();
        Long fixtureId = fixture.getId();

        // 중복 처리
        if (heartRateStatisticsRepository.findByMember_IdAndFixture_Id(memberId, fixtureId).isEmpty()) {
            // 객체 생성
            HeartRateStatistics heartRateStatistics = new HeartRateStatistics(member, fixture);
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

    public Member getMember(String email) {
        // 이메일로 회원 정보 조회
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("회원이 존재하지 않습니다."));

        return member;
    }

    public Fixture getFixture(Long matchId) {
        // 경기 정보 조회
        Fixture fixture = fixtureRepository.findById(matchId)
                .orElseThrow(() -> new IllegalArgumentException("경기가 존재하지 않습니다."));

        return fixture;
    }

}
