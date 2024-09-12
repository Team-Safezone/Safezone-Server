package KickIt.server.domain.teams.entity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EplTeamRepository extends JpaRepository<EplTeam, Long> {

}
