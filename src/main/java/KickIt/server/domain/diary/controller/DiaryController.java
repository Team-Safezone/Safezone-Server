package KickIt.server.domain.diary.controller;


import KickIt.server.domain.diary.dto.*;
import KickIt.server.domain.diary.service.*;
import KickIt.server.domain.heartRate.service.HeartRateStatisticsService;
import KickIt.server.jwt.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/diary")
public class DiaryController {

    private final JwtTokenUtil jwtTokenUtil;
    private final DiaryService diaryService;
    private final HeartRateStatisticsService heartRateStatisticsService;
    private final DiaryLikedService diaryLikedService;
    private final MyDiaryService myDiaryService;
    private final DiaryReportService diaryReportService;
    private final DiaryRecommendService diaryRecommendService;

    @Autowired
    public DiaryController(JwtTokenUtil jwtTokenUtil, DiaryService diaryService, HeartRateStatisticsService heartRateStatisticsService, DiaryLikedService diaryLikedService, MyDiaryService myDiaryService, DiaryReportService diaryReportService, DiaryRecommendService diaryRecommendService) {
        this.jwtTokenUtil = jwtTokenUtil;
        this.diaryService = diaryService;
        this.heartRateStatisticsService = heartRateStatisticsService;
        this.diaryLikedService = diaryLikedService;
        this.myDiaryService = myDiaryService;
        this.diaryReportService = diaryReportService;
        this.diaryRecommendService = diaryRecommendService;
    }

