package KickIt.Server;

import KickIt.server.domain.realtime.RealTimeConfig;
import KickIt.server.global.common.crawler.RealTimeCrawler;
import KickIt.server.global.common.crawler.RealTimeDataParser;
import org.junit.jupiter.api.Test;

public class RealTimeCrawlerTest {

    @Test
    public void start() {
        RealTimeConfig realTimeConfig = new RealTimeConfig();
        realTimeConfig.startStopCrawling("80073916");
    }

}
