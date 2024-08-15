package KickIt.server.domain.realtime.dto;

import KickIt.server.domain.realtime.entity.RealTime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RealTimeRepository extends JpaRepository<RealTime, Long> {

}
