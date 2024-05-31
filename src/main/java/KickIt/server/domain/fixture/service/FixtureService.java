package KickIt.server.domain.fixture.service;

import KickIt.server.domain.fixture.entity.Fixture;
import KickIt.server.domain.fixture.entity.FixtureRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FixtureService {
    @Autowired
    private FixtureRepository fixtureRepository;

    @Transactional
    public String saveFixtures(List<Fixture> fixtureList){
        for(Fixture fixture : fixtureList){
            try{
                if(!isFixtureExist(fixture)){
                    fixtureRepository.save(fixture);
                    System.out.println("해당 열 저장됨");
                }
                else{
                    System.out.println("해당 열 저장 안됨");
                }
            }
            catch(Exception e){
                System.out.println(e);
            }
        }
        return "저장 완료";
    }

    @Transactional
    public String saveFixture(Fixture fixture){
        if(!isFixtureExist(fixture)){
            fixtureRepository.save(fixture);
            System.out.println("해당 열 저장됨");
            }
        else {
            System.out.println("해당 열 저장 안됨");
        }
        return "저장 완료";
    }

    private boolean isFixtureExist(Fixture fixture){
        try{
            System.out.println("exist 통과");
            return fixtureRepository.findByDateAndHomeTeamAndAwayTeam(
                    fixture.getDate(), fixture.getHomeTeam(), fixture.getAwayTeam()
            ).isPresent();
        }
        catch (Exception e){
            System.out.println(e);
            return fixtureRepository.findByDateAndHomeTeamAndAwayTeam(
                    fixture.getDate(), fixture.getHomeTeam(), fixture.getAwayTeam()
            ).isPresent();
        }
    }
}
