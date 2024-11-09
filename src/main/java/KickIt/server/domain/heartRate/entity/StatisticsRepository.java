package KickIt.server.domain.heartRate.entity;

import KickIt.server.domain.heartRate.dto.HeartRateDto;
import KickIt.server.domain.heartRate.dto.RealTimeStatisticsDto;
import KickIt.server.domain.heartRate.dto.StatisticsDto;
import KickIt.server.domain.heartRate.entity.HeartRateStatistics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StatisticsRepository extends JpaRepository<HeartRateStatistics, Long> {

    // 통계 변수들 리스트 API
    @Query("SELECT new KickIt.server.domain.heartRate.dto.StatisticsDto(f.startDate, f.endDate, h.lowHeartRate, h.highHeartRate, f.minBPM, f.avgBPM, f.maxBPM, f.homeTeamViewerPercentage) " +
            "FROM HeartRateStatistics h " +
            "JOIN FixtureHeartRateStatistics f ON h.fixture.id = f.fixture.id " +
            "WHERE h.member.id = :memberId AND f.fixture.id = :fixtureId")
    List<StatisticsDto> findJoinedData(@Param("memberId") Long memberId, @Param("fixtureId") Long fixtureId);

    // 통계 이벤트 리스트 API
    @Query("SELECT new KickIt.server.domain.heartRate.dto.RealTimeStatisticsDto(r.teamUrl, r.eventName, r.time, r.player1) "+
            "FROM RealTime r WHERE r.fixture.id =:fixtureId")
    List<RealTimeStatisticsDto> getRealTimeStatistics(@Param("fixtureId") Long fixtureId);

    // 통계 homeTeam 심박수 리스트 API
    @Query("SELECT new KickIt.server.domain.heartRate.dto.HeartRateDto$MatchHeartRateRecords(hh.heartRate, hh.heartRateDate) " +
            "FROM HeartRateStatistics h " +
            "JOIN HeartRate hh ON h.member.id = hh.member.id " +
            "WHERE h.fixture.id = :fixtureId AND h.teamType = :teamType")
    List<HeartRateDto.MatchHeartRateRecords> getHomeAwayTeamHeartRate(@Param("fixtureId") Long fixtureId, @Param("teamType") String teamType);

    @Query("SELECT h.heartRate " +
            "FROM HeartRate h " +
            "JOIN HeartRateStatistics hh ON  h.member.id = hh.member.id " +
            "WHERE hh.fixture.id = :fixtureId AND hh.teamType = :teamType")
    List<Integer> getHeartRate(@Param("fixtureId") Long fixtureId, @Param("teamType") String teamType);




}
