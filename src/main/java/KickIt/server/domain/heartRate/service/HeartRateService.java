package KickIt.server.domain.heartRate.service;

import KickIt.server.domain.heartRate.dto.HeartRateDto;
import KickIt.server.domain.heartRate.entity.HeartRateRepository;
import KickIt.server.domain.heartRate.entity.HeartRateStatisticsRepository;
import KickIt.server.domain.heartRate.entity.HeartRate;
import KickIt.server.domain.member.dto.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HeartRateService {

    private final HeartRateRepository heartRateRepository;
    private final MemberRepository memberRepository;
    private final HeartRateParser heartRateParser;
    private final HeartRateStatisticsRepository heartRateStatisticsRepository;

    @Autowired
    public HeartRateService(HeartRateRepository heartRateRepository, MemberRepository memberRepository, HeartRateParser heartRateParser, HeartRateStatisticsRepository heartRateStatisticsRepository) {
        this.heartRateRepository = heartRateRepository;
        this.memberRepository = memberRepository;
        this.heartRateParser = heartRateParser;
        this.heartRateStatisticsRepository = heartRateStatisticsRepository;
    }

    // 사용자 Id
    public Long getMemberId(String email) {
        return memberRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("해당 이메일을 가진 사용자가 없습니다."))
                .getId();
    }

    // 심박수 저장
    public void save(String email, HeartRateDto heartRateDTO) {
        Long member_id = getMemberId(email);
        Long fixture_id = heartRateDTO.getMatchId();

        // 중복 아닐 때만 저장
        if(heartRateRepository.findByMemberIdAndFixtureId(member_id,fixture_id).isEmpty()){
            for (HeartRateDto.MatchHeartRateRecords records : heartRateDTO.getMatchHeartRateRecords()) {
                HeartRate heartRate = new HeartRate(member_id, heartRateDTO.getMatchId(), records.getHeartRate(), records.getDate());
                heartRateRepository.save(heartRate);
            }
        }

    }

    public boolean isExist(String email, Long fixtureId) {
        Long memberId = getMemberId(email);

        List<HeartRate> getHeartRate = heartRateRepository.findByMemberIdAndFixtureId(memberId, fixtureId);

        if (getHeartRate.isEmpty()) {
            return false;
        } else {
            return true;
        }

    }

}
