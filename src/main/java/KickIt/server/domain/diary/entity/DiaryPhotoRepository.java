package KickIt.server.domain.diary.entity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DiaryPhotoRepository extends JpaRepository<DiaryPhoto, Long> {

    @Query("SELECT dp.photoUrl FROM DiaryPhoto dp WHERE dp.diary.id = :diaryId")
    List<String> findPhotoUrlByDiaryId(@Param("diaryId") Long diaryId);
}
