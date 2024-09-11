package KickIt.server.domain.fixture.entity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface FixtureRepository extends JpaRepository<Fixture, Long>{
    // Date, HomeTeam, AwayTeam이 동일한 Fixture DB에서 찾아 반환
    Optional<Fixture> findByDateAndHomeTeamAndAwayTeam(Timestamp date, String homeTeam , String awayTeam);

    // Date 중 시간은 버리고 연 / 월 / 일이 일치하는 Fixture DB에서 찾아 반환
    @Query("SELECT f FROM Fixture f WHERE DATE(f.date) = DATE(:date)")
    List<Fixture> findByDate(@Param("date") Timestamp date);

    // Date 중 시간은 버리고 연 / 월 / 일이 일치하고 HomeTeam이나 awayTeam이 team과 일치하는 Fixture DB에서 찾아 반환
    @Query("SELECT f FROM Fixture f WHERE DATE(f.date) = DATE(:date) AND (f.homeTeam = :team OR f.awayTeam = :team)")
    List<Fixture> findByDateAndTeam(@Param("date") Timestamp date, @Param("team") String team);

    // Date 중 시간은 버리고 연 / 월이 일치하는 Fixture DB에서 찾아 반환
    @Query("SELECT f FROM Fixture f WHERE YEAR(f.date) = :year AND MONTH(f.date) = :month")
    List<Fixture> findByMonth(@Param("year") int year, @Param("month") int month);

    @Query("SELECT f FROM Fixture f WHERE YEAR(f.date) = :year AND MONTH(f.date) = :month AND (f.homeTeam = :team OR f.awayTeam = :team)")
    List<Fixture> findByMonthAndTeam(@Param("year") int year, @Param("month") int month, @Param("team") String team);
}
