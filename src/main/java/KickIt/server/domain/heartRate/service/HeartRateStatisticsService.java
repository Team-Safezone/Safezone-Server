package KickIt.server.domain.heartRate.service;

import KickIt.server.domain.heartRate.dto.HeartRateRepository;
import KickIt.server.domain.heartRate.dto.HeartRateStatisticsRepository;
import KickIt.server.domain.realtime.dto.RealTimeRepository;
import KickIt.server.domain.realtime.entity.RealTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HeartRateStatisticsService {

    private final HeartRateStatisticsRepository heartRateStatisticsRepository;
    private final HeartRateParser heartRateParser;

    @Autowired
    public HeartRateStatisticsService(HeartRateStatisticsRepository heartRateStatisticsRepository, HeartRateParser heartRateParser) {
        this.heartRateStatisticsRepository = heartRateStatisticsRepository;
        this.heartRateParser = heartRateParser;
    }


    public void saveMinMax(Long memberId, Long fixtureId) {
        List<Integer> minMaxList;
        minMaxList = heartRateParser.minMax(memberId, fixtureId);

        heartRateStatisticsRepository.updateHeartRate(memberId, fixtureId, minMaxList.get(0), minMaxList.get(1));
    }
}
