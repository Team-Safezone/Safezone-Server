package KickIt.server.domain.heartRate.dto;

import KickIt.server.domain.heartRate.entity.HeartRateStatistics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StatisticsRepository extends JpaRepository<HeartRateStatistics, Long> {

    // 통계 변수들 리스트 API
    @Query("SELECT new KickIt.server.domain.heartRate.dto.StatisticsDto(f.startDate, f.endDate, h.lowHeartRate, h.highHeartRate, f.minBPM, f.avgBPM, f.maxBPM) " +
            "FROM HeartRateStatistics h " +
            "JOIN FixtureHeartRateStatistics f ON h.fixtureId = f.fixtureId " +
            "WHERE h.memberId = :memberId AND f.fixtureId = :fixtureId")
    List<StatisticsDto> findJoinedData(@Param("memberId") Long memberId, @Param("fixtureId") Long fixtureId);

    // 통계 이벤트 리스트 API
    @Query("SELECT new KickIt.server.domain.realtime.dto.RealTimeStatisticsDto(r.teamUrl, r.eventName, r.time, r.player1)"+
            "FROM RealTime r WHERE r.matchId =:matchId")
    List<StatisticsDto.RealTimeStatisticsDto> getRealTimeStatistics(@Param("matchId") Long matchId);

    // 통계 homeTeam 심박수 리스트 API
    @Query("SELECT new KickIt.server.domain.heartRateDto.MatchHeartRateRecords(hh.heartRate, hh.date)" +
            "FROM HeartRateStatistics h" +
            "JOIN HeartRate hh ON h.memberId = hh.memberId" +
            "WHERE h.fixtureId = :fixtureId AND h.teamType = :teamType")
    List<HeartRateDto.MatchHeartRateRecords> getHomeTeamHeartRate(@Param("fixtureId") Long fixtureId, @Param("teamType") String teamType);

}
