package KickIt.server.domain.diary.entity;

import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface DiaryRepository extends JpaRepository<Diary, Long> {

    @Modifying
    @Transactional
    @Query("UPDATE Diary d SET d.likeCount = CASE WHEN d.likeCount + :likeCount < 0 THEN 0 ELSE d.likeCount + :likeCount END WHERE d.id = :id")
    void editLike(@Param("id") Long id, @Param("likeCount") int likeCount);

    List<Diary> findByMemberId(Long memberId);

    // 추천 축구 일기
    // 1. isPublic = true
    // 2. 현재 시간 기준 일주일 이내 작성된 일기
    // 3. 사용자의 선호 팀 일기
    // 4. 좋아요 순
    @Query("SELECT d FROM Diary d " +
            "JOIN d.member m " +
            "WHERE m.id =:memberId " +
            "AND d.isPublic = true " +
            "AND d.createdAt >= :sevenDaysAgo " +
            "AND (d.teamName = m.team1 OR d.teamName = m.team2 OR d.teamName = m.team3) " +
            "ORDER BY d.likeCount DESC")
    List<Diary> getRecommendDiary(@Param("memberId") Long memberId, @Param("sevenDaysAgo") LocalDateTime sevenDaysAgo);


}
