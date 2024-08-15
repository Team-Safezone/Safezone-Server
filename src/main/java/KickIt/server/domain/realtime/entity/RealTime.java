package KickIt.server.domain.realtime.entity;

import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class RealTime {
    // 경기 고유 id
    @Id
    private Long matchId;
    private String eventTime;
    private int eventCode;
    private String eventName;
    // 첫 번째 정보
    private String player1;
    // 두 번째 정보
    private String player2;
    private String teamName;
    private String teamUrl;

    @Override
    public String toString() {
        return "RealTime{" +
                "matchId=" + matchId +
                ", eventCode=" + eventCode +
                ", eventTime='" + eventTime + '\'' +
                ", eventName='" + eventName + '\'' +
                ", player1='" + player1 + '\'' +
                ", player2='" + player2 + '\'' +
                ", teamName='" + teamName + '\'' +
                ", teamUrl='" + teamUrl + '\'' +
                '}';
    }
}