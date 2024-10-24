package KickIt.server.domain.lineup.entity;

import KickIt.server.domain.lineup.dto.LineupPredictionDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface LineupPredictionRepository extends JpaRepository<LineupPrediction, Long> {
    @Query("SELECT l FROM LineupPrediction l WHERE l.member.memberId = :memberId AND l.fixture.id = :fixtureId")
    Optional<LineupPrediction> findByMemberAndFixture(@Param("memberId") Long memberId, @Param("fixtureId") Long fixtureId);

    @Query("SELECT p FROM LineupPrediction l JOIN l.players p WHERE l.member.memberId = :memberId AND l.fixture.id = :fixtureId AND p.position = :position AND p.team = :team ORDER BY p.location ASC")
    List<PredictionPlayer> findFilteredPlayersByMemberAndFixture(@Param("memberId") Long memberId, @Param("fixtureId") Long fixtureId, @Param("position") int position, @Param("team") int team);

    @Query("SELECT l FROM LineupPrediction l WHERE l.fixture.id = :fixtureId")
    List<LineupPrediction> findByFixture(@Param("fixtureId") Long fixtureId);

    @Query(value = "SELECT l.homeTeamForm, COUNT(l.homeTeamForm) AS count FROM LineupPrediction l WHERE l.fixture.id = :fixtureId GROUP BY l.homeTeamForm ORDER BY count DESC LIMIT 1")
    Integer findAvgHomeTeamForm(@Param("fixtureId") Long fixtureId);

    @Query(value = "SELECT l.awayTeamForm, COUNT(l.awayTeamForm) AS count FROM LineupPrediction l WHERE l.fixture.id = :fixtureId GROUP BY l.awayTeamForm ORDER BY count DESC LIMIT 1")
    Integer findAvgAwayTeamForm(@Param("fixtureId") Long fixtureId);
}
