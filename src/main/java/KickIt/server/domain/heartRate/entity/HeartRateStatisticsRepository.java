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
    @Query("UPDATE HeartRateStatistics SET lowHeartRate = :lowHeartRate, highHeartRate = :highHeartRate WHERE memberId = :memberId AND fixtureId = :fixtureId")
    void updateHeartRate(@Param("memberId") Long memberId, @Param("fixtureId") Long fixtureId, @Param("lowHeartRate") int lowHeartRate, @Param("highHeartRate") int highHeartRate);

    List<HeartRateStatistics> findByMemberIdAndFixtureId(Long memberId, Long fixtureId);

    @Modifying
    @Transactional
    @Query("UPDATE HeartRateStatistics SET teamType = :teamType WHERE memberId = :memberId AND fixtureId = :fixtureId")
    void updateTeamType(@Param("memberId") Long memberId, @Param("fixtureId") Long fixtureId, @Param("teamType") String teamType);
}
