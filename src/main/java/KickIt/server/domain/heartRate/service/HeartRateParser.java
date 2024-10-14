package KickIt.server.domain.heartRate.service;

import KickIt.server.domain.fixture.entity.FixtureRepository;
import KickIt.server.domain.heartRate.dto.HeartRateRepository;
import KickIt.server.domain.member.dto.MemberRepository;
import KickIt.server.domain.teams.service.TeamNameConvertService;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

// 사용자 심박수 최소, 최대 계산 클래스
@Service
public class HeartRateParser {

    private final HeartRateRepository heartRateRepository;
    private final FixtureRepository fixtureRepository;
    private final MemberRepository memberRepository;
    private final TeamNameConvertService teamNameConvertService;

    @Autowired
    public HeartRateParser(HeartRateRepository heartRateRepository, FixtureRepository fixtureRepository, MemberRepository memberRepository, TeamNameConvertService teamNameConvertService) {
        this.heartRateRepository = heartRateRepository;
        this.fixtureRepository = fixtureRepository;
        this.memberRepository = memberRepository;
        this.teamNameConvertService = teamNameConvertService;
    }

    public List<Integer> minMax(Long memberId, Long fixtureId) {

        List<Integer> heartRate = heartRateRepository.getUserHeartRate(memberId, fixtureId);

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

    public List<Integer> minAvgMax(Long fixtureId) {
        List<Integer> bpm = heartRateRepository.getAllHeartRate(fixtureId);

        if (bpm == null || bpm.isEmpty()) {
            return Arrays.asList(0, 0, 0);
        }

        int min = bpm.get(0);
        int max = bpm.get(0);
        int sum = 0;

        for (Integer i : bpm) {
            if (min > i) {
                min = i;
            }
            if (max < i) {
                max = i;
            }
            sum += i;
        }

        int avg = sum / bpm.size();

        List<Integer> minavgmaxList = new ArrayList<>();
        minavgmaxList.add(min);
        minavgmaxList.add(avg);
        minavgmaxList.add(max);

        return minavgmaxList;
    }

    public String getTeamType(Long memberId, Long fixtureId) {
        List<Object[]> homeawayTeam = fixtureRepository.findHomeAwayTeam(fixtureId);
        Object[] teams = homeawayTeam.get(0);

        String home = teamNameConvertService.convertToKrName((String) teams[0]);
        String away = teamNameConvertService.convertToKrName((String) teams[1]);

        List<Object[]> memberFavoriteTeams = memberRepository.getFavoriteTeam(memberId);
        Object[] memberTeams = homeawayTeam.get(0);
        String team1 = (String) memberTeams[0];
        String team2 = (String) memberTeams[1];
        String team3 = (String) memberTeams[2];

        if (team1.equals(home)) {
            return "home";
        } else if (team1.equals(away)) {
            return "away";
        } else if (team2.equals(home)) {
            return "home";
        } else if (team2.equals(away)) {
            return "away";
        } else if (team3.equals(home)) {
            return "home";
        } else if (team3.equals(away)) {
            return "away";
        }

        return "해당 경기에 선호하는 팀이 없습니다.";
    }

}
