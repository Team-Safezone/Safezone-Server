package KickIt.server.domain.diary.controller;


import KickIt.server.domain.diary.dto.DiaryIdRequest;
import KickIt.server.domain.diary.dto.DiarySaveDto;
import KickIt.server.domain.diary.service.DiaryService;
import KickIt.server.domain.heartRate.service.HeartRateStatisticsService;
import KickIt.server.jwt.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/diary")
public class DiaryController {

    private final JwtTokenUtil jwtTokenUtil;
    private final DiaryService diaryService;
    private final HeartRateStatisticsService heartRateStatisticsService;

    @Autowired
    public DiaryController(JwtTokenUtil jwtTokenUtil, DiaryService diaryService, HeartRateStatisticsService heartRateStatisticsService) {
        this.jwtTokenUtil = jwtTokenUtil;
        this.diaryService = diaryService;
        this.heartRateStatisticsService = heartRateStatisticsService;
    }

    @PostMapping("/upload")
    public ResponseEntity<Map<String, Object>> uploadDiary(@RequestHeader(value = "xAuthToken") String xAuthToken, @RequestBody DiarySaveDto diarySaveDto) {
        String email = jwtTokenUtil.getEmailFromToken(xAuthToken);

        Map<String, Object> responseBody = new HashMap<>();

        if (jwtTokenUtil.validateToken(xAuthToken, email)) {
            diaryService.save(diarySaveDto, email);

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

    @DeleteMapping("/delete")
    public ResponseEntity<Map<String, Object>> deleteDiary(@RequestHeader(value = "xAuthToken") String xAuthToken, @RequestBody DiaryIdRequest diaryIdRequest) {
        String email = jwtTokenUtil.getEmailFromToken(xAuthToken);
        Long diaryId = diaryIdRequest.getDiaryId();

        Map<String, Object> responseBody = new HashMap<>();

        if (jwtTokenUtil.validateToken(xAuthToken, email)) {

            diaryService.deleteDiary(diaryId);

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

    @GetMapping("/max-heartRate/{matchId}")
    public ResponseEntity<Map<String, Object>> getMaxHeartRate(@RequestHeader(value = "xAuthToken") String xAuthToken, @PathVariable(value = "matchId") Long matchId) {
        String email = jwtTokenUtil.getEmailFromToken(xAuthToken);

        Map<String, Object> responseBody = new HashMap<>();

        if (jwtTokenUtil.validateToken(xAuthToken, email)) {
            int highHeartRate = heartRateStatisticsService.getMax(email, matchId);
            if(highHeartRate != 0) {
                responseBody.put("status", HttpStatus.OK.value());
                responseBody.put("data", highHeartRate);
                responseBody.put("isSuccess", true);
                return new ResponseEntity<>(responseBody, HttpStatus.OK);
            } else {
                responseBody.put("status", HttpStatus.FORBIDDEN.value());
                responseBody.put("message", "최고 심박수가 존재하지 않습니다.");
                responseBody.put("isSuccess", false);
                return new ResponseEntity<>(responseBody, HttpStatus.FORBIDDEN);
            }

        } else {
            responseBody.put("status", HttpStatus.FORBIDDEN.value());
            responseBody.put("message", "유효하지 않은 사용자 입니다.");
            responseBody.put("isSuccess", false);
            return new ResponseEntity<>(responseBody, HttpStatus.FORBIDDEN);
        }
    }

    /*
    @PatchMapping("/isLiked/{diaryId}")
    public ResponseEntity<Map<String, Object>> editLiked(@RequestHeader(value = "xAuthToken") String xAuthToken, @PathVariable(value = "diaryId") Long diaryId) {
        String email = jwtTokenUtil.getEmailFromToken(xAuthToken);

        Map<String, Object> responseBody = new HashMap<>();

        if (jwtTokenUtil.validateToken(xAuthToken, email)) {
            if(highHeartRate != 0) {
                responseBody.put("status", HttpStatus.OK.value());
                responseBody.put("data", highHeartRate);
                responseBody.put("isSuccess", true);
                return new ResponseEntity<>(responseBody, HttpStatus.OK);
            } else {
                responseBody.put("status", HttpStatus.FORBIDDEN.value());
                responseBody.put("message", "최고 심박수가 존재하지 않습니다.");
                responseBody.put("isSuccess", false);
                return new ResponseEntity<>(responseBody, HttpStatus.FORBIDDEN);
            }

        } else {
            responseBody.put("status", HttpStatus.FORBIDDEN.value());
            responseBody.put("message", "유효하지 않은 사용자 입니다.");
            responseBody.put("isSuccess", false);
            return new ResponseEntity<>(responseBody, HttpStatus.FORBIDDEN);
        }
    }

     */

}
