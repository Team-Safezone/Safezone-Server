package KickIt.server.domain.heartRate.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class StatisticsDto {

    private String startDate;
    private String endDate;
    private int lowHeartRate;
    private int highHeartRate;
    private int minBPM;
    private int avgBPM;
    private int maxBPM;
    private List<RealTimeStatisticsDto> event;
    private List<HeartRateDto.MatchHeartRateRecords> homeTeamHeartRateRecords;

    public StatisticsDto(String startDate, String endDate, int lowHeartRate, int highHeartRate, int minBPM, int avgBPM, int maxBPM) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.lowHeartRate = lowHeartRate;
        this.highHeartRate = highHeartRate;
        this.minBPM = minBPM;
        this.avgBPM = avgBPM;
        this.maxBPM = maxBPM;
    }

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

}
