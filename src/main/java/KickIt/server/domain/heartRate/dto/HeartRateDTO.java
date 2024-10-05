package KickIt.server.domain.heartRate.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class HeartRateDTO {
    private Long matchId;

    private List<Integer> heartRate;
    private List<String> heartRateDate;
}
