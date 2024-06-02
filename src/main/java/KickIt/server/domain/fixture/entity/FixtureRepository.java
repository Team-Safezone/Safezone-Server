package KickIt.server.domain.fixture.entity;

import KickIt.server.domain.teams.EplTeams;
import org.springframework.data.jpa.repository.JpaRepository;

import java.sql.Timestamp;
import java.util.Optional;
import java.util.UUID;

public interface FixtureRepository extends JpaRepository<Fixture, UUID>{
    Optional<Fixture> findByDateAndHomeTeamAndAwayTeam(Timestamp date, EplTeams homeTeam , EplTeams awayTeam);
}
