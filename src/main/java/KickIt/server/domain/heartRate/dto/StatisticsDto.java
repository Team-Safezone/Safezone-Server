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
    private List<HeartRateDto.MatchHeartRateRecords> awayTeamHeartRateRecords;
    private List<MinAvgMaxDto> homeTeamHeartRate;
    private List<MinAvgMaxDto> awayTeamHeartRate;
    private int homeTeamViewerPercentage;

    public StatisticsDto(String startDate, String endDate, int lowHeartRate, int highHeartRate, int minBPM, int avgBPM, int maxBPM, int homeTeamViewerPercentage) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.lowHeartRate = lowHeartRate;
        this.highHeartRate = highHeartRate;
        this.minBPM = minBPM;
        this.avgBPM = avgBPM;
        this.maxBPM = maxBPM;
        this.homeTeamViewerPercentage = homeTeamViewerPercentage;
    }

}
