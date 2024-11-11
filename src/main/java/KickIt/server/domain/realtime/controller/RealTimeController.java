package KickIt.server.domain.realtime.controller;

import KickIt.server.domain.realtime.dto.RealTimeDto;
import KickIt.server.domain.realtime.service.RealTimeService;
import KickIt.server.jwt.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/realTime")
public class RealTimeController {

    private final RealTimeService realTimeService;
    private final JwtTokenUtil jwtTokenUtil;

    @Autowired
    public RealTimeController(RealTimeService realTimeService, JwtTokenUtil jwtTokenUtil) {
        this.realTimeService = realTimeService;
        this.jwtTokenUtil = jwtTokenUtil;
    }

    @GetMapping()
    public ResponseEntity<Map<String, Object>> getRealTime(@RequestHeader(value = "xAuthToken") String xAuthToken, @RequestParam("matchId") Long matchId) {
        String email = jwtTokenUtil.getEmailFromToken(xAuthToken);

        Map<String, Object> responseBody = new HashMap<>();

        List<RealTimeDto.RealTimeResponse> responseList = realTimeService.findRealTimeByMatchId(email, matchId);

        // 조회해 가져온 데이터가 존재하는 경우 성공, OK status로 반환
        if (jwtTokenUtil.validateToken(xAuthToken, email)) {
            if (!responseList.isEmpty()) {
                responseBody.put("status", HttpStatus.OK.value());
                responseBody.put("message", "success");
                responseBody.put("data", responseList);
                responseBody.put("isSuccess", true);
                return new ResponseEntity<>(responseBody, HttpStatus.OK);
            }
            // 조회한 list가 비어있는 경우 데이터 없음 처리, NOT FOUND로 반환
            else {
                responseBody.put("status", HttpStatus.NOT_FOUND.value());
                responseBody.put("message", "데이터 없음");
                responseBody.put("isSuccess", false);
                return new ResponseEntity<>(responseBody, HttpStatus.NOT_FOUND);
            }
        } else {
            responseBody.put("status", HttpStatus.FORBIDDEN.value());
            responseBody.put("message", "유효한 토큰이 아닙니다.");
            responseBody.put("isSuccess", false);
            return new ResponseEntity<>(responseBody, HttpStatus.FORBIDDEN);
        }

    }
}
