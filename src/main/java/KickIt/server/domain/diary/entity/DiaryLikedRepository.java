package KickIt.server.domain.diary.entity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface DiaryLikedRepository extends JpaRepository<DiaryLiked, Long> {

    Optional<DiaryLiked> findByMemberIdAndDiaryId(Long memberId, Long diaryId);

}
