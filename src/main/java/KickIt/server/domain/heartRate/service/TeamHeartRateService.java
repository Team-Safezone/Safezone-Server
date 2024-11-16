package KickIt.server.domain.heartRate.service;

import KickIt.server.domain.fixture.entity.Fixture;
import KickIt.server.domain.fixture.entity.FixtureRepository;
import KickIt.server.domain.heartRate.dto.HeartRateDto;
import KickIt.server.domain.heartRate.entity.HeartRateRepository;
import KickIt.server.domain.heartRate.entity.TeamHeartRate;
import KickIt.server.domain.heartRate.entity.TeamHeartRateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TeamHeartRateService {

    private final HeartRateParser heartRateParser;
    private final TeamHeartRateRepository teamHeartRateRepository;
    private final FixtureRepository fixtureRepository;

    @Autowired
    public TeamHeartRateService(HeartRateParser heartRateParser, TeamHeartRateRepository teamHeartRateRepository, FixtureRepository fixtureRepository) {
        this.heartRateParser = heartRateParser;
        this.teamHeartRateRepository = teamHeartRateRepository;
        this.fixtureRepository = fixtureRepository;
    }

    public void saveTeamMinAvgMax(Long fixtureId) {
        List<Object[]> homeTeam = teamHeartRateRepository.getHeartRateRecords(fixtureId, "home");
        List<Object[]> awayTeam = teamHeartRateRepository.getHeartRateRecords(fixtureId, "away");

        List<Object[]> homeTeamAvg = heartRateParser.avgObject(homeTeam);
        List<Object[]> awayTeamAvg = heartRateParser.avgObject(awayTeam);

        Fixture fixture = getFixture(fixtureId);

        if (teamHeartRateRepository.findByFixtureIdAndTeamType(fixtureId, "home").isEmpty()) {
            for (Object[] home : homeTeamAvg) {
                TeamHeartRate homeTeamHeartRate = new TeamHeartRate(fixture, "home", (Integer)home[0], (Integer)home[1]);
                teamHeartRateRepository.save(homeTeamHeartRate);
            }
        }
        if (teamHeartRateRepository.findByFixtureIdAndTeamType(fixtureId, "away").isEmpty()) {
            for (Object[] away : awayTeamAvg) {
                TeamHeartRate awayTeamHeartRate = new TeamHeartRate(fixture, "away", (Integer)away[0], (Integer)away[1]);
                teamHeartRateRepository.save(awayTeamHeartRate);
            }
        }
    }

    public Fixture getFixture(Long matchId) {
        // 경기 정보 조회
        Fixture fixture = fixtureRepository.findById(matchId)
                .orElseThrow(() -> new IllegalArgumentException("경기가 존재하지 않습니다."));

        return fixture;
    }

}
