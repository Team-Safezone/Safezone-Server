package KickIt.server.domain.realtime.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class RealTimeStatisticsDto {

    private String teamUrl;
    private String eventName;
    private int eventTime;
    private String player1;

    public RealTimeStatisticsDto(String teamUrl, String eventName, String time, String player1) {
        this.teamUrl = teamUrl;
        this.eventName = eventName;
        try {
            this.eventTime = Integer.parseInt(time);
        } catch (NumberFormatException e) {
            this.eventTime = 0;
        }
        this.player1 = player1;
    }
}
