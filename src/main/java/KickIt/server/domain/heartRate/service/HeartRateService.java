package KickIt.server.domain.heartRate.service;

import KickIt.server.domain.fixture.entity.Fixture;
import KickIt.server.domain.fixture.entity.FixtureRepository;
import KickIt.server.domain.heartRate.dto.HeartRateDto;
import KickIt.server.domain.heartRate.entity.HeartRateRepository;
import KickIt.server.domain.heartRate.entity.HeartRateStatisticsRepository;
import KickIt.server.domain.heartRate.entity.HeartRate;
import KickIt.server.domain.member.entity.Member;
import KickIt.server.domain.member.entity.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HeartRateService {

    private final HeartRateRepository heartRateRepository;
    private final MemberRepository memberRepository;
    private final HeartRateParser heartRateParser;
    private final HeartRateStatisticsRepository heartRateStatisticsRepository;
    private final FixtureRepository fixtureRepository;

    @Autowired
    public HeartRateService(HeartRateRepository heartRateRepository, MemberRepository memberRepository, HeartRateParser heartRateParser, HeartRateStatisticsRepository heartRateStatisticsRepository, FixtureRepository fixtureRepository) {
        this.heartRateRepository = heartRateRepository;
        this.memberRepository = memberRepository;
        this.heartRateParser = heartRateParser;
        this.heartRateStatisticsRepository = heartRateStatisticsRepository;
        this.fixtureRepository = fixtureRepository;
    }

    // 사용자 Id
    public Long getMemberId(String email) {
        return memberRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("해당 이메일을 가진 사용자가 없습니다."))
                .getId();
    }

    // 심박수 저장
    public void save(String email, HeartRateDto heartRateDto) {
        Member member = getMember(email);
        Fixture fixture = getFixture(heartRateDto.getMatchId());

        // 중복 아닐 때만 저장
        if(heartRateRepository.findByMember_IdAndFixture_Id(member,fixture).isEmpty()){
            for (HeartRateDto.MatchHeartRateRecords records : heartRateDto.getMatchHeartRateRecords()) {
                HeartRate heartRate = new HeartRate(member, fixture, records.getHeartRate(), records.getDate());
                heartRateRepository.save(heartRate);
            }
        }

    }

    public boolean isExist(String email, Long matchId) {
        Member member = getMember(email);
        Fixture fixture = getFixture(matchId);

        List<HeartRate> getHeartRate = heartRateRepository.findByMember_IdAndFixture_Id(member, fixture);

        if (getHeartRate.isEmpty()) {
            return false;
        } else {
            return true;
        }

    }

    public Member getMember(String email) {
        // 이메일로 회원 정보 조회
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("회원이 존재하지 않습니다."));

        return member;
    }

    public Fixture getFixture(Long matchId) {
        // 경기 정보 조회
        Fixture fixture = fixtureRepository.findById(matchId)
                .orElseThrow(() -> new IllegalArgumentException("경기가 존재하지 않습니다."));

        return fixture;
    }

}
