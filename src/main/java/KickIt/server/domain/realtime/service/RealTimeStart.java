package KickIt.server.domain.realtime.service;

import KickIt.server.domain.fixture.entity.Fixture;
import KickIt.server.domain.realtime.entity.RealTime;
import KickIt.server.global.common.crawler.RealTimeCrawler;
import org.springframework.context.annotation.Configuration;

public class RealTimeStart {

    private RealTimeCrawler realTimeCrawler;
    private Fixture fixture;
    private volatile boolean eventEnd = false;


    // 크롤링 시작, 중지, 종료
    public void startStopCrawling(String getLineupUrl){

  //      String currentDateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
//        String fixtureDateTime = fixture.getDate().toLocalDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        int count = 20;
        RealTimeCrawler crawler = new RealTimeCrawler(getLineupUrl);
        //if (currentDateTime.equals(fixtureDateTime)){
            //while (!eventEnd) {
             while(count > 0){
                try {
                    //RealTimeCrawler crawler = new RealTimeCrawler(getLineupUrl);
                    RealTime realTime = crawler.startCrawling();

                    // 이벤트 발생 X
                    if(realTime.getEventName() == null){
                        System.out.println("5분 쉬나요...?");
                        Thread.sleep(5 * 60 * 1000);
                        continue;
                    }

                    // 전반전 종료
                    if (realTime.getEventName().equals("하프타임")) {
                        System.out.println("15분 쉬나요..?");
                        Thread.sleep(15 * 60 * 1000); // 15분 대기
                        continue;
                    }

                    // 모두 종료
                    if (realTime.getEventName().equals("경기종료")) {
                        eventEnd = true;
                        crawler.quit();
                        break;
                    }

                    count--;
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    System.err.println("InterruptedException occurred: " + e.getMessage());
                    crawler.quit();
                    break;
                }
            }
        }
   // }
}
