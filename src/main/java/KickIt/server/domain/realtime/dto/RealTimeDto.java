package KickIt.server.domain.realtime.dto;

public class RealTimeDto {
    private Long matchId;
    private String startTime;
    private int eventCode;
    private String eventTime;
    private String eventName;
    private String player1;
    private String player2;
    private String teamName;
    private String teamUrl;

    @Override
    public String toString() {
        return "RealTimeDto{" +
                "matchId=" + matchId +
                ", startTime='" + startTime + '\'' +
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
