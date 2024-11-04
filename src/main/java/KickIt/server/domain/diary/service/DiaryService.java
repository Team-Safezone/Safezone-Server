package KickIt.server.domain.diary.service;

import KickIt.server.aws.s3.service.S3Service;
import KickIt.server.domain.diary.dto.DiarySaveDto;
import KickIt.server.domain.diary.entity.Diary;
import KickIt.server.domain.diary.entity.DiaryPhoto;
import KickIt.server.domain.diary.entity.DiaryRepository;
import KickIt.server.domain.fixture.entity.Fixture;
import KickIt.server.domain.fixture.entity.FixtureRepository;
import KickIt.server.domain.member.entity.Member;
import KickIt.server.domain.member.entity.MemberRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class DiaryService {


    private final DiaryRepository diaryRepository;
    private final MemberRepository memberRepository;
    private final FixtureRepository fixtureRepository;
    private final DiaryPhotoService diaryPhotoService;
    private final S3Service s3Service;

    @Autowired
    public DiaryService(DiaryRepository diaryRepository, MemberRepository memberRepository, FixtureRepository fixtureRepository, DiaryPhotoService diaryPhotoService, S3Service s3Service) {
        this.diaryRepository = diaryRepository;
        this.memberRepository = memberRepository;
        this.fixtureRepository = fixtureRepository;
        this.diaryPhotoService = diaryPhotoService;
        this.s3Service = s3Service;
    }

    @Transactional
    public void save(DiarySaveDto diarySaveDto, String email) {
        // Member 및 Fixture 객체 가져오기
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));
        Fixture fixture = fixtureRepository.findById(diarySaveDto.getFixtureId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 경기입니다."));


        // Diary 엔티티 생성 및 저장
        Diary diary = Diary.builder()
                .member(member)
                .fixture(fixture)
                .teamName(diarySaveDto.getTeamName())
                .emotion(diarySaveDto.getEmotion())
                .diaryContext(diarySaveDto.getDiaryContext())
                .mom(diarySaveDto.getMom())
                .isPublic(diarySaveDto.isPublic())
                .build();
        diaryRepository.save(diary);

        // 사진 URL을 S3에 업로드하고 DiaryPhoto 엔티티 저장
        for (String photoUrl : diarySaveDto.getDiaryPhotos()) {
            try {
                String s3Url = s3Service.uploadFileFromUrl(photoUrl);
                DiaryPhoto diaryPhoto = diaryPhotoService.photoSave(s3Url, diary);
                diary.getDiaryPhotos().add(diaryPhoto);

            } catch (IOException e) {
                throw new RuntimeException("파일 업로드 중 오류 발생: " + e.getMessage());
            }
        }
    }


    @Transactional
    public void deleteDiary(Long diaryId) {
        Diary diary = diaryRepository.findById(diaryId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 일기입니다."));

        // DiaryPhoto 삭제
        for (DiaryPhoto diaryPhoto : diary.getDiaryPhotos()) {
            diaryPhotoService.deleteDiaryPhoto(diaryPhoto);
        }

        // Diary 삭제
        diaryRepository.delete(diary);
    }

}
