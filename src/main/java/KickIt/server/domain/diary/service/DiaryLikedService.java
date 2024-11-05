package KickIt.server.domain.diary.service;

import KickIt.server.domain.diary.entity.Diary;
import KickIt.server.domain.diary.entity.DiaryLiked;
import KickIt.server.domain.diary.entity.DiaryLikedRepository;
import KickIt.server.domain.diary.entity.DiaryRepository;
import KickIt.server.domain.member.entity.Member;
import KickIt.server.domain.member.entity.MemberRepository;
import org.joda.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class DiaryLikedService {

    private final MemberRepository memberRepository;
    private final DiaryRepository diaryRepository;
    private final DiaryLikedRepository diaryLikedRepository;

    @Autowired
    public DiaryLikedService(MemberRepository memberRepository, DiaryRepository diaryRepository, DiaryLikedRepository diaryLikedRepository) {
        this.memberRepository = memberRepository;
        this.diaryRepository = diaryRepository;
        this.diaryLikedRepository = diaryLikedRepository;
    }

    public void saveLike(String email, Long diaryId, boolean islike) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));
        Diary diary = diaryRepository.findById(diaryId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 일기입니다."));

        Optional<DiaryLiked> like = diaryLikedRepository.findByMemberIdAndDiaryId(member.getId(), diaryId);

        if (islike) {
            // 좋아요 추가
            if (like.isEmpty()) {
                // 객체 생성
                DiaryLiked diaryLiked = DiaryLiked.builder()
                        .member(member)
                        .diary(diary)
                        .build();
                diaryLikedRepository.save(diaryLiked);
            }
        } else {
            // 좋아요 취소
            if (like.isPresent()) {
                // 기존 좋아요 삭제
                DiaryLiked existingLike = like.get();
                diaryLikedRepository.delete(existingLike);
            }
        }
    }
}
