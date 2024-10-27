package KickIt.server.domain.heartRate.dto;


import lombok.Getter;

@Getter
public class RealTimeStatisticsDto {

    private String teamUrl;
    private String eventName;
    private int time;
    private String player1;

    public RealTimeStatisticsDto(String teamUrl, String eventName, String time, String player1) {
        this.teamUrl = teamUrl;
        this.eventName = eventName;
        try {
            this.time = Integer.parseInt(time);
        } catch (NumberFormatException e) {
            this.time = 0;
            System.err.println("String -> int 변환 실패 " + time);
        }
        this.player1 = player1;
    }

}
