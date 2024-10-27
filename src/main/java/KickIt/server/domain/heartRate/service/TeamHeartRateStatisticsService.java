package KickIt.server.domain.heartRate.service;

import KickIt.server.domain.heartRate.dto.HeartRateDto;
import KickIt.server.domain.heartRate.dto.MinAvgMaxDto;
import KickIt.server.domain.heartRate.entity.StatisticsRepository;
import KickIt.server.domain.heartRate.entity.TeamHeartRateStatisticsRepository;
import KickIt.server.domain.heartRate.entity.TeamHeartRateStatistics;
import KickIt.server.domain.member.dto.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TeamHeartRateStatisticsService {

    private final TeamHeartRateStatisticsRepository teamHeartRateStatisticsRepository;
    private final MemberRepository memberRepository;
    private final HeartRateParser heartRateParser;
    private final StatisticsRepository statisticsRepository;

    @Autowired
    public TeamHeartRateStatisticsService(TeamHeartRateStatisticsRepository teamHeartRateStatisticsRepository, MemberRepository memberRepository, HeartRateParser heartRateParser, StatisticsRepository statisticsRepository) {
        this.teamHeartRateStatisticsRepository = teamHeartRateStatisticsRepository;
        this.memberRepository = memberRepository;
        this.heartRateParser = heartRateParser;
        this.statisticsRepository = statisticsRepository;
    }

    // 사용자 Id
    public Long getMemberId(String email) {
        return memberRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("해당 이메일을 가진 사용자가 없습니다."))
                .getId();
    }

    public void calculateTeamHeartRate(HeartRateDto heartRateDto) {
        Long fixtureId = heartRateDto.getMatchId();

        if (teamHeartRateStatisticsRepository.findByFixtureId(fixtureId).isEmpty()) {
            // 심박수 업데이트
            List<MinAvgMaxDto> homeHeartRateMinAvgMax = calculateHeartRate(fixtureId, "home");
            List<MinAvgMaxDto> awayHeartRateMinAvgMax = calculateHeartRate(fixtureId, "away");


            // 팀 심박수 별 통계 저장
            TeamHeartRateStatistics homeHeartRateStatistics = new TeamHeartRateStatistics(fixtureId, "home",
                    homeHeartRateMinAvgMax.get(0).getMin(), homeHeartRateMinAvgMax.get(0).getAvg(), homeHeartRateMinAvgMax.get(0).getMax());
            TeamHeartRateStatistics awayHeartRateStatistics = new TeamHeartRateStatistics(fixtureId, "away",
                    awayHeartRateMinAvgMax.get(0).getMin(), awayHeartRateMinAvgMax.get(0).getAvg(), awayHeartRateMinAvgMax.get(0).getMax());
            teamHeartRateStatisticsRepository.save(homeHeartRateStatistics);
            teamHeartRateStatisticsRepository.save(awayHeartRateStatistics);

        }

    }

    public List<MinAvgMaxDto> calculateHeartRate(Long fixtureId, String teamType) {
        // 팀 별 심박수를 가져옴
        List<Integer> bpm = statisticsRepository.getHeartRate(fixtureId, teamType);
        // 심박수 min, avg, max 계산
        List<MinAvgMaxDto> heartRate = heartRateParser.minAvgMaxDto(bpm);

        return heartRate;
    }

    public List<MinAvgMaxDto> getTeamMinAvgMax(Long fixtureId, String teamType) {
        return teamHeartRateStatisticsRepository.getHeartRateMinAvgMax(fixtureId, teamType);
    }

}
