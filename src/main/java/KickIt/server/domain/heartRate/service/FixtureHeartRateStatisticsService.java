package KickIt.server.domain.heartRate.service;

import KickIt.server.domain.fixture.entity.Fixture;
import KickIt.server.domain.fixture.entity.FixtureRepository;
import KickIt.server.domain.heartRate.entity.FixtureHeartRateStatisticsRepository;
import KickIt.server.domain.heartRate.dto.HeartRateDto;
import KickIt.server.domain.heartRate.entity.HeartRateRepository;
import KickIt.server.domain.heartRate.entity.FixtureHeartRateStatistics;
import KickIt.server.domain.realtime.entity.RealTimeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FixtureHeartRateStatisticsService {

    private final FixtureHeartRateStatisticsRepository fixtureHeartRateStatisticsRepository;
    private final RealTimeRepository realTimeRepository;
    private final HeartRateParser heartRateParser;
    private final HeartRateRepository heartRateRepository;
    private final FixtureRepository fixtureRepository;

    @Autowired
    public FixtureHeartRateStatisticsService(FixtureHeartRateStatisticsRepository fixtureHeartRateStatisticsRepository, RealTimeRepository realTimeRepository, HeartRateParser heartRateParser, HeartRateRepository heartRateRepository, FixtureRepository fixtureRepository) {
        this.fixtureHeartRateStatisticsRepository = fixtureHeartRateStatisticsRepository;
        this.realTimeRepository = realTimeRepository;
        this.heartRateParser = heartRateParser;
        this.heartRateRepository = heartRateRepository;
        this.fixtureRepository = fixtureRepository;
    }

    // 경기 시작 시간 가져오기
    public List<String> getStartTime(Long fixture_id) {
        List<String> startTime = realTimeRepository.getEventTime(fixture_id);
        return startTime;
    }

    public void calculateHeartRate(Long fixtureId) {
        Fixture fixture = getFixture(fixtureId);

        // 중복처리
        if (fixtureHeartRateStatisticsRepository.findByFixtureId(fixtureId).isEmpty()) {
            // 객체 생성
            FixtureHeartRateStatistics fixtureHeartRateStatistics = new FixtureHeartRateStatistics(fixture);
            fixtureHeartRateStatisticsRepository.save(fixtureHeartRateStatistics);

            // 시간 업데이트
            List<String> startTime = getStartTime(fixtureId);
            if (!startTime.isEmpty()) {
                fixtureHeartRateStatisticsRepository.updateTime(fixtureId, startTime.get(0), startTime.get(1));
            } else {
                System.out.println("해당 경기가 저장되지 않았습니다.");
            }

            // BPM 업데이트
            List<Integer> bpm = heartRateRepository.getAllHeartRate(fixtureId);
            List<Integer> bpm3 = heartRateParser.minAvgMaxInt(bpm);
            fixtureHeartRateStatisticsRepository.updateBPM(fixtureId, bpm3.get(0), bpm3.get(1), bpm3.get(2));

            // 퍼센트 계산
            int homeTeamViewerPercentage = homeTeamPercent(fixtureId);

            fixtureHeartRateStatisticsRepository.updatePercent(fixtureId, homeTeamViewerPercentage);

        }
    }

    public int homeTeamPercent(Long fixtureId) {
        // 팀 별 심박수 측정 사용자
        int homeFanCount = fixtureHeartRateStatisticsRepository.teamUser(fixtureId, "home");
        int awayFanCount = fixtureHeartRateStatisticsRepository.teamUser(fixtureId, "away");

        int totalCount = homeFanCount + awayFanCount;

//        System.out.println("totalCount = " + totalCount);
//        //분자
//        System.out.println("homeFanCount = " + homeFanCount);
//        System.out.println("awayFanCount = " + awayFanCount);

        int homePercentage = 0;

        if (totalCount != 0) {
            homePercentage = (homeFanCount * 100 /totalCount) ;
        } else {
            homePercentage = 0;
        }

        return homePercentage;
    }

    public Fixture getFixture(Long matchId) {
        // 경기 정보 조회
        Fixture fixture = fixtureRepository.findById(matchId)
                .orElseThrow(() -> new IllegalArgumentException("경기가 존재하지 않습니다."));

        return fixture;
    }
}
