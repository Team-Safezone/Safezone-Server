package KickIt.server.domain.heartRate.controller;

import KickIt.server.JwtService;
import KickIt.server.JwtTokenUtil;
import KickIt.server.domain.heartRate.dto.HeartRateDTO;
import KickIt.server.domain.heartRate.service.HeartRateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/heartRate")
public class HeartRateController {

    private final HeartRateService heartRateService;
    private final JwtTokenUtil jwtTokenUtil;

    @Autowired
    public HeartRateController(HeartRateService heartRateService, JwtTokenUtil jwtTokenUtil) {
        this.heartRateService = heartRateService;
        this.jwtTokenUtil = jwtTokenUtil;
    }

    @PostMapping("/save")
    public ResponseEntity<Map<String, Object>> saveHeartRate(@RequestParam(value = "xAuthToken") String xAuthToken, @RequestBody HeartRateDTO heartRateDTO) {
        String email = jwtTokenUtil.getEmailFromToken(xAuthToken);

        Map<String, Object> responseBody = new HashMap<>();

        if (jwtTokenUtil.validateToken(xAuthToken, email)) {
            heartRateService.save(email, heartRateDTO);

            responseBody.put("status", HttpStatus.OK.value());
            responseBody.put("message", "success");
            responseBody.put("isSuccess", true);

            return new ResponseEntity<>(responseBody, HttpStatus.OK);
        } else {
            responseBody.put("status", HttpStatus.FORBIDDEN.value());
            responseBody.put("message", "유효하지 않은 사용자입니다.");
            responseBody.put("isSuccess", false);
            return new ResponseEntity<>(responseBody, HttpStatus.FORBIDDEN);
        }
    }
}
