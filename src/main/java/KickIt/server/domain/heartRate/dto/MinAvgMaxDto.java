package KickIt.server.domain.heartRate.dto;

import lombok.Getter;

@Getter
public class MinAvgMaxDto {

    private int min;
    private int avg;
    private int max;

    public MinAvgMaxDto(int min, int avg, int max) {
        this.min = min;
        this.avg = avg;
        this.max = max;
    }
}
