package KickIt.server.domain.heartRate.service;

import KickIt.server.domain.heartRate.dto.HeartRateStatisticsRepository;
import KickIt.server.domain.heartRate.dto.StatisticsDto;
import KickIt.server.domain.heartRate.dto.StatisticsRepository;
import KickIt.server.domain.member.dto.MemberRepository;
import KickIt.server.domain.realtime.dto.RealTimeStatisticsDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StatisticsService {

    private final MemberRepository memberRepository;
    private final StatisticsRepository statisticsRepository;
    public StatisticsService(MemberRepository memberRepository, StatisticsRepository statisticsRepository) {
        this.memberRepository = memberRepository;
        this.statisticsRepository = statisticsRepository;
    }

    // 사용자 Id
    public Long getMemberId(String email) {
        return memberRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("해당 이메일을 가진 사용자가 없습니다."))
                .getMemberId();
    }

    public List<StatisticsDto> getHeartRateStatistics(String email, Long fixtureId) {
        Long memberId = getMemberId(email);

        List<StatisticsDto> statistics = statisticsRepository.findJoinedData(memberId, fixtureId);

        for (StatisticsDto stat : statistics) {
            List<RealTimeStatisticsDto> events = getRealTimeStatistics(fixtureId);
            stat.setEvent(events);
        }

        return statistics;
    }

    public List<RealTimeStatisticsDto> getRealTimeStatistics(Long fixtureId) {
        return statisticsRepository.getRealTimeStatistics(fixtureId);
    }
}
