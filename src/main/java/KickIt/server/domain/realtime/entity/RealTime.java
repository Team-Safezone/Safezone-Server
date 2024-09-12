package KickIt.server.domain.realtime.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class RealTime {
    // 경기 고유 id
    @Id
    @GeneratedValue
    private int sequence;
    private Long matchId;
    private int eventCode;
    private String eventTime;
    private String eventName;
    private String player1;
    private String player2;
    private String teamName;
    private String teamUrl;

    @Override
    public String toString() {
        return "RealTime{" +
                "sequence=" + sequence +
                ", matchId=" + matchId +
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