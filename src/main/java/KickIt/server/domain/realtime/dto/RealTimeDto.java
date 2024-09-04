package KickIt.server.domain.realtime.dto;

import KickIt.server.domain.realtime.entity.RealTime;
import KickIt.server.domain.teams.EplTeams;
import KickIt.server.domain.teams.entity.Teaminfo;
import lombok.Getter;

public class RealTimeDto {
    private Long matchId;
    private int eventCode;
    private String eventTime;
    private String eventName;
    private String player1;
    private String player2;
    private String teamName;
    private String teamUrl;

    @Getter
    public static class RealTimeResponse{
        private Long matchId;
        private int eventCode;
        private String eventTime;
        private String eventName;
        private String player1;
        private String player2;
        private String teamName;
        private String teamUrl;

        public RealTimeResponse(Long matchId, int eventCode, String eventTime, String eventName, String player1, String player2, String teamName, String teamUrl) {
            this.matchId = matchId;
            this.eventCode = eventCode;
            this.eventTime = eventTime;
            this.eventName = eventName;
            this.player1 = player1;
            this.player2 = player2;
            this.teamName = teamName;
            this.teamUrl = teamUrl;
        }
    }

    @Override
    public String toString() {
        return "RealTimeDto{" +
                ", matchId=" + matchId +
                ", eventTime='" + eventTime + '\'' +
                ", eventCode=" + eventCode +
                ", eventName='" + eventName + '\'' +
                ", player1='" + player1 + '\'' +
                ", player2='" + player2 + '\'' +
                ", teamName='" + teamName + '\'' +
                ", teamUrl='" + teamUrl + '\'' +
                '}';
    }
}
