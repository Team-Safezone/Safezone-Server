package KickIt.server.domain.fixture.entity;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
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

    // 심박수 통계
    @Query("SELECT f.homeTeam, f.awayTeam FROM Fixture f WHERE id = :id")
    List<Object[]> findHomeAwayTeam(@Param("id") Long id);

    // 경기 상태 정보 업데이트
    @Modifying
    @Transactional
    @Query("UPDATE Fixture f SET f.status =:status WHERE f.id = :id")
    void updateStatus(@Param("id") Long id, @Param("status") int status);

    // 선호 경기 선별을 위해 1, 2, 3 순위 팀 예정 경기를 가져와 날짜 순으로 3개 반환
    @Query(value = "SELECT f FROM Fixture f WHERE (f.homeTeam IN :teams OR f.awayTeam IN :teams) AND f.date > CURRENT_TIMESTAMP ORDER BY f.date ASC LIMIT 3")
    List<Fixture> findByFavTeams(@Param("teams") List<String> teams);

    // 선호 팀에 대한 우승팀 예측 추천 경기를 가져오기 위해 1, 2, 3 순위 팀 예정 경기 가져와 날짜 순으로 배열하고, 만약 날짜가 같다면 우선 순위가 높은 팀의 경기를 가져옴
    // 3 경기인 경우
    @Query(value = "SELECT f FROM Fixture f WHERE (f.homeTeam IN :teams OR f.awayTeam IN :teams) " +
            "AND f.date > CURRENT_TIMESTAMP " +
            "ORDER BY f.date ASC, " +
            "CASE " +
            "WHEN (f.homeTeam = :team1 OR f.awayTeam =:team1) THEN 1 " +
            "WHEN (f.homeTeam = :team2 OR f.awayTeam = :team2) THEN 2 " +
            "WHEN (f.homeTeam = :team3 OR f.awayTeam = :team3) THEN 3 " +
            "END LIMIT 1")
    Optional<Fixture> findByFavTeamsAndPriorityWhen3(@Param("teams") List<String> teams, @Param("team1") String team1, @Param("team2") String team2, @Param("team3") String team3);

    // 2 경기인 경우
    @Query(value = "SELECT f FROM Fixture f WHERE (f.homeTeam IN :teams OR f.awayTeam IN :teams) " +
            "AND f.date > CURRENT_TIMESTAMP " +
            "ORDER BY f.date ASC, " +
            "CASE " +
            "WHEN (f.homeTeam = :team1 OR f.awayTeam =:team1) THEN 1 " +
            "WHEN (f.homeTeam = :team2 OR f.awayTeam = :team2) THEN 2 " +
            "END LIMIT 1")
    Optional<Fixture> findByFavTeamsAndPriorityWhen2(@Param("teams") List<String> teams, @Param("team1") String team1, @Param("team2") String team2);

    // 1 경기인 경우
    @Query(value = "SELECT f FROM Fixture f WHERE (f.homeTeam = :team1 OR f.awayTeam = :team1) " +
            "AND f.date > CURRENT_TIMESTAMP " +
            "ORDER BY f.date ASC LIMIT 1")
    Optional<Fixture> findByFavTeamsAndPriorityWhen1(@Param("team1") String team1);

    // 선호 팀에 대한 일기 작성 추천 경기를 가져오기 위해 1, 2, 3 순위 팀 지난 경기 가져와 가까운 날짜 순으로 배열하고, 만약 날짜가 같다면 우선 순위가 높은 팀의 경기를 가져옴
    // 3 경기인 경우
    @Query(value = "SELECT f FROM Fixture f WHERE (f.homeTeam IN :teams OR f.awayTeam IN :teams) " +
            "AND f.date < CURRENT_TIMESTAMP " +
            "ORDER BY f.date DESC, " +
            "CASE " +
            "WHEN (f.homeTeam = :team1 OR f.awayTeam =:team1) THEN 1 " +
            "WHEN (f.homeTeam = :team2 OR f.awayTeam = :team2) THEN 2 " +
            "WHEN (f.homeTeam = :team3 OR f.awayTeam = :team3) THEN 3 " +
            "END LIMIT 1")
    Optional<Fixture> findByFavTeamsAndPriorityPastWhen3(@Param("teams") List<String> teams, @Param("team1") String team1, @Param("team2") String team2, @Param("team3") String team3);

    // 2 경기인 경우
    @Query(value = "SELECT f FROM Fixture f WHERE (f.homeTeam IN :teams OR f.awayTeam IN :teams) " +
            "AND f.date < CURRENT_TIMESTAMP " +
            "ORDER BY f.date DESC, " +
            "CASE " +
            "WHEN (f.homeTeam = :team1 OR f.awayTeam =:team1) THEN 1 " +
            "WHEN (f.homeTeam = :team2 OR f.awayTeam = :team2) THEN 2 " +
            "END LIMIT 1")
    Optional<Fixture> findByFavTeamsAndPriorityPastWhen2(@Param("teams") List<String> teams, @Param("team1") String team1, @Param("team2") String team2);

    // 1 경기인 경우
    @Query(value = "SELECT f FROM Fixture f WHERE (f.homeTeam = :team1 OR f.awayTeam = :team1) " +
            "AND f.date < CURRENT_TIMESTAMP " +
            "ORDER BY f.date DESC LIMIT 1")
    Optional<Fixture> findByFavTeamsAndPriorityPastWhen1(@Param("team1") String team1);
}
