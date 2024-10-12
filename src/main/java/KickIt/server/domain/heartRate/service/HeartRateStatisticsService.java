package KickIt.server.domain.heartRate.service;

import KickIt.server.domain.heartRate.dto.HeartRateDTO;
import KickIt.server.domain.heartRate.dto.HeartRateRepository;
import KickIt.server.domain.heartRate.dto.HeartRateStatisticsRepository;
import KickIt.server.domain.heartRate.entity.HeartRateStatistics;
import KickIt.server.domain.member.dto.MemberRepository;
import KickIt.server.domain.realtime.dto.RealTimeRepository;
import KickIt.server.domain.realtime.entity.RealTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HeartRateStatisticsService {

    private final HeartRateStatisticsRepository heartRateStatisticsRepository;
    private final HeartRateParser heartRateParser;
    private final MemberRepository memberRepository;
    private final RealTimeRepository realTimeRepository;

    @Autowired
    public HeartRateStatisticsService(HeartRateStatisticsRepository heartRateStatisticsRepository, HeartRateParser heartRateParser, MemberRepository memberRepository, RealTimeRepository realTimeRepository) {
        this.heartRateStatisticsRepository = heartRateStatisticsRepository;
        this.heartRateParser = heartRateParser;
        this.memberRepository = memberRepository;
        this.realTimeRepository = realTimeRepository;
    }

    // 사용자 Id
    public Long getMemberId(String email) {
        return memberRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("해당 이메일을 가진 사용자가 없습니다."))
                .getMemberId();
    }

    // min, max 가져오기
    public List<Integer> getMinMax(Long memberId, Long fixtureId) {
        List<Integer> minMaxList;
        minMaxList = heartRateParser.minMax(memberId, fixtureId);

        return minMaxList;
    }

    // 경기 시작 시간 가져오기
    public List<String> getStartTime(Long fixture_id) {
        List<String> startTime = realTimeRepository.getEventTime(fixture_id);
        return startTime;
    }


    // 통계 저장
    public void saveStatistics(String email, HeartRateDTO heartRateDTO) {
        Long member_id = getMemberId(email);
        Long fixture_id = heartRateDTO.getMatchId();

        // 중복 처리
        if(heartRateStatisticsRepository.findByMemberIdAndFixtureId(member_id,fixture_id).isEmpty()) {

            // 객체 생성
            HeartRateStatistics heartRateStatistics = new HeartRateStatistics(member_id, fixture_id);
            heartRateStatisticsRepository.save(heartRateStatistics);

            // 시간 업데이트
            List<String> startTime = getStartTime(fixture_id);
            if(!startTime.isEmpty()){
                heartRateStatisticsRepository.updateTime(member_id, fixture_id, startTime.get(0), startTime.get(1));
            } else {
                System.out.println("해당 경기가 저장되지 않았습니다.");
            }

            // min, max 업데이트
            List<Integer> minMax = getMinMax(member_id, fixture_id);
            if(!minMax.isEmpty()) {
                heartRateStatisticsRepository.updateHeartRate(member_id, fixture_id, minMax.get(0), minMax.get(1));
            } else {
                System.out.println("해당 경기에 심박수를 츨정하지 않았습니다.");
            }
        }
    }
}
