package KickIt.server.domain.heartRate.service;

import KickIt.server.domain.heartRate.dto.HeartRateDto;
import KickIt.server.domain.heartRate.entity.HeartRateRepository;
import KickIt.server.domain.heartRate.entity.TeamHeartRate;
import KickIt.server.domain.heartRate.entity.TeamHeartRateRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TeamHeartRateService {

    private final HeartRateParser heartRateParser;
    private final TeamHeartRateRepository teamHeartRateRepository;

    public TeamHeartRateService(HeartRateParser heartRateParser, TeamHeartRateRepository teamHeartRateRepository) {
        this.heartRateParser = heartRateParser;
        this.teamHeartRateRepository = teamHeartRateRepository;
    }

    public void saveTeamMinAvgMax(HeartRateDto heartRateDto) {
        Long fixtureId = heartRateDto.getMatchId();

        List<Object[]> homeTeam = teamHeartRateRepository.getHeartRateRecords(fixtureId, "home");
        List<Object[]> awayTeam = teamHeartRateRepository.getHeartRateRecords(fixtureId, "away");

        List<Object[]> homeTeamAvg = heartRateParser.avgObject(homeTeam);
        List<Object[]> awayTeamAvg = heartRateParser.avgObject(awayTeam);

        if (teamHeartRateRepository.findByFixtureIdAndTeamType(fixtureId, "home").isEmpty()) {
            for (Object[] home : homeTeamAvg) {
                TeamHeartRate homeTeamHeartRate = new TeamHeartRate(fixtureId, "home", (Integer)home[0], (Integer)home[1]);
                teamHeartRateRepository.save(homeTeamHeartRate);
            }
        }
        if (teamHeartRateRepository.findByFixtureIdAndTeamType(fixtureId, "away").isEmpty()) {
            for (Object[] away : awayTeamAvg) {
                TeamHeartRate awayTeamHeartRate = new TeamHeartRate(fixtureId, "away", (Integer)away[0], (Integer)away[1]);
                teamHeartRateRepository.save(awayTeamHeartRate);
            }
        }
    }

}
