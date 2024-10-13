package KickIt.server.domain.heartRate.dto;

import KickIt.server.domain.realtime.dto.RealTimeDto;
import KickIt.server.domain.realtime.dto.RealTimeStatisticsDto;
import KickIt.server.domain.realtime.entity.RealTime;
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

    public StatisticsDto(String startDate, String endDate, int lowHeartRate, int highHeartRate, int minBPM, int avgBPM, int maxBPM) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.lowHeartRate = lowHeartRate;
        this.highHeartRate = highHeartRate;
        this.minBPM = minBPM;
        this.avgBPM = avgBPM;
        this.maxBPM = maxBPM;
    }

}
