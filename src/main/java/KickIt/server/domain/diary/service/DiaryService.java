package KickIt.server.domain.diary.service;

import KickIt.server.aws.s3.service.ImageUploadService;
import KickIt.server.domain.diary.dto.DiarySaveDto;
import KickIt.server.domain.diary.entity.Diary;
import KickIt.server.domain.diary.entity.DiaryPhoto;
import KickIt.server.domain.diary.entity.DiaryRepository;
import KickIt.server.domain.fixture.entity.Fixture;
import KickIt.server.domain.fixture.entity.FixtureRepository;
import KickIt.server.domain.member.entity.Member;
import KickIt.server.domain.member.entity.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DiaryService {

    private final DiaryRepository diaryRepository;
    private final ImageUploadService imageUploadService;
    private final MemberRepository memberRepository;
    private final FixtureRepository fixtureRepository;

    @Autowired
    public DiaryService(DiaryRepository diaryRepository, ImageUploadService imageUploadService, MemberRepository memberRepository, FixtureRepository fixtureRepository) {
        this.diaryRepository = diaryRepository;
        this.imageUploadService = imageUploadService;
        this.memberRepository = memberRepository;
        this.fixtureRepository = fixtureRepository;
    }

    public void save(DiarySaveDto diarySaveDto, String email) throws IOException {
        // Member 및 Fixture 객체 가져오기
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));
        Fixture fixture = fixtureRepository.findById(diarySaveDto.getFixtureId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 경기입니다."));

        // Diary 엔티티 생성
        Diary diary = Diary.builder()
                .member(member)
                .fixture(fixture)
                .teamName(diarySaveDto.getTeamName())
                .emotion(diarySaveDto.getEmotion())
                .diaryContext(diarySaveDto.getDiaryContext())
                .mom(diarySaveDto.getMom())
                .isPublic(diarySaveDto.isPublic())
                .build();

        // S3에 파일 업로드하고 URL 리스트 생성
        List<String> photoUrls = diarySaveDto.getDiaryPhotos().stream()
                .map(fileName -> {
                    try {
                        return imageUploadService.upload(fileName); // 업로드 후 URL 반환
                    } catch (IOException e) {
                        throw new RuntimeException("파일 업로드 중 오류 발생: " + fileName, e); // 예외를 런타임 예외로 변환
                    }
                })
                .collect(Collectors.toList());

        // DiaryPhoto 엔티티 리스트 생성 및 Diary와 연관 설정
        List<DiaryPhoto> diaryPhotos = photoUrls.stream()
                .map(photoUrl -> DiaryPhoto.builder()
                        .photoUrl(photoUrl) // S3에 저장된 URL
                        .diary(diary)       // Diary와 연관 관계 설정
                        .build())
                .collect(Collectors.toList());

        // Diary 엔티티에 DiaryPhoto 리스트 설정
        diary.setDiaryPhotos(diaryPhotos);

        // Diary 저장
        diaryRepository.save(diary);

    }
}
