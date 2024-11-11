package KickIt.server.domain.realtime.dto;

import KickIt.server.domain.realtime.entity.RealTime;
import KickIt.server.domain.teams.entity.Teaminfo;
import lombok.Getter;

import static java.lang.Integer.parseInt;

public class RealTimeDto {
    private Long matchId;
    private int eventCode;
    private int time;
    private String eventTime;
    private String eventName;
    private String player1;
    private String player2;
    private Integer eventHeartRate;
    private int avgHeartRate;
    private String teamName;
    private String teamUrl;

    @Getter
    public static class RealTimeResponse{
        private Long matchId;
        private int eventCode;
        private int time;
        private String eventTime;
        private String eventName;
        private String player1;
        private String player2;
        private Integer eventHeartRate;
        private int avgHeartRate;
        private String teamName;
        private String teamUrl;

        public RealTimeResponse(Long matchId, int eventCode, String time, String eventTime, String eventName, String player1, String player2, Integer eventHeartRate, int avgHeartRate, String teamName, String teamUrl) {
            this.matchId = matchId;
            this.eventCode = eventCode;
            this.time = parseInt(time);
            this.eventTime = eventTime;
            this.eventName = eventName;
            this.player1 = player1;
            this.player2 = player2;
            this.eventHeartRate = eventHeartRate;
            this.avgHeartRate = avgHeartRate;
            this.teamName = teamName;
            this.teamUrl = teamUrl;
        }
    }
}
