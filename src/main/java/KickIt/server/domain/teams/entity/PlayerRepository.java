package KickIt.server.domain.teams.entity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PlayerRepository extends JpaRepository<Player, UUID> {
    @Query("SELECT p FROM Player p WHERE p.team = :team AND p.name LIKE %:name%")
    Optional<Player> findByTeamAndNameContaining(@Param("team")String team, @Param("name")String name);

    @Query("SELECT p FROM Player p WHERE p.team = :team AND (p.number = :number)")
    Optional<Player> findByTeamAndNumber(@Param("team") String team, @Param("number") Integer number);
}
