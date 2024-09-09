package KickIt.server.domain.realtime.service;

import KickIt.server.domain.fixture.entity.Fixture;
import KickIt.server.domain.fixture.entity.FixtureRepository;
import KickIt.server.domain.realtime.entity.RealTime;
import KickIt.server.domain.teams.entity.TeaminfoRepository;
import KickIt.server.global.common.crawler.RealTimeCrawler;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

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
    private final RestTemplate restTemplate = new RestTemplate();


    @Autowired
    public RealTimeStart(FixtureRepository fixtureRepository, RealTimeService realTimeService, TeaminfoRepository teaminfoRepository, ThreadPoolTaskScheduler taskScheduler) {
        this.taskScheduler = taskScheduler;
        this.fixtureRepository = fixtureRepository;
        this.realTimeService = realTimeService;
        this.teaminfoRepository = teaminfoRepository;
    }


    // 매 자정 마다 오늘 경기 여부 파악
    @Scheduled(cron = "30 34 18 * * ?")
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

        String matchId = getMatchIdFromApi(fixture);

        System.out.println("matchId = " + matchId);

        RealTimeCrawler realTimeCrawler = new RealTimeCrawler(realTimeService, teaminfoRepository);

        realTimeCrawler.initializeCrawler(fixture);

        try {
            while (!eventEnd) {
                // 전반/후반 시작 후 45분 대기
                realTimeCrawler.crawling();
                System.out.println("45분 대기");
                Thread.sleep(1 * 60 * 1000);

                while (!eventEnd) {
                    // 현재 상태 확인
                    String status = getMatchStatus(matchId);

                    switch (status) {
                        case "PAUSED":
                            System.out.println("경기 전반전 종료 감지: PAUSED 상태");
                            // 15분 대기 후 전반전 종료 후 다시 크롤링 시작
                            Thread.sleep(15 * 60 * 1000);
                            break;

                        case "FINISHED":
                            System.out.println("경기 종료 감지: FINISHED 상태");
                            eventEnd = true;
                            realTimeCrawler.quit();
                            return;

                        default:
                            // 상태가 PAUSED도 아니고 FINISHED도 아닌 경우 1분 대기 후 상태 확인
                            Thread.sleep(1 * 60 * 1000);
                    }
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            eventEnd = true;
            System.err.println("InterruptedException occurred: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("오류 발생: " + e.getMessage());
        }

    }


    // 경기 id 매칭하기
    private String getMatchIdFromApi(Fixture fixture) {
        try {
            String apiUrl = "https://api.football-data.org/v4/matches";
            RequestEntity<Void> req = RequestEntity
                    .get(apiUrl)
                    .header("X-Auth-Token", "62f9313599664f808aacc19ae5250420")
                    .build();
            ResponseEntity<String> result = restTemplate.exchange(req, String.class);
            String jsonResponse = result.getBody(); // 응답 body 가져오기

            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode root = objectMapper.readTree(jsonResponse);
            JsonNode matches = root.path("matches");

            // 팀 이름으로 경기 파악
            String fixtureHomeTeam = fixture.getHomeTeam().toString();
            System.out.println("fixtureHomeTeam = " + fixtureHomeTeam);
            String fixtureAwayTeam = fixture.getAwayTeam().toString();
            System.out.println("fixtureAwayTeam = " + fixtureAwayTeam);
            LocalDateTime fixtureDateTime = fixture.getDate().toLocalDateTime();

            // 경기 목록에서 ID를 찾기
            for (JsonNode match : matches) {
                String matchDate = match.path("utcDate").asText(); // 경기 날짜와 시간
                String matchHomeTeam = match.path("homeTeam").path("name").asText(); // 홈 팀
                String matchAwayTeam = match.path("awayTeam").path("name").asText(); // 어웨이 팀

                // JSON 응답에서 경기가 같은 날짜와 팀 정보를 가진 경우 찾기
                if (matchDate.startsWith(fixtureDateTime.toLocalDate().toString()) &&
                        matchHomeTeam.equals(fixtureHomeTeam) &&
                        matchAwayTeam.equals(fixtureAwayTeam)) {
                    return match.path("id").asText(); // 일치하는 경기 ID 반환
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("경기 ID를 가져오는 데 오류 발생: " + e.getMessage());
        }
        return null; // 일치하는 경기 ID가 없으면 null 반환
    }

    // 경기 상태 가져오기
    private String getMatchStatus(String matchId) {
        try {
            String apiUrl = "https://api.football-data.org/v4/matches/" + matchId; // 경기 ID를 기반으로 API URL 구성
            RequestEntity<Void> req = RequestEntity
                    .get(apiUrl)
                    .header("X-Auth-Token", "62f9313599664f808aacc19ae5250420")
                    .build();
            ResponseEntity<String> result = restTemplate.exchange(req, String.class);
            String jsonResponse = result.getBody(); // 응답 body 가져오기

            // Jackson ObjectMapper를 사용해 JSON 파싱
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode root = objectMapper.readTree(jsonResponse);

            // 경기 상태 추출
            JsonNode statusNode = root.path("status");
            String matchStatus = statusNode.asText();

            // 출력
            System.out.println("경기 상태: " + matchStatus);
            return matchStatus;

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("경기 상태를 가져오는 데 오류 발생: " + e.getMessage());
        }

        return null;
    }
}


