package KickIt.server.domain.diary.entity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DiaryReportRepository extends JpaRepository<DiaryReport, Long> {

    @Query("SELECT dr FROM DiaryReport dr WHERE dr.member.id = :memberId AND dr.diary.id = :diaryId")
    DiaryReport getReport(@Param("memberId") Long memberId, @Param("diaryId") Long diaryId);


}
