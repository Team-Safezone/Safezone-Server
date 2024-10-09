package KickIt.server.domain.heartRate.dto;

import KickIt.server.domain.heartRate.entity.HeartRateStatistics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface HeartRateStatisticsRepository extends JpaRepository<HeartRateStatistics, Long> {

    // 경기 시작시간 없데이트
    @Query("UPDATE HeartRateStatistics SET startDate = :startDate, endDate = :endDate WHERE memberId = :memberId AND fixtureId = :fixtureId")
    void updateTime(@Param("memberId") Long memberId, @Param("fixtureId") Long fixtureId, @Param("min") int min, @Param("max") int max);

    // 사용자 min,max 심박수 업데이트
    @Query("UPDATE HeartRateStatistics SET lowHeartRate = :min, highHeartRate = :max WHERE memberId = :memberId AND fixtureId = :fixtureId")
    void updateHeartRate(@Param("memberId") Long memberId, @Param("fixtureId") Long fixtureId, @Param("min") int min, @Param("max") int max);

}
