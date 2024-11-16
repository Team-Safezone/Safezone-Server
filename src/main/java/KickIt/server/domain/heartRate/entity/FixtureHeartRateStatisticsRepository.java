package KickIt.server.domain.heartRate.entity;

import KickIt.server.domain.heartRate.entity.FixtureHeartRateStatistics;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FixtureHeartRateStatisticsRepository extends JpaRepository<FixtureHeartRateStatistics, Long> {

    @Modifying
    @Transactional
    @Query("UPDATE FixtureHeartRateStatistics fh SET fh.startDate = :startDate, fh.endDate = :endDate WHERE fh.fixture.id = :fixtureId")
    void updateTime(@Param("fixtureId") Long fixtureId, @Param("startDate") String startDate, @Param("endDate") String endDate);

    @Modifying
    @Transactional
    @Query("UPDATE FixtureHeartRateStatistics fh SET fh.minBPM = :minBPM, fh.avgBPM = :avgBPM, fh.maxBPM = :maxBPM WHERE fh.fixture.id = :fixtureId")
    void updateBPM(@Param("fixtureId") Long fixtureId, @Param("minBPM") int minBPM, @Param("avgBPM") int avgBPM, @Param("maxBPM") int maxBPM);

    List<FixtureHeartRateStatistics> findByFixtureId(Long fixtureId);

    // 팀 중 심박수 기록 사용자 수
    @Query("SELECT count(hh) " +
            "FROM HeartRateStatistics hh " +
            "WHERE hh.fixture.id = :fixtureId AND hh.teamType = :teamType")
    int teamUser(@Param("fixtureId") Long fixtureId, @Param("teamType") String teamType);

    @Modifying
    @Transactional
    @Query("UPDATE FixtureHeartRateStatistics fh SET fh.homeTeamViewerPercentage = :homeTeamViewerPercentage WHERE fh.fixture.id = :fixtureId")
    void updatePercent (@Param("fixtureId") Long fixtureId, @Param("homeTeamViewerPercentage") int homeTeamViewerPercentage);

}
