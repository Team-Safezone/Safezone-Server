package KickIt.server.domain.member.dto;

import KickIt.server.domain.member.entity.Member;
import KickIt.server.domain.member.entity.AuthProvider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByEmail(String email);

    Optional<Member> findByEmailAndAuthProvider(String email, AuthProvider authProvider);

    Optional<Member> findByNickname(String nickname);

    // 심박수 통계용
    @Query("SELECT m.team1, m.team2, m.team3 FROM Member m WHERE id = :id ")
    List<Object[]> getFavoriteTeam(@Param("id") Long id);

    @Query("SELECT m.team1, m.team2, m.team3 FROM Member m")
    List<Object[]> getFavoriteTeamAll();
}
