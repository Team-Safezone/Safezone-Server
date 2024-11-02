package KickIt.server.domain.scorePrediction.entity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ScorePredictionRepository extends JpaRepository<ScorePrediction, Long> {
    @Query("SELECT s FROM ScorePrediction s WHERE s.fixture.id = :fixtureId AND s.member.id = :memberId")
    Optional<ScorePrediction> findByFixtureAndMember(@Param("fixtureId") Long fixtureId, @Param("memberId") Long memberId);
}
