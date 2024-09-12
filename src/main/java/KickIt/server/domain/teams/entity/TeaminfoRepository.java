package KickIt.server.domain.teams.entity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
// DB와 CRUD 수행
public interface TeaminfoRepository extends JpaRepository<Teaminfo, Integer> {
    boolean existsByRankingAndTeamAndSeason(int ranking, String team, String season);
    List<Teaminfo> findTeaminfoBySeasonOrderByRankingAsc(String season);


    // 팀이름 + 시즌 정보로 팀 로고 url 반환
    @Query("SELECT t.logoUrl FROM Teaminfo t WHERE t.team = :teamName AND t.season = :season")
    String findByTeamNameAndSeason(@Param("teamName") String teamName, @Param("season") String season);

}

