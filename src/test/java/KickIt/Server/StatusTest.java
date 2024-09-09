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
                .get("https://api.football-data.org/v4/matches")
                .header("X-Auth-Token", "62f9313599664f808aacc19ae5250420")
                .build();

            ResponseEntity<String> result = restTemplate.exchange(req, String.class);
            String jsonResponse = result.getBody(); // 응답 body 가져오기

            // Jackson ObjectMapper를 사용해 JSON 파싱
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode root = objectMapper.readTree(jsonResponse);

            // 팀 목록 추출
            JsonNode teams = root.path("ids"); // 배열 노드
            for (JsonNode team : teams) {
                String teamId = team.path("ids").asText(); // teamId 필드 추출
                System.out.println("팀 ID: " + teamId);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
