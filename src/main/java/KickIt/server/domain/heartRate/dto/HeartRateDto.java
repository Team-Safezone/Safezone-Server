package KickIt.server.domain.heartRate.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class HeartRateDto {
    private Long matchId;

    private List<MatchHeartRateRecords> matchHeartRateRecords;

    @Getter
    @NoArgsConstructor
    public static class MatchHeartRateRecords {
        private int heartRate;
        private int date;

        public MatchHeartRateRecords(int heartRate, int date) {
            this.heartRate = heartRate;
            this.date = date;
        }
    }
}
