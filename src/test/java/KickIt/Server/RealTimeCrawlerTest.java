package KickIt.Server;

import KickIt.server.domain.realtime.service.RealTimeStart;
import org.junit.jupiter.api.Test;

public class RealTimeCrawlerTest {

    @Test
    public void start() {
        RealTimeStart realTimeStart = new RealTimeStart();
        realTimeStart.startStopCrawling("80085794");
    }

    @Test
    public void star2() {
        RealTimeStart realTimeStart = new RealTimeStart();
        realTimeStart.startStopCrawling("80085392");
    }
}
