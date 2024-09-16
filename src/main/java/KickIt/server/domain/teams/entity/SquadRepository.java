package KickIt.server.domain.teams.entity;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SquadRepository extends JpaRepository<Squad, Long>{
    Optional<Squad> findBySeasonAndTeam(String season, String team);
}
