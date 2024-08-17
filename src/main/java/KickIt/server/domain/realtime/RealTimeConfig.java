package KickIt.server.domain.realtime;

import KickIt.server.domain.fixture.entity.Fixture;
import KickIt.server.domain.realtime.entity.RealTime;
import KickIt.server.domain.realtime.service.RealTimeService;
import KickIt.server.global.common.crawler.RealTimeCrawler;
import KickIt.server.global.common.crawler.RealTimeDataParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Configuration
public class RealTimeConfig {

    private RealTimeCrawler realTimeCrawler;
    private RealTimeDataParser realTimeDataParser;

    /*
    public RealTimeConfig(RealTimeCrawler realTimeCrawler, RealTimeDataParser realTimeDataParser) {
        this.realTimeCrawler = realTimeCrawler;
        this.realTimeDataParser = realTimeDataParser;
    }

     */

    // 크롤링 시작, 중지, 종료
    // @Bean
    public void startStopCrawling(String getLinupUrl){

        boolean eventEnd = false;

        Fixture fixture = new Fixture();
        RealTimeCrawler crawler = new RealTimeCrawler(getLinupUrl);
        RealTime realTime;

        //fixture 형태가 어떤 식이 었는지,, 기억이 안나서.. 일단 요렇게 표시
        //if (LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss")).equals(fixture.getDate())) {
            while (!eventEnd) {
                try {
                    realTime = crawler.startCrawling();

                    if (realTime.getEventName().equals("종료")) {
                        //System.out.println("전반전 종료. 15분 후 후반전 시작.(리스트 포함X)");
                        Thread.sleep(15 * 60 * 1000); // 15분 대기
                    }

                    if (realTime.getEventName().equals("경기종료")) {
                        eventEnd = true;
                        crawler.quit();
                        break;
                    }

                } catch (InterruptedException e) {
                    // 스레드 인터럽트 처리
                    Thread.currentThread().interrupt(); // 인터럽트 플래그 설정
                    System.err.println("InterruptedException occurred: " + e.getMessage());
                    crawler.quit();
                    break; // 예외 발생 시 루프 종료
                }
            }
        //}
    }
}
