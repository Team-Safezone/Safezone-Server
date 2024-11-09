package KickIt.server.domain.heartRate.entity;

import KickIt.server.domain.heartRate.dto.HeartRateDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TeamHeartRateRepository extends JpaRepository<TeamHeartRate, Long> {

    // 홈팀, 어웨이팀 심박수 리스트 가져오기
    @Query("SELECT hr.heartRateDate, hr.heartRate " +
            "FROM HeartRateStatistics AS hst " +
            "JOIN HeartRate AS hr " +
            "ON hst.member.id = hr.member.id AND hst.fixture.id = hr.fixture.id " +
            "WHERE hst.fixture.id = :fixtureId AND hst.teamType = :teamType")
    List<Object[]> getHeartRateRecords(@Param("fixtureId") Long fixtureId, @Param("teamType") String teamType);


    List<TeamHeartRate> findByFixtureIdAndTeamType(Long fixtureId, String teamType);

    // 팀 별 통계 심박수 리스트 API
    @Query("SELECT new KickIt.server.domain.heartRate.dto.HeartRateDto$MatchHeartRateRecords(h.heartRateDate, h.heartRate) " +
            "FROM TeamHeartRate h " +
            "WHERE h.fixture.id = :fixtureId AND h.teamType = :teamType")
    List<HeartRateDto.MatchHeartRateRecords> getHomeAwayTeamHeartRate(@Param("fixtureId") Long fixtureId, @Param("teamType") String teamType);

}
