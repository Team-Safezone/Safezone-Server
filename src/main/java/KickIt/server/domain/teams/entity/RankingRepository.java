package KickIt.server.domain.teams.entity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RankingRepository extends JpaRepository<Ranking, Long> {
    @Query(value = "SELECT r FROM Ranking r ORDER BY r.lastUpdated LIMIT 1")
    Optional<Ranking> findNewestRank();

    @Query(value = "SELECT r FROM Ranking r WHERE r.squad.season = :season")
    List<Ranking> findBySeason(@Param("season") String season);
}
