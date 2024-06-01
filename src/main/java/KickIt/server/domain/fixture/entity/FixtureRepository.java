package KickIt.server.domain.fixture.entity;

import KickIt.server.domain.teams.EplTeams;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface FixtureRepository extends JpaRepository<Fixture, Long>{
    // Date, HomeTeam, AwayTeam이 동일한 Fixture DB에서 찾아 반환
    Optional<Fixture> findByDateAndHomeTeamAndAwayTeam(Timestamp date, EplTeams homeTeam , EplTeams awayTeam);

    // Date 중 시간은 버리고 연 / 월 / 일이 일치하는 Fixture DB에서 찾아 반환
    @Query("SELECT f FROM Fixture f WHERE DATE(f.date) = DATE(:date)")
    List<Fixture> findByDate(@Param("date") Timestamp date);
}
