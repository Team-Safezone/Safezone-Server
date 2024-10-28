package KickIt.server.domain.member.entity;

import KickIt.server.domain.member.entity.Member;
import KickIt.server.domain.member.entity.AuthProvider;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByEmail(String email);

    Optional<Member> findByEmailAndAuthProvider(String email, AuthProvider authProvider);

    Optional<Member> findByNickname(String nickname);

    @Modifying
    @Transactional
    @Query("UPDATE Member SET team1 = :team1, team2 = :team2, team3 = :team3 WHERE id = :id")
    void updateTeams(@Param("id") Long id, @Param("team1") String team1, @Param("team2") String team2, @Param("team3") String team3);

    @Modifying
    @Transactional
    @Query("UPDATE Member SET nickname = :nickname WHERE id = :id")
    void updateNickname(@Param("id") Long id, @Param("nickname") String nickname);
}
