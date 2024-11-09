package KickIt.server.domain.heartRate.service;

import KickIt.server.domain.fixture.entity.FixtureRepository;
import KickIt.server.domain.heartRate.dto.MinAvgMaxDto;
import KickIt.server.domain.member.entity.MemberRepository;
import KickIt.server.domain.teams.service.TeamNameConvertService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

// 사용자 심박수 최소, 최대 계산 클래스
@Service
public class HeartRateParser {

    private final FixtureRepository fixtureRepository;
    private final MemberRepository memberRepository;
    private final TeamNameConvertService teamNameConvertService;

    @Autowired
    public HeartRateParser(FixtureRepository fixtureRepository, MemberRepository memberRepository, TeamNameConvertService teamNameConvertService) {
        this.fixtureRepository = fixtureRepository;
        this.memberRepository = memberRepository;
        this.teamNameConvertService = teamNameConvertService;
    }


    public List<Integer> minAvgMaxInt(List<Integer> bpm) {

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

    public List<MinAvgMaxDto> minAvgMaxDto(List<Integer> bpm) {

        if (bpm == null || bpm.isEmpty()) {
            return Arrays.asList(new MinAvgMaxDto(0, 0, 0));
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

        List<MinAvgMaxDto> minavgmaxList = new ArrayList<>();
        minavgmaxList.add(new MinAvgMaxDto(min, avg, max));

        return minavgmaxList;
    }


    public int avgInt(List<Integer> bpm) {
        int avg = 0;
        int sum = 0;

        for (Integer i : bpm) {
            sum += i;
        }

        if(!bpm.isEmpty()){
            avg = sum/bpm.size();
        }

        return avg;
    }


    public List<Object[]> avgObject(List<Object[]> bpm) {

        // 1. heartRateDate 값을 기준으로 그룹화
        Map<Integer, List<Integer>> groupedByHeartRateDate = bpm.stream()
                .collect(Collectors.groupingBy(
                        record -> (Integer) record[0],
                        Collectors.mapping(record -> (Integer) record[1], Collectors.toList())
                ));

        // 2. 평균 계산 및 결과 리스트 생성
        List<Object[]> result = new ArrayList<>();
        for (Map.Entry<Integer, List<Integer>> entry : groupedByHeartRateDate.entrySet()) {
            Integer heartRateDate = entry.getKey();
            List<Integer> heartRates = entry.getValue();

            // 0이 아닌 heartRate 값들만 사용하여 평균 계산
            double averageHeartRate = heartRates.stream()
                    .filter(rate -> rate != 0) // 0인 값 제외
                    .mapToInt(Integer::intValue)
                    .average()
                    .orElse(0.0);

            // 결과 리스트에 heartRateDate와 평균 heartRate 추가
            result.add(new Object[]{heartRateDate, (int) averageHeartRate});
        }

        return result;
    }


    public String getTeamType(Long memberId, Long fixtureId) {
        String home;
        String away;

        // 경기 아이디로 홈팀 어웨이팀 파악
        List<Object[]> homeawayTeam = fixtureRepository.findHomeAwayTeam(fixtureId);

        if (homeawayTeam.isEmpty()) {
            // homeawayTeam이 비어 있을 경우 처리
            throw new IllegalArgumentException("해당 fixture에 대한 팀 정보가 없습니다.");
        } else {
            Object[] teams = homeawayTeam.get(0);
            home = teamNameConvertService.convertToKrName((String) teams[0]);
            away = teamNameConvertService.convertToKrName((String) teams[1]);
        }

        List<Object[]> memberFavoriteTeams = memberRepository.getFavoriteTeam(memberId);

        if (memberFavoriteTeams.isEmpty()) {
            // memberFavoriteTeams가 비어 있을 경우 처리
            throw new IllegalArgumentException("해당 멤버의 좋아하는 팀 정보가 없습니다.");
        }

        Object[] memberTeams = memberFavoriteTeams.get(0);

        for (Object memberTeam : memberTeams) {
            if (memberTeam.equals(home)) {
                return "home";
            } else if (memberTeam.equals(away)) {
                return "away";
            }
        }

        return "others";
    }

    /*
    public List<Integer> teamAll(Long fixtureId) {
        String home;
        String away;

        int homeTeamCount = 0;
        int awayTeamCount = 0;
        int otherCount = 0;

        List<String> homeawayTeam = fixtureRepository.findHomeAwayTeam(fixtureId);

        if (homeawayTeam.isEmpty()) {
            // homeawayTeam이 비어 있을 경우 처리
            throw new IllegalArgumentException("해당 fixture에 대한 팀 정보가 없습니다.");
        } else {
            String teams = homeawayTeam.get(0);
            home = teamNameConvertService.convertToKrName((String) teams[0]);
            away = teamNameConvertService.convertToKrName((String) teams[1]);
        }


        List<Object[]> memberFavoriteTeams = memberRepository.getFavoriteTeamAll();

        if (memberFavoriteTeams.isEmpty()) {
            // memberFavoriteTeams가 비어 있을 경우 처리
            throw new IllegalArgumentException("해당 멤버의 좋아하는 팀 정보가 없습니다.");
        }


        for (Object[] memberTeam : memberFavoriteTeams) {
            for (Object team : memberTeam) {
                if (team != null) {
                    if (team.equals(home)) {
                        homeTeamCount++;
                        break;
                    } else if (team.equals(away)) {
                        awayTeamCount++;
                        break;
                    }
                }
            }
        }

        List<Integer> count = new ArrayList<>();
        count.add(homeTeamCount);
        count.add(awayTeamCount);

        return count;
    }

     */

}
