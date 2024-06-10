package KickIt.server.domain.teams.entity;

import KickIt.server.domain.fixture.entity.Fixture;
import KickIt.server.domain.teams.EplTeams;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;

@Repository
// DB와 CRUD 수행
public interface TeaminfoRepository extends JpaRepository<Teaminfo, Integer> {
    boolean existsByRankingAndTeamAndSeason(int ranking, EplTeams team, String season);

    List<Teaminfo> findTeaminfoBySeasonOrderByRankingAsc(String season);
}
