package KickIt.server.domain.realtime.service;

import KickIt.server.domain.fixture.entity.Fixture;
import KickIt.server.domain.realtime.dto.RealTimeDto;
import KickIt.server.domain.realtime.dto.RealTimeRepository;
import KickIt.server.domain.realtime.entity.RealTime;
import KickIt.server.domain.teams.dto.TeaminfoDto;
import KickIt.server.domain.teams.entity.Teaminfo;
import KickIt.server.domain.teams.entity.TeaminfoRepository;
import KickIt.server.global.common.crawler.RealTimeCrawler;
import KickIt.server.global.common.crawler.TeaminfoCrawler;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class RealTimeService {

    // 크롤링 데이터 DB 저장
    private final RealTimeRepository realTimeRepository;

    @Autowired
    public RealTimeService(RealTimeRepository realTimeRepository){
        this.realTimeRepository = realTimeRepository;
    }

    // 완전히 동일한 이벤트인지 확인
    private boolean isRealTimeExist(RealTime realTime){
        return realTimeRepository.findByMatchIdAndTimeAndEventNameAndPlayer1AndPlayer2(
                realTime.getMatchId(), realTime.getTime(), realTime.getEventName(), realTime.getPlayer1(), realTime.getPlayer2()).isPresent();
    }

    // 업데이트 된 이벤트인지 확인
    private boolean updateEvent(RealTime realTime) {
        return realTimeRepository.findByMatchIdAndTimeAndEventNameAndPlayer1(
                realTime.getMatchId(), realTime.getTime(), realTime.getEventName(), realTime.getPlayer1()).isPresent();
    }


    // 중복 or 업데이트 된 이벤트 확인
    @Transactional
    public boolean findUpdate(RealTime realTime) {
        // 중복 이벤트 아님
        if (!isRealTimeExist(realTime)) {
            // player2 업데이트 아님
            if (!updateEvent(realTime)) {
                return true;
            } else {
                // 업데이트 이벤트
                updatePlayer2(realTime);
            }
        }
        return false;
    }

    // 새로운 이벤트 DB 저장
    @Transactional
    public void saveEvent(RealTime realTime ){
        if (findUpdate(realTime)) {
            realTimeRepository.save(realTime);
        }
    }

    // player2 업데이트
    public void updatePlayer2(RealTime realTime) {
        realTimeRepository.updateEvent(realTime.getPlayer2(), realTime.getMatchId(), realTime.getTime(), realTime.getEventName(), realTime.getPlayer1());

    }

    // 경기 id로 RealTime 반환하기
    @Transactional
    public List<RealTimeDto.RealTimeResponse> findRealTimeByMatchId(Long matchId) {
        List<RealTime> realTimeList = realTimeRepository.findRealTimeByMatchId(matchId);
        List<RealTimeDto.RealTimeResponse> responseList = new ArrayList<>();
        for (RealTime realTime : realTimeList) {
            responseList.add(new RealTimeDto.RealTimeResponse(
                    realTime.getMatchId(),
                    realTime.getEventCode(),
                    realTime.getEventTime(),
                    realTime.getEventName(),
                    realTime.getPlayer1(),
                    realTime.getPlayer2(),
                    realTime.getTeamName(),
                    realTime.getTeamUrl()
            ));
        }
        return responseList;
    }


}
