package KickIt.server.domain.realtime.service;

import KickIt.server.domain.fixture.entity.Fixture;
import KickIt.server.domain.fixture.entity.FixtureRepository;
import KickIt.server.domain.realtime.entity.RealTime;
import KickIt.server.domain.teams.entity.TeaminfoRepository;
import KickIt.server.domain.teams.service.TeamNameConvertService;
import KickIt.server.global.common.crawler.RealTimeCrawler;
import KickIt.server.global.util.DataSend;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

@Service
@EnableAsync
public class RealTimeStart {

    private static final String API_URL = "https://api.football-data.org/v4/competitions/PL/matches";

    @Value("${fixture-status-admin-key}")
    private String AUTH_TOKEN;

    private final FixtureRepository fixtureRepository;
    private final ThreadPoolTaskScheduler taskScheduler;
    private final RealTimeService realTimeService;
    private final TeaminfoRepository teaminfoRepository;
    private final RestTemplate restTemplate = new RestTemplate();
    private final TeamNameConvertService teamNameConvertService;
    private final DataSend dataSend;


    private String apiMatchId;

    @Autowired
    public RealTimeStart(FixtureRepository fixtureRepository, RealTimeService realTimeService, TeaminfoRepository teaminfoRepository, ThreadPoolTaskScheduler taskScheduler, TeamNameConvertService teamNameConvertService, DataSend dataSend) {
        this.taskScheduler = taskScheduler;
        this.fixtureRepository = fixtureRepository;
        this.realTimeService = realTimeService;
        this.teaminfoRepository = teaminfoRepository;
        this.teamNameConvertService = teamNameConvertService;
        this.dataSend = dataSend;
    }


    // 매 자정 마다 오늘 경기 여부 파악
    @Scheduled(cron = "0 0 0 * * ?")
    public void getTodayFixture() {
        //LocalDate today = LocalDate.of(2024, 9, 2);
        LocalDate today = LocalDate.now(); //-> default

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
            apiMatchId = getMatchIdFromApi(fixture);
            String status = getMatchStatus(apiMatchId);

            //&& !status.equals("FINISHED")
            // 경기 연기, 취소 처리
            if(!status.equals("POSTPONED") && !status.equals("CANCELLED")){
                LocalDateTime fixtureMatchTime = fixture.getDate().toLocalDateTime();

                if (fixtureMatchTime.isAfter(now)) {
                    Date startDate = Date.from(fixtureMatchTime.atZone(ZoneId.systemDefault()).toInstant());
                    taskScheduler.schedule(() -> startStopCrawling(fixture,apiMatchId), startDate);
                    System.out.println("오늘 경기 시작 시간: " + fixtureMatchTime);
                }
                else {
                    // 이미 시작된 경기는 바로 크롤링 시작
                    System.out.println("이미 시작: " + fixtureMatchTime);
                    Date startDate = new Date();
                    taskScheduler.schedule(() -> startStopCrawling(fixture, apiMatchId), startDate);
                }

            }
        }

    }


    // 크롤링 시작, 중지, 종료
    @Async
    public void startStopCrawling(Fixture fixture, String apiMatchId){
        boolean eventEnd = false;

        System.out.println("apiMatchId = " + apiMatchId);

        String isDone;

        RealTimeCrawler realTimeCrawler = new RealTimeCrawler(realTimeService, teaminfoRepository, teamNameConvertService);

        realTimeCrawler.initializeCrawler(fixture);

        try {
            while (!eventEnd) {
                isDone = realTimeCrawler.crawling();

                switch (isDone) {
                    case "isFirst":
                        System.out.println("경기 시작");
                        Thread.sleep(45 * 60 * 1000);
                        break;
                    case " ":
                        System.out.println("1분 대기");
                        Thread.sleep(60 * 1000);
                        break;
                    case "종료":
                        System.out.println("전반전 종료");
                        dataSend.sendRealTimeData(fixture.getId());
                        Thread.sleep(13 * 60 * 1000);
                        break;
                    case "경기종료":
                        System.out.println("경기 종료");
                        eventEnd = true;
                        realTimeCrawler.quit();
                        dataSend.sendRealTimeData(fixture.getId());
                        return;
                    default:
                        System.out.println("예상치 못한 상태: " + isDone);
                        Thread.sleep(60 * 1000);
                        break;
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
            String apiUrl = API_URL;
            RequestEntity<Void> req = RequestEntity
                    .get(apiUrl)
                    .header("X-Auth-Token", AUTH_TOKEN)
                    .build();
            ResponseEntity<String> result = restTemplate.exchange(req, String.class);
            String jsonResponse = result.getBody(); // 응답 body 가져오기

            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode root = objectMapper.readTree(jsonResponse);
            JsonNode matches = root.path("matches");

            // 팀 이름 + 경기 시간으로 경기 파악
            String fixtureHomeTeam = fixture.getHomeTeam().toString();
            String fixtureAwayTeam = fixture.getAwayTeam().toString();
            LocalDateTime fixtureDateTime = fixture.getDate().toLocalDateTime();
            String fixtureDate = fixtureDateTime.toString();

            // 경기 목록에서 ID를 찾기
            for (JsonNode match : matches) {
                String matchDate = match.path("utcDate").asText(); // 경기 날짜와 시간
                String matchHomeTeam = match.path("homeTeam").path("tla").asText(); // 홈 팀
                String matchAwayTeam = match.path("awayTeam").path("tla").asText(); // 어웨이 팀

                matchDate = addNineHoursAndConvert(matchDate);

                //경기가 같은 날짜와 팀 정보를 가진 경우 찾기
                if (matchDate.equals(fixtureDate) &&
                        matchHomeTeam.equals(fixtureHomeTeam) &&
                        matchAwayTeam.equals(fixtureAwayTeam)) {
                    // 일치하는 경기 ID 반환
                    return match.path("id").asText();
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
            String apiUrl = "https://api.football-data.org/v4/matches/" + matchId;
            RequestEntity<Void> req = RequestEntity
                    .get(apiUrl)
                    .header("X-Auth-Token", AUTH_TOKEN)
                    .build();
            ResponseEntity<String> result = restTemplate.exchange(req, String.class);
            String jsonResponse = result.getBody(); // 응답 body 가져오기

            // Jackson ObjectMapper를 사용해 JSON 파싱
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode root = objectMapper.readTree(jsonResponse);

            // 경기 상태 반환
            JsonNode statusNode = root.path("status");
            return statusNode.asText();


        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("경기 상태를 가져오는 데 오류 발생: " + e.getMessage());
        }

        return null;
    }

    // 경기 시간 맞추기(api 경기 시간이 현지 시간 으로 되어 있어서 시차 9시간 처리)
    public static String addNineHoursAndConvert(String utcDateTime) {
        ZonedDateTime zonedDateTime = ZonedDateTime.parse(utcDateTime);
        ZonedDateTime updatedDateTime = zonedDateTime.plusHours(9);
        String apiMatchDate = updatedDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm"));

        return apiMatchDate;
    }
}


