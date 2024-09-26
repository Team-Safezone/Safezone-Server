package KickIt.server.domain.teams.entity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SquadRepository extends JpaRepository<Squad, Long>{
    Optional<Squad> findBySeasonAndTeam(String season, String team);
}
