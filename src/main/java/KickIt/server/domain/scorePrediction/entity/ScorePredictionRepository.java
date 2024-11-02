package KickIt.server.domain.scorePrediction.entity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ScorePredictionRepository extends JpaRepository<ScorePrediction, Long> {
    @Query("SELECT s FROM ScorePrediction s WHERE s.fixture.id = :fixtureId AND s.member.id = :memberId")
    Optional<ScorePrediction> findByFixtureAndMember(@Param("fixtureId") Long fixtureId, @Param("memberId") Long memberId);

    @Query("SELECT s FROM ScorePrediction s WHERE s.fixture.id = :fixtureId")
    List<ScorePrediction> findByFixture(@Param("fixtureId") Long fixtureId);

    @Query("SELECT s.homeTeamScore, COUNT(s.homeTeamScore) AS count FROM ScorePrediction s WHERE s.fixture.id = :fixtureId GROUP BY s.homeTeamScore ORDER BY count DESC, MAX(s.lastUpdated) DESC LIMIT 1")
    Integer findAvgHomeTeamScore(@Param("fixtureId") Long fixtureId);

    @Query("SELECT s.awayTeamScore, COUNT(s.awayTeamScore) AS count FROM ScorePrediction s WHERE s.fixture.id = :fixtureId GROUP BY s.awayTeamScore ORDER BY count DESC, MAX(s.lastUpdated) DESC LIMIT 1")
    Integer findAvgAwayTeamScore(@Param("fixtureId") Long fixtureId);
}
