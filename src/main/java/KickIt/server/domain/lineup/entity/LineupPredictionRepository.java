package KickIt.server.domain.lineup.entity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface LineupPredictionRepository extends JpaRepository<LineupPrediction, Long> {
    @Query("SELECT l FROM LineupPrediction l WHERE l.member.memberId = :memberId AND l.fixture.id = :fixtureId")
    Optional<LineupPrediction> findByMemberAndFixture(@Param("memberId") Long memberId, @Param("fixtureId") Long fixtureId);
}
