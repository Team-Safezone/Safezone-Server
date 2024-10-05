package KickIt.server.domain.heartRate.dto;

import KickIt.server.domain.heartRate.entity.HeartRate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HeartRateRepository extends JpaRepository<HeartRate, Long> {

}
