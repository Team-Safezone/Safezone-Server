package KickIt.server.domain.heartRate.entity;

import KickIt.server.domain.heartRate.dto.MinAvgMaxDto;
import KickIt.server.domain.heartRate.entity.FixtureHeartRateStatistics;
import KickIt.server.domain.heartRate.entity.TeamHeartRateStatistics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TeamHeartRateStatisticsRepository extends JpaRepository<TeamHeartRateStatistics, Long> {

    // 중복 확인
    List<TeamHeartRateStatistics> findByFixtureId(Long fixtureId);


    List<TeamHeartRateStatistics> findByFixtureIdAndTeamType(@Param("fixtureId") Long fixtureId, @Param("teamType") String teamType);

    @Query("SELECT new KickIt.server.domain.heartRate.dto.MinAvgMaxDto(t.minBPM, t.avgBPM, t.maxBPM) " +
            "FROM TeamHeartRateStatistics t " +
            "WHERE t.fixtureId = :fixtureId AND t.teamType = :teamType")
    List<MinAvgMaxDto> getHeartRateMinAvgMax(@Param("fixtureId") Long fixtureId, @Param("teamType") String teamType);


}
