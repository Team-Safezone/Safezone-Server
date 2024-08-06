package KickIt.server.domain.realtime.dto;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RealTime {
    // 경기 고유 id
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    // 이벤트 발생시간(크롤링 발생 시간 + 전반전, 후반전 시작 시간)
    private String dateTime;
    // 타임라인 시간
    private String timeLine;
    // 발생 이벤트
    private String event;
    // 첫 번째 정보
    private String inform1;
    // 두 번째 정보
    private String inform2;


    public static class Builder {
        private String dateTime;
        private String timeLine;
        private String event;
        private String inform1;
        private String inform2;

        public Builder dateTime(String dateTime) {
            this.dateTime = dateTime;
            return this;
        }

        public Builder timeLine(String timeLine) {
            this.timeLine = timeLine;
            return this;
        }

        public Builder event(String event) {
            this.event = event;
            return this;
        }

        public Builder inform1(String inform1) {
            this.inform1 = inform1;
            return this;
        }

        public Builder inform2(String inform2) {
            this.inform2 = inform2;
            return this;
        }

        public RealTime build() {
            RealTime realTime = new RealTime();
            realTime.dateTime = this.dateTime;
            realTime.timeLine = this.timeLine;
            realTime.event = this.event;
            realTime.inform1 = this.inform1;
            realTime.inform2 = this.inform2;
            return realTime;
        }
    }

    @Override
    public String toString() {
        return dateTime + " " + timeLine + " " + event + " " + inform1 + " " + inform2;
    }

}