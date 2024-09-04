package KickIt.server.domain.realtime.service;

import KickIt.server.domain.fixture.entity.Fixture;
import KickIt.server.domain.fixture.entity.FixtureRepository;
import KickIt.server.domain.realtime.entity.RealTime;
import KickIt.server.domain.teams.entity.TeaminfoRepository;
import KickIt.server.global.common.crawler.RealTimeCrawler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

@Service
@EnableAsync
public class RealTimeStart {
    int n = 1;
    private final FixtureRepository fixtureRepository;
    private final ThreadPoolTaskScheduler taskScheduler;
    private final RealTimeService realTimeService;
    private final TeaminfoRepository teaminfoRepository;


    @Autowired
    public RealTimeStart(FixtureRepository fixtureRepository, RealTimeService realTimeService, TeaminfoRepository teaminfoRepository, ThreadPoolTaskScheduler taskScheduler) {
        this.taskScheduler = taskScheduler;
        this.fixtureRepository = fixtureRepository;
        this.realTimeService = realTimeService;
        this.teaminfoRepository = teaminfoRepository;
    }


    // 매 자정 마다 오늘 경기 여부 파악
    @Scheduled(cron = "0 43 23 * * ?")
    public void getTodayFixture() {
        LocalDate today = LocalDate.of(2024, 8, 20);
        //LocalDate today = LocalDate.now();

        // LocalDate를 LocalDateTime으로 변환하고, Timestamp로 변환
        LocalDateTime startOfDay = today.atStartOfDay();
        Timestamp timestamp = Timestamp.valueOf(startOfDay);

        try {
            List<Fixture> fixtureList = fixtureRepository.findByDate(timestamp);
            if (fixtureList != null && !fixtureList.isEmpty()) {
                getTimeMatch(fixtureList);
            } else {
                System.out.println("오늘은 경기가 없습니다.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("db에서 가져오는 데 오류");
        }

    }

    public void getTimeMatch(List<Fixture> fixtureList) {
        LocalDateTime now = LocalDateTime.now();
        for (Fixture fixture : fixtureList) {
            LocalDateTime fixtureMatchTime = fixture.getDate().toLocalDateTime();

            System.out.println("fixtureMatchTime = " + fixtureMatchTime);

            if (fixtureMatchTime.isAfter(now)) {
                Date startDate = Date.from(fixtureMatchTime.atZone(ZoneId.systemDefault()).toInstant());
                taskScheduler.schedule(() -> startStopCrawling(fixture), startDate);
                System.out.println("오늘 경기 시작 시간: " + fixtureMatchTime);
            } else {
                // 이미 시작된 경기는 바로 크롤링 시작
                System.out.println("이미 시작: " + fixtureMatchTime);
                Date startDate = new Date();
                taskScheduler.schedule(() -> startStopCrawling(fixture), startDate);
            }
        }

    }


    // 크롤링 시작, 중지, 종료
    @Async
    public void startStopCrawling(Fixture fixture){
        boolean eventEnd = false;

        RealTimeCrawler realTimeCrawler = new RealTimeCrawler(realTimeService, teaminfoRepository);

        realTimeCrawler.initializeCrawler(fixture);

        RealTime realTime;

        while (!eventEnd) {
            try {
                realTime = realTimeCrawler.crawling();

                System.out.println(n++ + "만큼 while 도는 중");
                // 경기 시작만 감지하고 return
                if (realTime.getEventName() == null) {
                    System.out.println("경기 시작 감지 완료");
                    LocalDateTime now = LocalDateTime.now();
                    System.out.println(now);
                    LocalDate now2 = LocalDate.now();
                    System.out.println(now2);
                    // 5분 대기
                    // Thread.sleep(5 * 60 * 1000);
                    continue;
                }
                // 전반전 종료
                if (realTime.getEventName().equals("하프타임")) {
                    System.out.println("하프타임, 이대로 15분 기다리기");
                    // 15분 대기
                    Thread.sleep(1 * 60 * 1000);
                    continue;
                }

                // 모두 종료
                if (realTime.getEventName().equals("경기종료")) {
                    eventEnd = true;
                    realTimeCrawler.quit();
                    break;
                }

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                eventEnd = true;
                System.err.println("InterruptedException occurred: " + e.getMessage());
                break;
            }
        }
    }
}
