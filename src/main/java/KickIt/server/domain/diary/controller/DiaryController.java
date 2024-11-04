package KickIt.server.domain.diary.controller;


import KickIt.server.domain.diary.dto.DiaryIdRequest;
import KickIt.server.domain.diary.dto.DiarySaveDto;
import KickIt.server.domain.diary.service.DiaryService;
import KickIt.server.jwt.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/diary")
public class DiaryController {

    private final JwtTokenUtil jwtTokenUtil;
    private final DiaryService diaryService;


    @Autowired
    public DiaryController(JwtTokenUtil jwtTokenUtil, DiaryService diaryService) {
        this.jwtTokenUtil = jwtTokenUtil;
        this.diaryService = diaryService;
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

}
