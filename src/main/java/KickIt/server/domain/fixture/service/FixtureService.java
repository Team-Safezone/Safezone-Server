package KickIt.server.domain.fixture.service;

import KickIt.server.domain.fixture.dto.FixtureDto;
import KickIt.server.domain.fixture.entity.Fixture;
import KickIt.server.domain.fixture.entity.FixtureRepository;
import KickIt.server.domain.teams.EplTeams;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class FixtureService {
    @Autowired
    private FixtureRepository fixtureRepository;

    // fixture List 중 중복되지 않은 fixture만을 저장
    @Transactional
    public String saveFixtures(List<Fixture> fixtureList){
        for(Fixture fixture : fixtureList){
            if(!isFixtureExist(fixture)){
                fixtureRepository.save(fixture);
                System.out.println("해당 열 저장됨");
            }
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

    // findByDate로 가져온 List<Fixture>의 Fixture들 DTO의 Response 형태로 변환 후 반환
    @Transactional
    public List<FixtureDto.FixtureResponse> findFixturesByDate(Date date){
        List<Fixture> fixtureList = fixtureRepository.findByDate(new Timestamp(date.getTime()));
        List<FixtureDto.FixtureResponse> responseList = new ArrayList<>();
        for (Fixture fixture : fixtureList){
            responseList.add(new FixtureDto.FixtureResponse(fixture));
        }
        return responseList;
    }

    // findByDateAndTeam으로 가져온 List<Fixture>의 Fixture들 DTO의 Response 형태로 변환 후 반환
    @Transactional
    public List<FixtureDto.FixtureResponse> findFixturesByDateAndTeam(Date date, String teamName){
        EplTeams team = EplTeams.valueOfKrName(teamName);
        List<Fixture> fixtureList = fixtureRepository.findByDateAndTeam(new Timestamp(date.getTime()), team);
        List<FixtureDto.FixtureResponse> responseList = new ArrayList<>();
        for (Fixture fixture: fixtureList){
            responseList.add(new FixtureDto.FixtureResponse(fixture));
        }
        return responseList;
    }
}
