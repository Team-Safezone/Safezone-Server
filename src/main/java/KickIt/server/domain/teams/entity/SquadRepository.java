package KickIt.server.domain.teams.entity;

import KickIt.server.domain.member.dto.MypageDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SquadRepository extends JpaRepository<Squad, Long>{
    Optional<Squad> findBySeasonAndTeam(String season, String team);
    List<Squad> findBySeason(String season);

    @Query("SELECT new KickIt.server.domain.member.dto.MypageDto$FavoriteTeamsUrl(s.team, s.logoImg) " +
            "FROM Squad s " +
            "WHERE team =:team")
    List<MypageDto.FavoriteTeamsUrl> getTeamInfo(@Param("team") String team);

    @Query("SELECT logoImg FROM Squad WHERE team = :team")
    String getUrl(@Param("team") String team);

    @Query(value = "SELECT s.season FROM Squad s ORDER BY s.season DESC LIMIT 1")
    Optional<String> getNewestSeason();
}
