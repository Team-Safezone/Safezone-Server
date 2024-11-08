package KickIt.server.domain.heartRate.entity;

import KickIt.server.domain.heartRate.dto.EventHeartRateDto;
import KickIt.server.domain.heartRate.entity.HeartRate;
import KickIt.server.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HeartRateRepository extends JpaRepository<HeartRate, Long> {

    List<HeartRate> findByMemberIdAndFixtureId(Long memberId, Long fixtureId);

    @Query("SELECT heartRate FROM HeartRate h WHERE fixtureId = :fixtureId")
    List<Integer> getAllHeartRate(@Param("fixtureId") Long fixtureId);

    @Query("SELECT new KickIt.server.domain.heartRate.dto.EventHeartRateDto(h.heartRateDate, h.heartRate) FROM HeartRate h WHERE h.memberId = :memberId AND h.fixtureId = :fixtureId")
    List<EventHeartRateDto> getEventHeartRate(@Param("memberId") Long memberId, @Param("fixtureId") Long fixtureId);
}
