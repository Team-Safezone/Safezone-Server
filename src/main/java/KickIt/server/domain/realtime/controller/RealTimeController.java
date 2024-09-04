package KickIt.server.domain.realtime.controller;

import KickIt.server.domain.realtime.dto.RealTimeDto;
import KickIt.server.domain.realtime.service.RealTimeService;
import KickIt.server.domain.teams.dto.TeaminfoDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/realTime")
public class RealTimeController {

    private final RealTimeService realTimeService;

    @Autowired
    public RealTimeController(RealTimeService realTimeService) {
        this.realTimeService = realTimeService;
    }

    @GetMapping()
    public ResponseEntity<Map<String, Object>> getRealTime(@RequestParam("matchId") Long matchId) {
        Map<String, Object> responseBody = new HashMap<>();
        List<RealTimeDto.RealTimeResponse> responseList = realTimeService.findRealTimeByMatchId(matchId);

        // 조회해 가져온 데이터가 존재하는 경우 성공, OK status로 반환
        if (!responseList.isEmpty()) {
            responseBody.put("status", HttpStatus.OK.value());
            responseBody.put("message", "success");
            responseBody.put("realTime", responseList);
            return new ResponseEntity<>(responseBody, HttpStatus.OK);
        }
        // 조회한 list가 비어있는 경우 데이터 없음 처리, NOT FOUND로 반환
        else {
            responseBody.put("status", HttpStatus.NOT_FOUND.value());
            responseBody.put("message", "데이터 없음");
            return new ResponseEntity<>(responseBody, HttpStatus.NOT_FOUND);
        }
    }
}
