package KickIt.server.domain.fixture.entity;

import KickIt.server.domain.teams.EplTeams;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.Optional;
import java.util.UUID;

public interface FixtureRepository extends JpaRepository<Fixture, UUID>{
    Optional<Fixture> findByDateAndHomeTeamAndAwayTeam(Date date, EplTeams homeTeam , EplTeams awayTeam);
}
