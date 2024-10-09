package KickIt.server.domain.heartRate.service;

import KickIt.server.domain.heartRate.dto.HeartRateDTO;
import KickIt.server.domain.heartRate.dto.HeartRateRepository;
import KickIt.server.domain.heartRate.dto.HeartRateStatisticsRepository;
import KickIt.server.domain.heartRate.entity.HeartRate;
import KickIt.server.domain.heartRate.entity.HeartRateStatistics;
import KickIt.server.domain.member.dto.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class HeartRateService {

    private final HeartRateRepository heartRateRepository;
    private final MemberRepository memberRepository;
    private final HeartRateStatisticsRepository heartRateStatisticsRepository;

    @Autowired
    public HeartRateService(HeartRateRepository heartRateRepository, MemberRepository memberRepository, HeartRateStatisticsRepository heartRateStatisticsRepository) {
        this.heartRateRepository = heartRateRepository;
        this.memberRepository = memberRepository;
        this.heartRateStatisticsRepository = heartRateStatisticsRepository;
    }


    // 사용자 Id
    public Long getMemberId(String email) {
        return memberRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("해당 이메일을 가진 사용자가 없습니다."))
                .getMemberId();
    }

    public void save(String email, HeartRateDTO heartRateDTO) {
        Long member_id = getMemberId(email);

        for (HeartRateDTO.MatchHeartRateRecords records : heartRateDTO.getMatchHeartRateRecords()) {
            HeartRate heartRate = new HeartRate(member_id, heartRateDTO.getMatchId(), records.getHeartRate(), records.getDate());
            heartRateRepository.save(heartRate);
        }

    }

    public void saveStatistics(String email, HeartRateDTO heartRateDTO) {
        Long member_id = getMemberId(email);

        HeartRateStatistics heartRateStatistics = new HeartRateStatistics(member_id, heartRateDTO.getMatchId());
        heartRateStatisticsRepository.save(heartRateStatistics);
    }

}
