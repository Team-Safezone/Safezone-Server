package KickIt.server.domain.realtime.service;

import KickIt.server.domain.fixture.entity.Fixture;
import KickIt.server.domain.fixture.entity.FixtureRepository;
import KickIt.server.domain.heartRate.dto.EventHeartRateDto;
import KickIt.server.domain.heartRate.entity.HeartRateRepository;
import KickIt.server.domain.member.entity.MemberRepository;
import KickIt.server.domain.realtime.dto.RealTimeDto;
import KickIt.server.domain.realtime.entity.RealTimeRepository;
import KickIt.server.domain.realtime.entity.RealTime;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class RealTimeService {

    // 크롤링 데이터 DB 저장
    private final RealTimeRepository realTimeRepository;
    private final FixtureRepository fixtureRepository;
    private final MemberRepository memberRepository;
    private final HeartRateRepository heartRateRepository;

    @Autowired
    public RealTimeService(RealTimeRepository realTimeRepository, FixtureRepository fixtureRepository, MemberRepository memberRepository, HeartRateRepository heartRateRepository) {
        this.realTimeRepository = realTimeRepository;
        this.fixtureRepository = fixtureRepository;
        this.memberRepository = memberRepository;
        this.heartRateRepository = heartRateRepository;
    }

    // memberId 가져오기
    public Long getMemberId(String email) {
        return memberRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("해당 이메일을 가진 사용자가 없습니다."))
                .getId();
    }

    // fixtureId 가져오기
    public Long getFixtureId(Long id) {
        Fixture fixture = fixtureRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 경기입니다."));

        return fixture.getId();
    }

    // 완전히 동일한 이벤트인지 확인
    private boolean isRealTimeExist(RealTime realTime){
        return realTimeRepository.findByFixture_IdAndTimeAndEventNameAndPlayer1AndPlayer2(
                getFixtureId(realTime.getFixture().getId()), realTime.getTime(), realTime.getEventName(), realTime.getPlayer1(), realTime.getPlayer2()).isPresent();
    }

    // 업데이트 된 이벤트인지 확인
    private boolean updateEvent(RealTime realTime) {
        return realTimeRepository.findByFixture_IdAndTimeAndEventNameAndPlayer1(
                getFixtureId(realTime.getFixture().getId()), realTime.getTime(), realTime.getEventName(), realTime.getPlayer1()).isPresent();
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
        realTimeRepository.updateEvent(realTime.getPlayer2(), getFixtureId(realTime.getFixture().getId()), realTime.getTime(), realTime.getEventName(), realTime.getPlayer1());

    }

    // 경기 id로 RealTime 반환하기
    @Transactional
    public List<RealTimeDto.RealTimeResponse> findRealTimeByMatchId(String email, Long matchId) {
        Long memberId = getMemberId(email);

        List<RealTime> realTimeList = realTimeRepository.findRealTimeByFixture_Id(matchId);

        int avgHeartRate = getAvgHeartRate(memberId);

        List<EventHeartRateDto>  eventHeartRateList = getEventHeartRate(memberId, matchId);

        Map<Integer, Integer> heartRateMap = eventHeartRateList.stream()
                .collect(Collectors.toMap(EventHeartRateDto::getHeartRateDate, EventHeartRateDto::getHeartRate));

        List<RealTimeDto.RealTimeResponse> responseList = new ArrayList<>();

        for (RealTime realTime : realTimeList) {
            Integer timeKey = null;
            try {
                timeKey = Integer.parseInt(realTime.getTime());
            } catch (NumberFormatException e) {
                System.out.println("Invalid time format: " + realTime.getTime());
            }

            // heartRateMap에서 eventHeartRate를 가져옴
            Integer eventHeartRate = (timeKey != null) ? heartRateMap.get(timeKey) : null;

            responseList.add(new RealTimeDto.RealTimeResponse(
                    getFixtureId(realTime.getFixture().getId()),
                    realTime.getEventCode(),
                    realTime.getTime(),
                    realTime.getEventTime(),
                    realTime.getEventName(),
                    realTime.getPlayer1(),
                    realTime.getPlayer2(),
                    eventHeartRate,
                    avgHeartRate,
                    realTime.getTeamName(),
                    realTime.getTeamUrl()
            ));
        }
        return responseList;
    }


    // 평균 심박수 가져오기
    public int getAvgHeartRate(Long memberId) {
        int avgHeartRate = memberRepository.getMemberAvgHeartRate(memberId);
        return avgHeartRate;
    }

    // 이벤트 별 심박수 가져오기
    public List<EventHeartRateDto>  getEventHeartRate(Long memberId, Long fixtureId) {
        List<EventHeartRateDto>  heartRates = heartRateRepository.getEventHeartRate(memberId, fixtureId);

        return heartRates;
    }

}