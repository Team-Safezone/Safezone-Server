package KickIt.server.domain.diary.entity;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DiaryRepository extends JpaRepository<Diary, Long> {

    @Modifying
    @Transactional
    @Query("UPDATE Diary d SET d.likeCount = CASE WHEN d.likeCount + :likeCount < 0 THEN 0 ELSE d.likeCount + :likeCount END WHERE d.id = :id")
    void editLike(@Param("id") Long id, @Param("likeCount") int likeCount);

    List<Diary> findByMemberId(Long memberId);


}
