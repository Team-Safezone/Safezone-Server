package KickIt.Server;

import KickIt.server.domain.fixture.entity.Fixture;
import KickIt.server.domain.fixture.entity.FixtureRepository;
import KickIt.server.domain.realtime.service.RealTimeStart;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class RealTimeCrawlerTest {

    @Autowired
    private RealTimeStart realTimeStart;

    @Test
    public void testGetTodayFixture() {
        realTimeStart.getTodayFixture();
    }

}
