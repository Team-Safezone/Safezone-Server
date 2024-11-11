package KickIt.server.domain.heartRate.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
public class EventHeartRateDto {
    private int heartRateDate;
    private int heartRate;

    public EventHeartRateDto(int heartRateDate, int heartRate) {
        this.heartRateDate = heartRateDate;
        this.heartRate = heartRate;
    }
}