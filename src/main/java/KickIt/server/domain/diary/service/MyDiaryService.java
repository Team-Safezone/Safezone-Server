package KickIt.server.domain.diary.service;

import KickIt.server.domain.diary.dto.MyDiaryDto;
import KickIt.server.domain.diary.entity.*;
import KickIt.server.domain.fixture.entity.Fixture;
import KickIt.server.domain.heartRate.entity.HeartRateStatisticsRepository;
import KickIt.server.domain.member.entity.Member;
import KickIt.server.domain.member.entity.MemberRepository;
import KickIt.server.domain.teams.entity.SquadRepository;
import KickIt.server.domain.teams.service.TeamNameConvertService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class MyDiaryService {


    private final DiaryRepository diaryRepository;
    private final MemberRepository memberRepository;
    private final DiaryLikedRepository diaryLikedRepository;
    private final DiaryPhotoRepository diaryPhotoRepository;
    private final HeartRateStatisticsRepository heartRateStatisticsRepository;
    private final TeamNameConvertService teamNameConvertService;
    private final SquadRepository squadRepository;

    @Autowired
    public MyDiaryService(DiaryRepository diaryRepository, MemberRepository memberRepository, DiaryLikedRepository diaryLikedRepository, DiaryPhotoRepository diaryPhotoRepository, HeartRateStatisticsRepository heartRateStatisticsRepository, TeamNameConvertService teamNameConvertService, SquadRepository squadRepository) {
        this.diaryRepository = diaryRepository;
        this.memberRepository = memberRepository;
        this.diaryLikedRepository = diaryLikedRepository;
        this.diaryPhotoRepository = diaryPhotoRepository;
        this.heartRateStatisticsRepository = heartRateStatisticsRepository;
        this.teamNameConvertService = teamNameConvertService;
        this.squadRepository = squadRepository;
    }

    public List<MyDiaryDto> getMyDiary(String email) {
        List<MyDiaryDto> myDiaryDtos = new ArrayList<>();

        List<Diary> myDiary = getDiaryInfo(email);

        for (Diary diary : myDiary) {
            MyDiaryDto myDiaryDto = new MyDiaryDto();
            myDiaryDto.setDiaryId(diary.getId());
            myDiaryDto.setTeamName(diary.getTeamName());
            myDiaryDto.setTeamUrl(teamUrl(diary.getTeamName()));
            myDiaryDto.setIsPublic(diary.isPublic());
            myDiaryDto.setDiaryDate(getDiaryDate(diary));
            myDiaryDto.setLikes(diary.getLikeCount());
            myDiaryDto.setEmotion(diary.getEmotion());
            myDiaryDto.setMom(diary.getMom());

            myDiaryDto.setDiaryPhotos(getPhotos(diary.getId()));

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
            myDiaryDto.setMatchDate(getMatchTime(fixture.getDate()));
            myDiaryDto.setHomeTeamName(homeTeam);
            myDiaryDto.setHomeTeamScore(homeScore);
            myDiaryDto.setAwayTeamName(awayTeam);
            myDiaryDto.setAwayTeamScore(awayScore);

            myDiaryDto.setHighHeartRate(getHeartRate(email, fixture.getId()));

            myDiaryDto.setIsLiked(getIsLiked(email, diary.getId()));

            myDiaryDtos.add(myDiaryDto);
        }
        return myDiaryDtos;
    }


    // 유저의 다이어리 가져오기
    public List<Diary> getDiaryInfo(String email) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        List<Diary> myDiary = diaryRepository.findByMemberId(member.getId());

        return myDiary;
    }

    // 일기 작성 시간 계산(현재 시간 - 작성 시간)
    public String getDiaryDate(Diary diary) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime createdAt = diary.getCreatedAt();

        Duration duration = Duration.between(createdAt, now);

        long seconds = duration.getSeconds();
        long minutes = duration.toMinutes();
        long hours = duration.toHours();
        long days = duration.toDays();

        StringBuilder elapsedTime = new StringBuilder();

        if (days > 0) {
            elapsedTime.append(days).append("일 전");
        } else if (hours > 0) {
            elapsedTime.append(hours).append("시간 전");
        } else if (minutes > 0) {
            elapsedTime.append(minutes).append("분 전");
        } else if (seconds > 0) {
            elapsedTime.append(seconds).append("초 전");
        } else {
            elapsedTime.append("방금 전");
        }

        return elapsedTime.toString().trim();
    }

    // 좋아요 여부
    public Boolean getIsLiked(String email, Long diaryId) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        Optional<DiaryLiked> isLiked = diaryLikedRepository.findByMemberIdAndDiaryId(member.getId(), diaryId);

        if (isLiked.isPresent()) {
            return true;
        } else {
            return false;
        }
    }


    // photos 가져오기
    public List<String> getPhotos(Long diaryId) {
        List<String> photoUrls = diaryPhotoRepository.findPhotoUrlByDiaryId(diaryId);

        return photoUrls != null ? photoUrls : new ArrayList<>();
    }

    // 경기 시간 TimeStamp to String
    public String getMatchTime(Timestamp timestamp) {
        LocalDate localDate = timestamp.toLocalDateTime().toLocalDate();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");

        return localDate.format(formatter);
    }

    public int getHeartRate(String email, Long fixtureId) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        Integer maxHeartRate = heartRateStatisticsRepository.getMaxHeartRate(member.getId(), fixtureId);
        return maxHeartRate != null ? maxHeartRate : 0;
    }

    public String teamUrl(String teamName) {
        String name = teamNameConvertService.convertFromKrName(teamName);

        return squadRepository.getUrl(name);
    }
}
