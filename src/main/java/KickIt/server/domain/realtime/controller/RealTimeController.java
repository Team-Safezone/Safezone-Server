package KickIt.server.domain.realtime.controller;


import KickIt.server.domain.realtime.RealTimeConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/realtime")
public class RealTimeController {

    // API 정리되고 나면 수정 예정
    // 틀만 잡아놓기

    // postMapping
    // 사실 나중엔 필요없지만, 데이터 저장 잘 되는지 확인용

    private final RealTimeConfig realTimeConfig;

    @Autowired
    public RealTimeController(RealTimeConfig realTimeConfig) {
        this.realTimeConfig = realTimeConfig;
    }

    @PostMapping("/crawl")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Map<String, Object>> crawlRealTime(@RequestParam("getLineupUrl") String getLineupUrl) {
        realTimeConfig.startStopCrawling(getLineupUrl);
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("status", HttpStatus.OK.value());
        responseBody.put("message", "success");
        return new ResponseEntity<>(responseBody, HttpStatus.OK);
    }
}
