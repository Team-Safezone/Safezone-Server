package KickIt.server.domain.lineup.entity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface MatchLineupRepository extends JpaRepository<MatchLineup, Long> {
    @Query("SELECT m FROM MatchLineup m WHERE m.fixture.id = :matchId")
    Optional<MatchLineup> findByFixtureId(@Param("matchId") long id);
}
