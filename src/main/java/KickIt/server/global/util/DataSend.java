package KickIt.server.global.util;


import KickIt.server.domain.fixture.entity.Fixture;
import KickIt.server.domain.realtime.dto.RealTimeDto;
import KickIt.server.domain.realtime.dto.RealTimeRepository;
import KickIt.server.domain.realtime.entity.RealTime;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DataSend {


    private final RestTemplate restTemplate;
    private final RealTimeRepository realTimeRepository;

    @Value("${frontend-url}")
    private String frontendUrl;

    @Autowired
    public DataSend(RestTemplate restTemplate, RealTimeRepository realTimeRepository) {
        this.restTemplate = restTemplate;
        this.realTimeRepository = realTimeRepository;
    }


    public void sendRealTimeData(Long matchId) {
        try {
            List<RealTime> realTimeData = realTimeRepository.findRealTimeByMatchId(matchId);

            List<RealTimeDto.RealTimeResponse> realTimeDtos = realTimeData.stream()
                    .map(rt -> new RealTimeDto.RealTimeResponse(
                            rt.getMatchId(),
                            rt.getEventCode(),
                            rt.getEventTime(),
                            rt.getEventName(),
                            rt.getPlayer1(),
                            rt.getPlayer2(),
                            rt.getTeamName(),
                            rt.getTeamUrl()))
                    .collect(Collectors.toList());

            ObjectMapper objectMapper = new ObjectMapper();
            String jsonData = objectMapper.writeValueAsString(realTimeDtos);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<String> requestEntity = new HttpEntity<>(jsonData, headers);

            ResponseEntity<String> response = restTemplate.postForEntity(
                    frontendUrl + "/realTime/matchId={matchId}",
                    requestEntity,
                    String.class,
                    matchId
            );

            if (response.getStatusCode() == HttpStatus.OK) {
                System.out.println("프론트엔드에 데이터 전송 성공");
            } else {
                System.err.println("프론트엔드에 데이터 전송 실패: " + response.getStatusCode());
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("프론트엔드로 데이터를 전송하는 데 오류 발생: " + e.getMessage());
        }
    }
}



