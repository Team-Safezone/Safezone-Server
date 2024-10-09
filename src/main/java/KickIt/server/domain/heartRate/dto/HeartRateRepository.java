package KickIt.server.domain.heartRate.dto;

import KickIt.server.domain.heartRate.entity.HeartRate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HeartRateRepository extends JpaRepository<HeartRate, Long> {

    @Query("SELECT heartRate FROM HeartRate h WHERE memberId = :memberId AND fixtureId = :fixtureId")
    List<Integer> getheartRate(@Param("memberId") Long memberId, @Param("fixtureId") Long fixtureid);

}
