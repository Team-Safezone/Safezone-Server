package KickIt.server.domain.realtime.dto;

import KickIt.server.domain.realtime.entity.RealTime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RealTimeRepository extends JpaRepository<RealTime, Long>{
    // 아예 동일한 이벤트 인지 먼저 확인 (경기 id, eventTime, eventName, player1, player2)
    Optional<RealTime> findByMatchIdAndTimeAndEventNameAndPlayer1AndPlayer2(Long matchId, String time, String eventName, String player1, String player2);

    // player2의 정보 변경 확인
    Optional<RealTime> findByMatchIdAndTimeAndEventNameAndPlayer1(Long matchId, String time, String eventName, String player1);

    // 업데이트
    @Query("UPDATE RealTime r SET r.player2 = :player2 WHERE r.matchId = :matchId AND r.time = :time AND r.eventName = :eventName AND r.player1 = :player1")
    void updateEvent(@Param("player2") String player2, @Param("matchId") Long matchId, @Param("time") String time, @Param("eventName") String eventName, @Param("player1") String player1);

    @Query("SELECT r FROM RealTime r WHERE r.matchId = :matchId ORDER BY r.sequence ASC")
    List<RealTime> findRealTimeByMatchId(@Param("matchId") Long matchId);

    // 심박수 통계에 필요한 정보
    @Query("SELECT r.eventTime FROM RealTime r WHERE r.matchId = :matchId AND (r.eventCode = 0 OR r.eventCode = 6)")
    List<String> getEventTime(@Param("matchId") Long matchId);

}
