package KickIt.server.domain.heartRate.service;

import KickIt.server.domain.heartRate.dto.FixtureHeartRateStatisticsRepository;
import KickIt.server.domain.heartRate.dto.HeartRateDto;
import KickIt.server.domain.heartRate.entity.FixtureHeartRateStatistics;
import KickIt.server.domain.realtime.dto.RealTimeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FixtureHeartRateStatisticsService {

    private final FixtureHeartRateStatisticsRepository fixtureHeartRateStatisticsRepository;
    private final RealTimeRepository realTimeRepository;
    private final HeartRateParser heartRateParser;

    @Autowired
    public FixtureHeartRateStatisticsService(FixtureHeartRateStatisticsRepository fixtureHeartRateStatisticsRepository, RealTimeRepository realTimeRepository, HeartRateParser heartRateParser) {
        this.fixtureHeartRateStatisticsRepository = fixtureHeartRateStatisticsRepository;
        this.realTimeRepository = realTimeRepository;
        this.heartRateParser = heartRateParser;
    }

    // 경기 시작 시간 가져오기
    public List<String> getStartTime(Long fixture_id) {
        List<String> startTime = realTimeRepository.getEventTime(fixture_id);
        return startTime;
    }

    public void calculateHeartRate(HeartRateDto heartRateDTO) {
        Long fixture_id = heartRateDTO.getMatchId();

        // 중복처리
        if (fixtureHeartRateStatisticsRepository.findByFixtureId(fixture_id).isEmpty()) {
            // 객체 생성
            FixtureHeartRateStatistics fixtureHeartRateStatistics = new FixtureHeartRateStatistics(fixture_id);
            fixtureHeartRateStatisticsRepository.save(fixtureHeartRateStatistics);

            // 시간 업데이트
            List<String> startTime = getStartTime(fixture_id);
            if (!startTime.isEmpty()) {
                fixtureHeartRateStatisticsRepository.updateTime(fixture_id, startTime.get(0), startTime.get(1));
            } else {
                System.out.println("해당 경기가 저장되지 않았습니다.");
            }

            // BPM 업데이트
            List<Integer> bpm3 = heartRateParser.minAvgMax(fixture_id);
            fixtureHeartRateStatisticsRepository.updateBPM(fixture_id, bpm3.get(0), bpm3.get(1), bpm3.get(2));

        }
    }

}
