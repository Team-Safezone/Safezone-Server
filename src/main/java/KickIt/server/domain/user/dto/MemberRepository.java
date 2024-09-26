package KickIt.server.domain.user.dto;

import KickIt.server.domain.user.entity.Member;
import KickIt.server.domain.user.entity.OAuthProvider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByEmail(String email);

    Optional<Member> findByEmailAndOAuthProvider(String email, OAuthProvider oAuthProvider);
}
