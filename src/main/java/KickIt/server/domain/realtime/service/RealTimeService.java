package KickIt.server.domain.realtime.service;

import KickIt.server.domain.realtime.dto.RealTimeRepository;
import KickIt.server.domain.realtime.entity.RealTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

public class RealTimeService {

    private final RealTimeRepository realTimeRepository;

    @Autowired
    public RealTimeService(RealTimeRepository realTimeRepository) {
        this.realTimeRepository = realTimeRepository;
    }

    public void saveRealTime(RealTime realTime) {
        realTimeRepository.save(realTime);
    }

}
