package KickIt.Server;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class StatusTest {

    @Test
    public void test() {

        try {
        RestTemplate restTemplate = new RestTemplate();
        RequestEntity<Void> req = RequestEntity
                .get("https://api.football-data.org/v4/competitions/PL/matches")
                .header("X-Auth-Token", "62f9313599664f808aacc19ae5250420")
                .build();

            ResponseEntity<String> result = restTemplate.exchange(req, String.class);
            String jsonResponse = result.getBody(); // 응답 body 가져오기

            // Jackson ObjectMapper를 사용해 JSON 파싱
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode root = objectMapper.readTree(jsonResponse);

            JsonNode matches = root.path("matches");
            for (JsonNode match : matches) {
                String homeTla = match.path("homeTeam").path("tla").asText();
                String awayTla = match.path("awayTeam").path("tla").asText();
                String matchDate = match.path("utcDate").asText(); // 경기 날짜와 시간

                // tla 값을 출력
                System.out.print("matchDate = " + matchDate + " ");
                System.out.print("Home Team TLA: " + homeTla + " ");
                System.out.println("Away Team TLA: " + awayTla);

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
