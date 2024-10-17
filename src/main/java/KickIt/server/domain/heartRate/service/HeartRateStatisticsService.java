package KickIt.server.domain.heartRate.service;

import KickIt.server.domain.heartRate.dto.HeartRateDto;
import KickIt.server.domain.heartRate.dto.HeartRateStatisticsRepository;
import KickIt.server.domain.heartRate.entity.HeartRateStatistics;
import KickIt.server.domain.member.dto.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HeartRateStatisticsService {

    private final HeartRateStatisticsRepository heartRateStatisticsRepository;
    private final HeartRateParser heartRateParser;
    private final MemberRepository memberRepository;

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
        List<Integer> minMaxList;
        minMaxList = heartRateParser.minMax(memberId, fixtureId);

        return minMaxList;
    }

    // 통계 저장
    public void saveStatistics(String email, HeartRateDto heartRateDTO) {
        Long member_id = getMemberId(email);
        Long fixture_id = heartRateDTO.getMatchId();

        // 중복 처리
        if(heartRateStatisticsRepository.findByMemberIdAndFixtureId(member_id,fixture_id).isEmpty()) {

            // 객체 생성
            HeartRateStatistics heartRateStatistics = new HeartRateStatistics(member_id, fixture_id);
            heartRateStatisticsRepository.save(heartRateStatistics);

            // min, max 업데이트
            List<Integer> minMax = getMinMax(member_id, fixture_id);
            if(!minMax.isEmpty()) {
                heartRateStatisticsRepository.updateHeartRate(member_id, fixture_id, minMax.get(0), minMax.get(1));
            } else {
                System.out.println("해당 경기에 심박수를 측정하지 않았습니다.");
            }

            // 사용자의 선호팀이 홈 팀 인지, 어웨이 팀 인지, 아예 다른 팀인지
            String teamType = heartRateParser.getTeamType(member_id, fixture_id);
            if(teamType.equals("others")) {
                // 선호 팀이 아닌경우 추가 처리 필요
                heartRateStatisticsRepository.updateTeamType(member_id, fixture_id, "others");
            } else {
                heartRateStatisticsRepository.updateTeamType(member_id, fixture_id, teamType);
            }
        }
    }


}
