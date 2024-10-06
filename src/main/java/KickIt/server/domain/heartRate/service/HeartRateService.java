package KickIt.server.domain.heartRate.service;

import KickIt.server.domain.heartRate.dto.HeartRateDTO;
import KickIt.server.domain.heartRate.dto.HeartRateRepository;
import KickIt.server.domain.heartRate.entity.HeartRate;
import KickIt.server.domain.member.dto.MemberRepository;
import KickIt.server.domain.member.entity.Member;
import io.jsonwebtoken.Jwt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class HeartRateService {

    private final HeartRateRepository heartRateRepository;
    private final MemberRepository memberRepository;

    @Autowired
    public HeartRateService(HeartRateRepository heartRateRepository, MemberRepository memberRepository) {
        this.heartRateRepository = heartRateRepository;
        this.memberRepository = memberRepository;
    }

    public void save(String email, HeartRateDTO heartRateDTO) {
        Long member_id = memberRepository.findByEmail(email).get().getMemberId();

        for (HeartRateDTO.MatchHeartRateRecords records : heartRateDTO.getMatchHeartRateRecords()) {
            HeartRate heartRate = new HeartRate(member_id, heartRateDTO.getMatchId(), records.getHeartRate(), records.getDate());
            heartRateRepository.save(heartRate);
        }

    }

}
