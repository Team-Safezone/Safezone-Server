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

    // fixture List 중 중복되지 않은 fixture만을 저장
    @Transactional
    public String saveFixtures(List<Fixture> fixtureList){
        try{
            for(Fixture fixture : fixtureList){
                if(!isFixtureExist(fixture)){
                    fixtureRepository.save(fixture);
                    System.out.println("해당 열 저장됨");
                }
            }
        }
        catch (Exception e){
            System.out.println("해당 열 저장 실패" + e.toString());
        }
        return "저장 완료";
    }

    // fixture가 중복되지 않은 경우 저장
    @Transactional
    public String saveFixture(Fixture fixture){
        if(!isFixtureExist(fixture)){
            fixtureRepository.save(fixture);
        }
        return "저장 완료";
    }

    // fixture의 중복 여부를 검사
    private boolean isFixtureExist(Fixture fixture){
        return fixtureRepository.findByDateAndHomeTeamAndAwayTeam(fixture.getDate(), fixture.getHomeTeam(), fixture.getAwayTeam()).isPresent();
    }
}
