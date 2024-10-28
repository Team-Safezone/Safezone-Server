package KickIt.server.domain.heartRate.service;

import KickIt.server.domain.heartRate.dto.*;
import KickIt.server.domain.heartRate.dto.MinAvgMaxDto;
import KickIt.server.domain.heartRate.entity.StatisticsRepository;
import KickIt.server.domain.heartRate.entity.TeamHeartRateRepository;
import KickIt.server.domain.member.dto.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StatisticsService {

    private final MemberRepository memberRepository;
    private final StatisticsRepository statisticsRepository;
    private final TeamHeartRateStatisticsService teamHeartRateStatisticsService;
    private final TeamHeartRateRepository teamHeartRateRepository;

    @Autowired
    public StatisticsService(MemberRepository memberRepository, StatisticsRepository statisticsRepository, TeamHeartRateStatisticsService teamHeartRateStatisticsService, TeamHeartRateRepository teamHeartRateRepository) {
        this.memberRepository = memberRepository;
        this.statisticsRepository = statisticsRepository;
        this.teamHeartRateStatisticsService = teamHeartRateStatisticsService;
        this.teamHeartRateRepository = teamHeartRateRepository;
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

        // 이벤트 리스트 추가
        for (StatisticsDto stat : statistics) {
            List<RealTimeStatisticsDto> events = getRealTimeStatistics(fixtureId);
            stat.setEvent(events);
        }

        // 홈팀 심박수 리스트
        for (StatisticsDto stat : statistics) {
            List<HeartRateDto.MatchHeartRateRecords> heartRateRecordsList = getTeamHeartRate(fixtureId, "home");
            stat.setHomeTeamHeartRateRecords(heartRateRecordsList);
        }

        // 어웨이팀 심박수 리스트
        for (StatisticsDto stat : statistics) {
            List<HeartRateDto.MatchHeartRateRecords> heartRateRecordsList = getTeamHeartRate(fixtureId, "away");
            stat.setAwayTeamHeartRateRecords(heartRateRecordsList);
        }

        // 홈팀 심박수 통계
        for (StatisticsDto stat : statistics) {
            List<MinAvgMaxDto> minAvgMaxDtoList = getTeamMinAvgMax(fixtureId,"home");
            stat.setHomeTeamHeartRate(minAvgMaxDtoList);
        }

        // 어웨이팀 심박수 통계
        for (StatisticsDto stat : statistics) {
            List<MinAvgMaxDto> minAvgMaxDtoList = getTeamMinAvgMax(fixtureId,"away");
            stat.setAwayTeamHeartRate(minAvgMaxDtoList);
        }

        return statistics;
    }


    public List<RealTimeStatisticsDto> getRealTimeStatistics(Long fixtureId) {
        return statisticsRepository.getRealTimeStatistics(fixtureId);
    }

    public List<MinAvgMaxDto> getTeamMinAvgMax(Long fixtureId, String teamType) {
        return teamHeartRateStatisticsService.getTeamMinAvgMax(fixtureId,teamType);
    }

    public List<HeartRateDto.MatchHeartRateRecords> getTeamHeartRate(Long fixtureId, String teamType) {
        return teamHeartRateRepository.getHomeAwayTeamHeartRate(fixtureId, teamType);
    }

}
