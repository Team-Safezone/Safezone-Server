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
public interface DiaryPhotoRepository extends JpaRepository<DiaryPhoto, Long> {

    @Query("SELECT dp.photoUrl FROM DiaryPhoto dp WHERE dp.diary.id = :diaryId")
    List<String> findPhotoUrlByDiaryId(@Param("diaryId") Long diaryId);

    // delete 하기 위해 다이어리Id && photoUrl 같은 객체 찾기
    DiaryPhoto findByDiaryIdAndPhotoUrl(Long diaryId, String photoUrl);

}
