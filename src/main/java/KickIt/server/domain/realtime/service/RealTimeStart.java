package KickIt.server.domain.realtime.service;

import KickIt.server.domain.fixture.entity.Fixture;
import KickIt.server.domain.fixture.entity.FixtureRepository;
import KickIt.server.domain.fixture.service.FixtureService;
import KickIt.server.domain.realtime.dto.RealTimeRepository;
import KickIt.server.domain.realtime.entity.RealTime;
import KickIt.server.domain.teams.entity.TeaminfoRepository;
import KickIt.server.global.common.crawler.RealTimeCrawler;
import KickIt.server.global.util.WebDriverUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Service
public class RealTimeStart {

    @Autowired
    private RealTimeCrawler realTimeCrawler;
    private final FixtureRepository fixtureRepository;
    private final ThreadPoolTaskScheduler taskScheduler;
    private RealTimeRepository realTimeRepository;
    private TeaminfoRepository teaminfoRepository;

    @Autowired
    public RealTimeStart(FixtureRepository fixtureRepository, RealTimeCrawler realTimeCrawler, ThreadPoolTaskScheduler taskScheduler) {
        this.taskScheduler = taskScheduler;
        this.realTimeCrawler = realTimeCrawler;
        this.fixtureRepository = fixtureRepository;
    }

    // 매 자정 마다 오늘 경기 여부 파악
    //@Scheduled(cron = "0 0 0 * * ?")
    public void getTodayFixture() {
        LocalDate today = LocalDate.of(2024, 8, 19);
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
        for (Fixture fixture : fixtureList) {
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime fixtureMatchTime = fixture.getDate().toLocalDateTime();

            if (fixtureMatchTime.isAfter(now)) {
                Date startDate = Date.from(fixtureMatchTime.atZone(ZoneId.systemDefault()).toInstant());
                taskScheduler.schedule(() -> startStopCrawling(fixture), startDate);
                System.out.println("오늘 경기 시작 시간: " + fixtureMatchTime);
            } else {
                // 이미 시작된 경기는 바로 크롤링 시작
                startStopCrawling(fixture);
            }
        }
    }


    // 크롤링 시작, 중지, 종료
    public void startStopCrawling(Fixture fixture){
        boolean eventEnd = false;

        realTimeCrawler = new RealTimeCrawler(realTimeRepository, teaminfoRepository);

        while (!eventEnd) {
            try {
                RealTime realTime = realTimeCrawler.crawling(fixture);

                // 이벤트 발생 X
                if (realTime.getEventName() == null) {
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
