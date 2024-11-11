package KickIt.server.domain.fixture.service;

import KickIt.server.domain.fixture.dto.FixtureDto;
import KickIt.server.domain.fixture.entity.Fixture;
import KickIt.server.domain.fixture.entity.FixtureRepository;
import KickIt.server.domain.teams.service.SquadService;
import KickIt.server.domain.teams.service.TeamNameConvertService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class FixtureService {
    @Autowired
    private FixtureRepository fixtureRepository;
    @Autowired
    private FixtureDto fixtureDto;
    @Autowired
    private SquadService squadService;
    @Autowired
    private TeamNameConvertService teamNameConvertService;

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
            responseList.add(fixtureDto.new FixtureResponse(fixture));
        }
        return responseList;
    }

    // findByDateAndTeam으로 가져온 List<Fixture>의 Fixture들 DTO의 Response 형태로 변환 후 반환
    @Transactional
    public List<FixtureDto.FixtureResponse> findFixturesByDateAndTeam(Date date, String team){
        List<Fixture> fixtureList = fixtureRepository.findByDateAndTeam(new Timestamp(date.getTime()), team);
        List<FixtureDto.FixtureResponse> responseList = new ArrayList<>();
        for (Fixture fixture: fixtureList){
            responseList.add(fixtureDto.new FixtureResponse(fixture));
        }
        return responseList;
    }

    // 한달 경기 일정 조회 API에서 사용
    // findByMonth로 가져온 List<Fixture>의 Fixture들 DTO의 Response 형태로 변환 후 반환
    @Transactional
    public FixtureDto.FixtureDateResponse findFixturesByMonth(int year, int month){
        List<Fixture> fixtureList = fixtureRepository.findByMonth(year, month);
        if(fixtureList.isEmpty()){
            return null;
        }
        FixtureDto.FixtureDateResponse response = fixtureDto.new FixtureDateResponse(fixtureList, false);
        return response;
    }

    // 한달 경기 일정 조회 API에서 사용
    // findByMonthAndTeam으로 가져온 List<Fixture>의 Fixture들 DTO의 Response 형태로 변환 후 반환
    @Transactional
    public FixtureDto.FixtureDateResponse findFixtureByMonthAndTeam(int year, int month, String team){
        List<Fixture> fixtureList = fixtureRepository.findByMonthAndTeam(year, month, team);
        if(fixtureList.isEmpty()){
            return null;
        }
        FixtureDto.FixtureDateResponse response = fixtureDto.new FixtureDateResponse(fixtureList, true);
        return response;
    }

    @Transactional
    public boolean updateFixtureScore(Long fixtureId, Integer homeTeamScore, Integer awayTeamScore){
        Optional<Fixture> fixture = fixtureRepository.findById(fixtureId);
        if(fixture.isPresent()){
            Fixture updatedFixture = Fixture.builder()
                    .id(fixtureId)
                    .season(fixture.get().getSeason())
                    .date(fixture.get().getDate())
                    .homeTeam(fixture.get().getHomeTeam())
                    .awayTeam(fixture.get().getAwayTeam())
                    .homeTeamScore(homeTeamScore)
                    .awayteamScore(awayTeamScore)
                    .round(fixture.get().getRound())
                    .status(fixture.get().getStatus())
                    .stadium(fixture.get().getStadium())
                    .lineupUrl(fixture.get().getLineupUrl()).build();
            fixtureRepository.save(updatedFixture);
            return true;
        }
        else{
            return false;
        }
    }

    // 일기 선택을 위한 경기 일정 조회에서 사용
    // findByMonth로 가져온 해당 달의 Fixture들 DTO의 Response 형태로 변환 후 반환
    @Transactional
    public FixtureDto.DiaryFixtureResponse findDiaryFixturesByMonth(int year, int month){
        List<FixtureDto.DiaryFixture> diaryFixtures = new ArrayList<>();
        // 가져온 경기 리스트
        List<Fixture> fixtureList = fixtureRepository.findByMonth(year, month);

        Boolean isLeftExist; // 이전 달에 경기 존재하는지 여부
        Boolean isRightExist; // 다음 달에 경기 존재하는지 여부

        // 1월인 경우 이전 달 작년 12월로 변경해 조회
        if(month == 1){
            isLeftExist = !fixtureRepository.findByMonth(year-1, 12).isEmpty();
            isRightExist = !fixtureRepository.findByMonth(year, month+1).isEmpty();
        }
        // 12월인 경우 다음 달 내년 1월로 변경해 조회
        else if(month == 12){
            isLeftExist = !fixtureRepository.findByMonth(year, month-1).isEmpty();
            isRightExist = !fixtureRepository.findByMonth(year+1, 1).isEmpty();
        }
        else{
            isLeftExist = !fixtureRepository.findByMonth(year, month-1).isEmpty();
            isRightExist = !fixtureRepository.findByMonth(year, month+1).isEmpty();
        }
        // 조회되는 경기 없는 경우
        if(fixtureList.isEmpty()){
            return FixtureDto.DiaryFixtureResponse.builder()
                    .soccerTeamNames(null)
                    .matches(null)
                    .isLeftExist(isLeftExist)
                    .isRightExist(isRightExist)
                    .build();
        }
        // 조회된 경기 response class 형식대로 변경
        for(Fixture fixture : fixtureList){
            diaryFixtures.add(FixtureDto.DiaryFixture.builder()
                    .matchId(fixture.getId())
                    .matchDate(new SimpleDateFormat("yyyy-MM-dd").format(fixture.getDate()))
                    .matchTime(new SimpleDateFormat("HH:mm").format(fixture.getDate()))
                    .homeTeamEmblemURL(squadService.getTeamLogoImg(fixture.getSeason(), fixture.getHomeTeam()))
                    .awayTeamEmblemUrl(squadService.getTeamLogoImg(fixture.getSeason(), fixture.getAwayTeam()))
                    .homeTeamName(teamNameConvertService.convertToKrName(fixture.getHomeTeam()))
                    .awayTeamName(teamNameConvertService.convertToKrName(fixture.getAwayTeam()))
                    .homeTeamScore(fixture.getHomeTeamScore())
                    .awayTeamScore(fixture.getAwayteamScore())
                    .build());
        }
        return FixtureDto.DiaryFixtureResponse.builder()
                .soccerTeamNames(squadService.getSeasonSquads(fixtureList.get(0).getSeason()))
                .matches(diaryFixtures)
                .isLeftExist(isLeftExist)
                .isRightExist(isRightExist)
                .build();
    }

    // 일기 선택을 위한 경기 일정 조회에서 사용
    // findByMonthAndTeam으로 가져온 해당 달의 Fixture들 DTO의 Response 형태로 변환 후 반환
    @Transactional
    public FixtureDto.DiaryFixtureResponse findDiaryFixturesByMonthAndTeam(int year, int month, String team){
        List<FixtureDto.DiaryFixture> diaryFixtures = new ArrayList<>();
        // 가져온 경기 리스트
        List<Fixture> fixtureList = fixtureRepository.findByMonthAndTeam(year, month, team);

        Boolean isLeftExist; // 이전 달에 경기 존재하는지 여부
        Boolean isRightExist; // 다음 달에 경기 존재하는지 여부

        // 1월인 경우 이전 달 작년 12월로 변경해 조회
        if(month == 1){
            isLeftExist = !fixtureRepository.findByMonthAndTeam(year-1, 12, team).isEmpty();
            isRightExist = !fixtureRepository.findByMonthAndTeam(year, month+1, team).isEmpty();
        }
        // 12월인 경우 다음 달 내년 1월로 변경해 조회
        else if(month == 12){
            isLeftExist = !fixtureRepository.findByMonthAndTeam(year, month-1, team).isEmpty();
            isRightExist = !fixtureRepository.findByMonthAndTeam(year+1, 1, team).isEmpty();
        }
        else{
            isLeftExist = !fixtureRepository.findByMonthAndTeam(year, month-1, team).isEmpty();
            isRightExist = !fixtureRepository.findByMonthAndTeam(year, month+1, team).isEmpty();
        }

        // 조회되는 경기 없는 경우
        if(fixtureList.isEmpty()){
            return FixtureDto.DiaryFixtureResponse.builder()
                    .soccerTeamNames(null)
                    .matches(null)
                    .isLeftExist(isLeftExist)
                    .isRightExist(isRightExist)
                    .build();
        }
        // 조회된 경기 response class 형식대로 변경
        for(Fixture fixture : fixtureList){
            diaryFixtures.add(FixtureDto.DiaryFixture.builder()
                    .matchId(fixture.getId())
                    .matchDate(new SimpleDateFormat("yyyy-MM-dd").format(fixture.getDate()))
                    .matchTime(new SimpleDateFormat("HH:mm").format(fixture.getDate()))
                    .homeTeamEmblemURL(squadService.getTeamLogoImg(fixture.getSeason(), fixture.getHomeTeam()))
                    .awayTeamEmblemUrl(squadService.getTeamLogoImg(fixture.getSeason(), fixture.getAwayTeam()))
                    .homeTeamName(teamNameConvertService.convertToKrName(fixture.getHomeTeam()))
                    .awayTeamName(teamNameConvertService.convertToKrName(fixture.getAwayTeam()))
                    .homeTeamScore(fixture.getHomeTeamScore())
                    .awayTeamScore(fixture.getAwayteamScore())
                    .build());
        }
        return FixtureDto.DiaryFixtureResponse.builder()
                .soccerTeamNames(squadService.getSeasonSquads(fixtureList.get(0).getSeason()))
                .matches(diaryFixtures)
                .isLeftExist(isLeftExist)
                .isRightExist(isRightExist)
                .build();
    }

    public void updateFixtureStatus(Long fixtureId, int status) {
        fixtureRepository.updateStatus(fixtureId, status);
    }

}
