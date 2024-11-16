package KickIt.server.domain.diary.service;

import KickIt.server.domain.diary.dto.MyDiaryDto;
import KickIt.server.domain.diary.entity.Diary;
import KickIt.server.domain.fixture.entity.Fixture;
import KickIt.server.domain.teams.service.TeamNameConvertService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class MyDiaryService {

    private final DiaryDataParser diaryDataParser;
    private final TeamNameConvertService teamNameConvertService;

    @Autowired
    public MyDiaryService(DiaryDataParser diaryDataParser, TeamNameConvertService teamNameConvertService) {
        this.diaryDataParser = diaryDataParser;
        this.teamNameConvertService = teamNameConvertService;
    }

    public List<MyDiaryDto> getMyDiary(String email) {
        List<MyDiaryDto> myDiaryDtos = new ArrayList<>();

        List<Diary> myDiary = diaryDataParser.getDiaryInfo(email);

        for (Diary diary : myDiary) {
            MyDiaryDto myDiaryDto = new MyDiaryDto();
            myDiaryDto.setDiaryId(diary.getId());
            myDiaryDto.setTeamName(diary.getTeamName());
            myDiaryDto.setTeamUrl(diaryDataParser.teamUrl(diary.getTeamName()));
            myDiaryDto.setIsPublic(diary.getIsPublic());
            myDiaryDto.setDiaryDate(diaryDataParser.getDiaryDate(diary));
            myDiaryDto.setLikes(diary.getLikeCount());
            myDiaryDto.setEmotion(diary.getEmotion());
            myDiaryDto.setMom(diary.getMom());
            myDiaryDto.setDiaryContent(diary.getDiaryContent());

            myDiaryDto.setDiaryPhotos(diaryDataParser.getPhotos(diary.getId()));

            Fixture fixture = diary.getFixture();
            String homeTeam = teamNameConvertService.convertToKrName(fixture.getHomeTeam());
            String awayTeam = teamNameConvertService.convertToKrName(fixture.getAwayTeam());
            Integer homeScore = fixture.getHomeTeamScore();
            Integer awayScore = fixture.getAwayteamScore();
            if(homeTeam == null){
                homeTeam = "홈팀";
            }
            if (awayTeam == null) {
                awayTeam = "어웨이팀";
            }
            if (homeScore == null) {
                homeScore = 100;
            }
            if (awayScore == null) {
                awayScore = 100;
            }
            myDiaryDto.setMatchDate(diaryDataParser.getMatchTime(fixture.getDate()));
            myDiaryDto.setHomeTeamName(homeTeam);
            myDiaryDto.setHomeTeamScore(homeScore);
            myDiaryDto.setAwayTeamName(awayTeam);
            myDiaryDto.setAwayTeamScore(awayScore);

            myDiaryDto.setHighHeartRate(diaryDataParser.getHeartRate(email, fixture.getId()));

            myDiaryDto.setIsLiked(diaryDataParser.getIsLiked(email, diary.getId()));

            myDiaryDtos.add(myDiaryDto);
        }
        return myDiaryDtos;
    }

}
