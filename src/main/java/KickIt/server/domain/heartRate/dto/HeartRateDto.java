package KickIt.server.domain.heartRate.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class HeartRateDto {
    private Long matchId;

    private List<MatchHeartRateRecords> matchHeartRateRecords;

    @Getter
    public static class MatchHeartRateRecords {
        private int heartRate;
        private int date;

    }
}
