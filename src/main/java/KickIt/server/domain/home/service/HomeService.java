package KickIt.server.domain.home.service;

import KickIt.server.domain.fixture.entity.Fixture;
import KickIt.server.domain.fixture.entity.FixtureRepository;
import KickIt.server.domain.home.dto.HomeDto;
import KickIt.server.domain.member.entity.Member;
import KickIt.server.domain.teams.entity.SquadRepository;
import KickIt.server.domain.teams.service.TeamNameConvertService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

@Service
public class HomeService {
    @Autowired
    SquadRepository squadRepository;
    @Autowired
    FixtureRepository fixtureRepository;
    @Autowired
    TeamNameConvertService teamNameConvertService;

    @Transactional
    public HomeDto.homeResponse inquireHomeInfos(Member member){
        int gradePoint = member.getPoint(); // 사용자 point

        // 사용자 선호팀 가져와 list로 만듦
        List<String> favTeams = new ArrayList<>();
        if(member.getTeam1() != null){ favTeams.add(teamNameConvertService.convertFromKrName(member.getTeam1())); }
        if(member.getTeam2() != null){ favTeams.add(teamNameConvertService.convertFromKrName(member.getTeam2())); }
        if(member.getTeam3() != null){ favTeams.add(teamNameConvertService.convertFromKrName(member.getTeam3())); }

        // list에 있는 사용자 선호팀 Emblem URL 가져와 List<String>으로 저장
        List<String> favoriteImagesURL = new ArrayList<>();
        for(String team : favTeams){
            favoriteImagesURL.add(squadRepository.getUrl(team));
        }

        // 사용자 선호팀으로 사용자가 관심 있을 경기 일정 확인
        List<Fixture> favFixtures = fixtureRepository.findByFavTeams(favTeams);

        // 가져온 사용자 관심 경기 일정 DTO class 객체로 생성
        List<HomeDto.homeMatchInfo> homeMatchInfos = new ArrayList<>();
        for(Fixture fixture : favFixtures){
            homeMatchInfos.add(HomeDto.homeMatchInfo.builder()
                    .id(fixture.getId())
                    .homeTeamEmblemURL(squadRepository.getUrl(fixture.getHomeTeam()))
                    .awayTeamEmblemURL(squadRepository.getUrl(fixture.getAwayTeam()))
                    .homeTeamName(teamNameConvertService.convertToKrName(fixture.getHomeTeam()))
                    .awayTeamName(teamNameConvertService.convertToKrName(fixture.getAwayTeam()))
                    .matchDate(new SimpleDateFormat("yyyy-MM-dd").format(fixture.getDate()))
                    .matchTime(new SimpleDateFormat("HH:mm").format(fixture.getDate()))
                    .homeTeamScore(fixture.getHomeTeamScore())
                    .awayTeamScore(fixture.getAwayteamScore())
                    .round(fixture.getRound())
                    .status(fixture.getStatus())
                    .stadium(fixture.getStadium())
                    .build());
        }

        // 사용자 선호팀으로 사용자가 관심 있을 경기 예측 확인
        Fixture favFixtureForPrediction = switch (favTeams.size()){
            default -> fixtureRepository.findByFavTeamsAndPriorityWhen1(favTeams.get(0)).orElse(null);
            case 2 -> fixtureRepository.findByFavTeamsAndPriorityWhen2(favTeams, favTeams.get(0), favTeams.get(1)).orElse(null);
            case 3 -> fixtureRepository.findByFavTeamsAndPriorityWhen3(favTeams, favTeams.get(0), favTeams.get(1), favTeams.get(2)).orElse(null);
        };

        // 가져온 사용자 추천 우승팀 예측 경기 DTO class 객체로 생성
        HomeDto.homeMatchPredictionInfo homeMatchPredictionInfo = (favFixtureForPrediction == null) ? null
                : HomeDto.homeMatchPredictionInfo.builder()
                .id(favFixtureForPrediction.getId())
                .matchDate(new SimpleDateFormat("yyyy-MM-dd").format(favFixtureForPrediction.getDate()))
                .matchTime(new SimpleDateFormat("HH:mm").format(favFixtureForPrediction.getDate()))
                .homeTeamName(teamNameConvertService.convertToKrName(favFixtureForPrediction.getHomeTeam()))
                .awayTeamName(teamNameConvertService.convertToKrName(favFixtureForPrediction.getAwayTeam()))
                .homeTeamEmblemURL(squadRepository.getUrl(favFixtureForPrediction.getHomeTeam()))
                .awayTeamEmblemURL(squadRepository.getUrl(favFixtureForPrediction.getAwayTeam()))
                .build();

        return HomeDto.homeResponse.builder()
                .gradePoint(gradePoint)
                .matchPredictions(homeMatchPredictionInfo)
                .favoriteImagesURL(favoriteImagesURL)
                .matches(homeMatchInfos)
                .build();
    }
}
