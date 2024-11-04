package KickIt.server.domain.diary.entity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DiaryPhotoRepository extends JpaRepository<DiaryPhoto, Long> {
}
