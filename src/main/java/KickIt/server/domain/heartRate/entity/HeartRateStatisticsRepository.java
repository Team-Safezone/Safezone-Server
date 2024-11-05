package KickIt.server.domain.heartRate.entity;

import KickIt.server.domain.heartRate.entity.HeartRateStatistics;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HeartRateStatisticsRepository extends JpaRepository<HeartRateStatistics, Long> {

    // 경기 시작시간 없데이트

    // 사용자 min,max 심박수 업데이트
    @Modifying
    @Transactional
    @Query("UPDATE HeartRateStatistics SET lowHeartRate = :lowHeartRate, averageRate = :averageRate, highHeartRate = :highHeartRate WHERE memberId = :memberId AND fixtureId = :fixtureId")
    void updateHeartRate(@Param("memberId") Long memberId, @Param("fixtureId") Long fixtureId, @Param("lowHeartRate") int lowHeartRate, @Param("averageRate") int averageRate, @Param("highHeartRate") int highHeartRate);

    List<HeartRateStatistics> findByMemberIdAndFixtureId(Long memberId, Long fixtureId);

    @Query("SELECT heartRate FROM HeartRate WHERE memberId = :memberId AND fixtureId = :fixtureId")
    List<Integer> getUserHeartRate(@Param("memberId") Long memberId, @Param("fixtureId") Long fixtureId);

    @Modifying
    @Transactional
    @Query("UPDATE HeartRateStatistics SET teamType = :teamType WHERE memberId = :memberId AND fixtureId = :fixtureId")
    void updateTeamType(@Param("memberId") Long memberId, @Param("fixtureId") Long fixtureId, @Param("teamType") String teamType);

    // 사용자의 심박수 평균값만 가져오기
    @Query("SELECT averageRate FROM HeartRateStatistics WHERE memberId =:memberId")
    List<Integer> getHeartRate(@Param("memberId") Long memberId);

    // 심박수 평균값 업데이트
    @Modifying
    @Transactional
    @Query("UPDATE Member m SET m.avgHeartRate = :avgHeartRate WHERE m.id = :id")
    void updateAvg(@Param("id") Long id, @Param("avgHeartRate") int avgHeartRate);

    @Query("SELECT highHeartRate FROM HeartRateStatistics WHERE memberId = :memberId AND fixtureId = :fixtureId")
    Integer getMaxHeartRate(@Param("memberId") Long memberId, @Param("fixtureId") Long fixtureId);
}
