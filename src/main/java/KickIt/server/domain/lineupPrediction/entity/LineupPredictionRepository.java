package KickIt.server.domain.lineupPrediction.entity;

import KickIt.server.domain.teams.entity.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface LineupPredictionRepository extends JpaRepository<LineupPrediction, Long> {
    @Query("SELECT l FROM LineupPrediction l WHERE l.member.id = :memberId AND l.fixture.id = :fixtureId")
    Optional<LineupPrediction> findByMemberAndFixture(@Param("memberId") Long memberId, @Param("fixtureId") Long fixtureId);

    @Query("SELECT p FROM LineupPrediction l JOIN l.players p WHERE l.member.id = :memberId AND l.fixture.id = :fixtureId AND p.position = :position AND p.team = :team ORDER BY p.location ASC")
    List<PredictionPlayer> findFilteredPlayersByMemberAndFixture(@Param("memberId") Long memberId, @Param("fixtureId") Long fixtureId, @Param("position") int position, @Param("team") int team);

    @Query("SELECT l FROM LineupPrediction l WHERE l.fixture.id = :fixtureId")
    List<LineupPrediction> findByFixture(@Param("fixtureId") Long fixtureId);

    @Query(value = "SELECT l.homeTeamForm, COUNT(l.homeTeamForm) AS count FROM LineupPrediction l WHERE l.fixture.id = :fixtureId GROUP BY l.homeTeamForm ORDER BY count DESC, MAX(l.lastUpdated) DESC LIMIT 1")
    Integer findAvgHomeTeamForm(@Param("fixtureId") Long fixtureId);

    @Query(value = "SELECT l.awayTeamForm, COUNT(l.awayTeamForm) AS count FROM LineupPrediction l WHERE l.fixture.id = :fixtureId GROUP BY l.awayTeamForm ORDER BY count DESC, MAX(l.lastUpdated) DESC LIMIT 1")
    Integer findAvgAwayTeamForm(@Param("fixtureId") Long fixtureId);

    @Query(value = "SELECT p.player, COUNT(p.player.id) As count FROM LineupPrediction l JOIN l.players p WHERE l.fixture.id = :fixtureId and l.homeTeamForm = :homeFormation AND p.team = 0 AND p.position = :position AND p.location = :location GROUP BY p.player.id ORDER BY count DESC, p.player.number ASC LIMIT 1")
    Player findAvgHomePlayer(@Param("fixtureId") Long fixtureId, @Param("homeFormation") Integer homeFormation, @Param("position") Integer position, @Param("location") Integer location);

    @Query(value = "SELECT p.player, COUNT(p.player.id) As count FROM LineupPrediction l JOIN l.players p WHERE l.fixture.id = :fixtureId and l.awayTeamForm = :awayFormation AND p.team = 1 AND p.position = :position AND p.location = :location GROUP BY p.player.id ORDER BY count DESC, p.player.number ASC LIMIT 1")
    Player findAvgAwayPlayer(@Param("fixtureId") Long fixtureId, @Param("awayFormation") Integer awayFormation, @Param("position") Integer position, @Param("location") Integer location);
}
