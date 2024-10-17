package KickIt.server.domain.heartRate.service;

import KickIt.server.domain.heartRate.dto.HeartRateDto;
import KickIt.server.domain.heartRate.dto.StatisticsDto;
import KickIt.server.domain.heartRate.dto.StatisticsRepository;
import KickIt.server.domain.heartRate.entity.HeartRate;
import KickIt.server.domain.member.dto.MemberRepository;
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
                .getId();
    }

    // API에 전달할 리스트 get
    public List<StatisticsDto> getHeartRateStatistics(String email, Long fixtureId) {
        Long memberId = getMemberId(email);

        List<StatisticsDto> statistics = statisticsRepository.findJoinedData(memberId, fixtureId);

        for (StatisticsDto stat : statistics) {
            List<StatisticsDto.RealTimeStatisticsDto> events = getRealTimeStatistics(fixtureId);
            stat.setEvent(events);
        }

        for (StatisticsDto stat : statistics) {
            List<HeartRateDto.MatchHeartRateRecords> heartRateRecordsList = getHomeTeamHeartRate(fixtureId, "home");
            stat.setHomeTeamHeartRateRecords(heartRateRecordsList);
        }


        return statistics;
    }

    public List<StatisticsDto.RealTimeStatisticsDto> getRealTimeStatistics(Long fixtureId) {
        return statisticsRepository.getRealTimeStatistics(fixtureId);
    }

    public List<HeartRateDto.MatchHeartRateRecords> getHomeTeamHeartRate(Long fixtureId, String teamType) {
        return statisticsRepository.getHomeTeamHeartRate(fixtureId, teamType);
    }
}
