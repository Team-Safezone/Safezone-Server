package KickIt.server.domain.fixture.service;

import KickIt.server.domain.fixture.dto.FixtureDto;
import KickIt.server.domain.fixture.entity.Fixture;
import KickIt.server.domain.fixture.entity.FixtureRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.beans.Transient;
import java.util.List;

@Service
public class FixtureService {
    @Autowired
    private FixtureRepository fixtureRepository;

    @Transactional
    public String saveFixtures(List<Fixture> fixtureList){
        for(Fixture fixture : fixtureList){
            if(!isFixtureExist(fixture)){
                fixtureRepository.save(fixture);
            }
        }
        return "저장 완료";
    }

    private boolean isFixtureExist(Fixture fixture){
        return fixtureRepository.findByDateAndHomeTeamAndAwayTeam(
                fixture.getDate(), fixture.getHomeTeam(), fixture.getAwayTeam()
        ).isPresent();
    }
}
