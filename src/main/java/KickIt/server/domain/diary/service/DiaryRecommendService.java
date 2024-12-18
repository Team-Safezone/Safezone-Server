package KickIt.server.domain.diary.service;

import KickIt.server.domain.diary.dto.DiaryRecommendDto;
import KickIt.server.domain.diary.dto.MyDiaryDto;
import KickIt.server.domain.diary.entity.Diary;
import KickIt.server.domain.diary.entity.DiaryRepository;
import KickIt.server.domain.fixture.entity.Fixture;
import KickIt.server.domain.member.entity.Member;
import KickIt.server.domain.member.entity.MemberRepository;
import KickIt.server.domain.teams.service.TeamNameConvertService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Service
public class DiaryRecommendService {

    private final DiaryDataParser diaryDataParser;
    private final TeamNameConvertService teamNameConvertService;
    private final MemberRepository memberRepository;
    private final DiaryRepository diaryRepository;

    @Autowired
    public DiaryRecommendService(DiaryDataParser diaryDataParser, TeamNameConvertService teamNameConvertService, MemberRepository memberRepository, DiaryRepository diaryRepository) {
        this.diaryDataParser = diaryDataParser;
        this.teamNameConvertService = teamNameConvertService;
        this.memberRepository = memberRepository;
        this.diaryRepository = diaryRepository;
    }

    public List<DiaryRecommendDto> getRecommendDiary(String email, int requestNum) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        LocalDateTime sevenDaysAgo = LocalDateTime.now().minus(7, ChronoUnit.DAYS);

        Pageable pageable = PageRequest.of(requestNum, 10);

        List<DiaryRecommendDto> diaryRecommendDtos = new ArrayList<>();

        List<String> preferredTeams = new ArrayList<>();
        preferredTeams.add(member.getTeam1());
        if(member.getTeam2() != null && !member.getTeam2().isEmpty()) {
            preferredTeams.add(member.getTeam2());
        }
        if(member.getTeam3() != null && !member.getTeam3().isEmpty()) {
            preferredTeams.add(member.getTeam3());
        }

        Page<Diary> recommendDiary = diaryRepository.getRecommendDiary(preferredTeams, sevenDaysAgo, pageable);

        // 추천 축구 일기 존재
        if (recommendDiary.hasContent()) {
            System.out.println("추천 축구 일기 존재");
            for (Diary diary : recommendDiary) {
                DiaryRecommendDto diaryRecommendDto = recommendDto(email, diary);
                diaryRecommendDtos.add(diaryRecommendDto);
            }

            return diaryRecommendDtos;

        }  else {
            System.out.println("추천 축구 일기 존재X");
            // 추천 축구 일기 존재 X
            Page<Diary> generalDiary = diaryRepository.getDiary(sevenDaysAgo, pageable);

            for (Diary diary : generalDiary) {
                DiaryRecommendDto diaryRecommendDto = recommendDto(email, diary);
                diaryRecommendDtos.add(diaryRecommendDto);

            }
            return diaryRecommendDtos;
        }

    }

    public DiaryRecommendDto recommendDto(String email, Diary diary) {
        Long memberId = getMemberId(email);
        DiaryRecommendDto diaryRecommendDto = new DiaryRecommendDto();
        diaryRecommendDto.setDiaryId(diary.getId());
        diaryRecommendDto.setTeamName(diary.getTeamName());
        diaryRecommendDto.setTeamUrl(diaryDataParser.teamUrl(diary.getTeamName()));
        diaryRecommendDto.setDiaryDate(diaryDataParser.getDiaryDate(diary));
        diaryRecommendDto.setNickname(getNickName(diary.getMember().getId()));
        diaryRecommendDto.setLikes(diary.getLikeCount());
        diaryRecommendDto.setEmotion(diary.getEmotion());
        diaryRecommendDto.setMom(diary.getMom());
        diaryRecommendDto.setDiaryContent(diary.getDiaryContent());
        if (diary.getMember().getId().equals(memberId)) {
            diaryRecommendDto.setIsMine(true);
        } else {
            diaryRecommendDto.setIsMine(false);
        }

        diaryRecommendDto.setDiaryPhotos(diaryDataParser.getPhotos(diary.getId()));

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
        diaryRecommendDto.setMatchDate(diaryDataParser.getMatchTime(fixture.getDate()));
        diaryRecommendDto.setHomeTeamName(homeTeam);
        diaryRecommendDto.setHomeTeamScore(homeScore);
        diaryRecommendDto.setAwayTeamName(awayTeam);
        diaryRecommendDto.setAwayTeamScore(awayScore);

        diaryRecommendDto.setHighHeartRate(diaryDataParser.getHeartRate(email, fixture.getId()));

        diaryRecommendDto.setIsLiked(diaryDataParser.getIsLiked(email, diary.getId()));

        return diaryRecommendDto;
    }

    public String getNickName(Long memberId) {
        return memberRepository.getNickname(memberId);
    }

    public Long getMemberId(String email) {
        return memberRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("해당 이메일을 가진 사용자가 없습니다."))
                .getId();
    }
}
