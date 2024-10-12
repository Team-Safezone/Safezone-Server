package KickIt.server.domain.heartRate.service;

import KickIt.server.domain.heartRate.dto.HeartRateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// 사용자 심박수 최소, 최대 계산 클래스
@Service
public class HeartRateParser {

    private final HeartRateRepository heartRateRepository;

    @Autowired
    public HeartRateParser(HeartRateRepository heartRateRepository) {
        this.heartRateRepository = heartRateRepository;
    }

    public List<Integer> minMax(Long memberId, Long fixtureId) {

        List<Integer> heartRate = heartRateRepository.getHeartRate(memberId, fixtureId);

        // 데이터 없을 경우
        if (heartRate == null || heartRate.isEmpty()) {
            return Collections.emptyList();  // 빈 리스트 반환
        }

        int min = heartRate.get(0);
        int max = heartRate.get(0);

        for (Integer i : heartRate) {
            if (min > i) {
                min = i;
            }
            if (max < i) {
                max = i;
            }
        }

        List<Integer> minmaxList = new ArrayList<>();
        minmaxList.add(min);
        minmaxList.add(max);

        return minmaxList;
    }
}