    // 일기 업로드
    @PostMapping("/upload")
    public ResponseEntity<Map<String, Object>> uploadDiary(@RequestHeader(value = "xAuthToken") String xAuthToken,
                                                           @RequestPart(value = "matchId") Long matchId,
                                                           @RequestPart(value = "teamName") String teamName,
                                                           @RequestPart(value = "emotion") int emotion,
                                                           @RequestPart(value = "diaryContent") String diaryContent,
                                                           @RequestPart(value = "diaryPhotos", required = false) List<MultipartFile> diaryPhotos,
                                                           @RequestPart(value = "mom", required = false) String mom,
                                                           @RequestPart(value = "isPublic") Boolean isPublic
    ) {
        String email = jwtTokenUtil.getEmailFromToken(xAuthToken);

        Map<String, Object> responseBody = new HashMap<>();

        if (jwtTokenUtil.validateToken(xAuthToken, email)) {
            diaryService.save(email, matchId, teamName, emotion, diaryContent, diaryPhotos, mom, isPublic);

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


    // 일기 삭제
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


    // 일기에 보여 줄 최고 심박수
    @GetMapping("/max-heartRate/{matchId}")
    public ResponseEntity<Map<String, Object>> getMaxHeartRate(@RequestHeader(value = "xAuthToken") String xAuthToken, @PathVariable(value = "matchId") Long matchId) {
        String email = jwtTokenUtil.getEmailFromToken(xAuthToken);

        Map<String, Object> responseBody = new HashMap<>();

        if (jwtTokenUtil.validateToken(xAuthToken, email)) {
            int highHeartRate = heartRateStatisticsService.getMax(email, matchId);
            if (highHeartRate != 0) {
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

    // 일기 좋아요 반영
    @PatchMapping("/isLiked/{diaryId}")
    public ResponseEntity<Map<String, Object>> editLiked(@RequestHeader(value = "xAuthToken") String xAuthToken, @PathVariable(value = "diaryId") Long diaryId, @RequestBody DiaryLikeDto diaryLikeDto) {
        String email = jwtTokenUtil.getEmailFromToken(xAuthToken);

        boolean isLiked = diaryLikeDto.isLiked();

        Map<String, Object> responseBody = new HashMap<>();

        if (jwtTokenUtil.validateToken(xAuthToken, email)) {
            diaryService.updateLike(diaryId, isLiked);
            diaryLikedService.saveLike(email, diaryId, isLiked);

            responseBody.put("status", HttpStatus.OK.value());
            responseBody.put("message", "success");
            responseBody.put("isSuccess", true);
            return new ResponseEntity<>(responseBody, HttpStatus.OK);

        } else {
            responseBody.put("status", HttpStatus.FORBIDDEN.value());
            responseBody.put("message", "유효하지 않은 사용자 입니다.");
            responseBody.put("isSuccess", false);
            return new ResponseEntity<>(responseBody, HttpStatus.FORBIDDEN);
        }
    }

    // 나의 축구 일기 조회
    @GetMapping("/mine")
    public ResponseEntity<Map<String, Object>> getMyDiary(@RequestHeader(value = "xAuthToken") String xAuthToken) {
        String email = jwtTokenUtil.getEmailFromToken(xAuthToken);

        Map<String, Object> responseBody = new HashMap<>();

        if (jwtTokenUtil.validateToken(xAuthToken, email)) {
            List<MyDiaryDto> response = myDiaryService.getMyDiary(email);

            responseBody.put("status", HttpStatus.OK.value());
            responseBody.put("message", "success");
            responseBody.put("data", response);
            responseBody.put("isSuccess", true);

            return new ResponseEntity<>(responseBody, HttpStatus.OK);


        } else {
            responseBody.put("status", HttpStatus.FORBIDDEN.value());
            responseBody.put("message", "유효하지 않은 사용자 입니다.");
            responseBody.put("isSuccess", false);

            return new ResponseEntity<>(responseBody, HttpStatus.FORBIDDEN);
        }
    }

    // 일기 신고하기
    @PostMapping("/report/{diaryId}")
    public ResponseEntity<Map<String, Object>> postReport(@RequestHeader(value = "xAuthToken") String xAuthToken, @PathVariable(value = "diaryId") Long diaryId, @RequestBody DiaryReportDto diaryReportDto) {
        String email = jwtTokenUtil.getEmailFromToken(xAuthToken);

        Map<String, Object> responseBody = new HashMap<>();

        if (jwtTokenUtil.validateToken(xAuthToken, email)) {
            boolean isReport = diaryReportService.saveReport(diaryReportDto, email, diaryId);

            if (isReport) {
                responseBody.put("status", HttpStatus.OK.value());
                responseBody.put("message", "신고가 완료 되었습니다.");
                responseBody.put("isSuccess", true);

                return new ResponseEntity<>(responseBody, HttpStatus.OK);
            } else {
                responseBody.put("status", HttpStatus.CONFLICT.value());
                responseBody.put("message", "이미 신고를 완료 하였습니다.");
                responseBody.put("isSuccess", false);

                return new ResponseEntity<>(responseBody, HttpStatus.OK);
            }

        } else {
            responseBody.put("status", HttpStatus.FORBIDDEN.value());
            responseBody.put("message", "유효하지 않은 사용자입니다.");
            responseBody.put("isSuccess", false);
            return new ResponseEntity<>(responseBody, HttpStatus.FORBIDDEN);
        }
    }

    // 일기 수정
    @PatchMapping("/edit/{diaryId}")
    public ResponseEntity<Map<String, Object>> editDiary(@RequestHeader(value = "xAuthToken") String xAuthToken, @PathVariable(value = "diaryId") Long diaryId,
                                                         @RequestPart(value = "teamName", required = false) String teamName,
                                                         @RequestPart(value = "emotion", required = false) Integer emotion,
                                                         @RequestPart(value = "diaryContent", required = false) String diaryContent,
                                                         @RequestPart(value = "diaryPhotos", required = false) List<MultipartFile> diaryPhotos,
                                                         @RequestPart(value = "mom", required = false) String mom,
                                                         @RequestPart(value = "isPublic", required = false) Boolean isPublic) {
        String email = jwtTokenUtil.getEmailFromToken(xAuthToken);

        Map<String, Object> responseBody = new HashMap<>();

        if (jwtTokenUtil.validateToken(xAuthToken, email)) {
            boolean isEdit = diaryService.updateDiary(diaryId, email, teamName, emotion, diaryContent, diaryPhotos, mom, isPublic);

            if (isEdit) {
                responseBody.put("status", HttpStatus.OK.value());
                responseBody.put("message", "변경이 완료 되었습니다.");
                responseBody.put("isSuccess", true);
                return new ResponseEntity<>(responseBody, HttpStatus.OK);
            } else {
                responseBody.put("status", HttpStatus.OK.value());
                responseBody.put("message", "변경된 내용이 없습니다.");
                responseBody.put("isSuccess", true);
                return new ResponseEntity<>(responseBody, HttpStatus.OK);
            }

        } else {
            responseBody.put("status", HttpStatus.FORBIDDEN.value());
            responseBody.put("message", "유효하지 않은 사용자 입니다.");
            responseBody.put("isSuccess", false);
            return new ResponseEntity<>(responseBody, HttpStatus.FORBIDDEN);
        }
    }


    /*
    // 추천 축구 일기
    @GetMapping("/recommend")
    public ResponseEntity<Map<String, Object>> getRecommendDiary(@RequestHeader(value = "xAuthToken") String xAuthToken) {
        String email = jwtTokenUtil.getEmailFromToken(xAuthToken);

        Map<String, Object> responseBody = new HashMap<>();

        if (jwtTokenUtil.validateToken(xAuthToken, email)) {
            List<DiaryRecommendDto> response = (email);

            responseBody.put("status", HttpStatus.OK.value());
            responseBody.put("message", "success");
            responseBody.put("data", response);
            responseBody.put("isSuccess", true);

            return new ResponseEntity<>(responseBody, HttpStatus.OK);


        } else {
            responseBody.put("status", HttpStatus.FORBIDDEN.value());
            responseBody.put("message", "유효하지 않은 사용자 입니다.");
            responseBody.put("isSuccess", false);

            return new ResponseEntity<>(responseBody, HttpStatus.FORBIDDEN);
        }
    }

     */
}
