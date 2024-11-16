package KickIt.server.domain.member.controller;

import KickIt.server.domain.member.dto.FavoriteTeamsDto;
import KickIt.server.domain.member.dto.MypageDto;
import KickIt.server.domain.member.dto.NicknameDto;
import KickIt.server.domain.member.entity.AuthProvider;
import KickIt.server.domain.member.entity.LoginRequest;
import KickIt.server.domain.member.entity.Member;
import KickIt.server.domain.member.entity.SignupRequest;
import KickIt.server.domain.member.service.MemberService;
import KickIt.server.jwt.JwtService;
import KickIt.server.jwt.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/users")
public class MemberController {

    private final MemberService memberService;
    private final JwtService jwtService;
    private final JwtTokenUtil jwtTokenUtil;

    @Autowired
    public MemberController(MemberService memberService, JwtService jwtService, JwtTokenUtil jwtTokenUtil) {
        this.memberService = memberService;
        this.jwtService = jwtService;
        this.jwtTokenUtil = jwtTokenUtil;
    }


    @PostMapping("/signup/{loginId}")
    public ResponseEntity<Map<String, Object>> getMember(@PathVariable(value = "loginId") String loginId, @RequestBody SignupRequest signupRequest) {
        Map<String, Object> responseBody = new HashMap<>();

        AuthProvider authProvider = memberService.transAuth(loginId);

        Member member = new Member(signupRequest.getEmail(), signupRequest.getNickname(),
                signupRequest.getFavoriteTeams(), 0, 1, signupRequest.isMarketingConsent(), authProvider);

        if (memberService.saveMember(member)) {
            String accessToken = jwtService.createAccessToken(member.getEmail());

            responseBody.put("status", HttpStatus.OK.value());
            responseBody.put("message", "success");
            responseBody.put("data", accessToken);
            responseBody.put("isSuccess", true);
            return new ResponseEntity<>(responseBody, HttpStatus.OK);
        } else {
            responseBody.put("status", HttpStatus.FORBIDDEN.value());
            responseBody.put("message", "이미 가입된 회원입니다.");
            responseBody.put("isSuccess", false);
            return new ResponseEntity<>(responseBody, HttpStatus.FORBIDDEN);
        }

    }

    @PostMapping("/login/{loginId}")
    public ResponseEntity<Map<String, Object>> authLogin(@PathVariable(value = "loginId") String loginId, @RequestBody LoginRequest loginRequest) {
        Map<String, Object> responseBody = new HashMap<>();

        AuthProvider authProvider = memberService.transAuth(loginId);

        if (memberService.isMemberExist(loginRequest.getEmail(), authProvider)) {
            String accessToken = jwtService.createAccessToken(loginRequest.getEmail());

            responseBody.put("status", HttpStatus.OK.value());
            responseBody.put("message", "success");
            responseBody.put("data", accessToken);
            responseBody.put("isSuccess", true);
            return new ResponseEntity<>(responseBody, HttpStatus.OK);
        } else {
            responseBody.put("status", HttpStatus.NOT_FOUND.value());
            responseBody.put("message", "가입된 회원이 아닙니다.");
            responseBody.put("isSuccess", false);
            return new ResponseEntity<>(responseBody, HttpStatus.NOT_FOUND);
        }

    }

    @GetMapping("/check-nickname")
    public ResponseEntity<Map<String, Object>> checkNickname(
            @RequestParam(value = "nickname") String nickname,
            @RequestHeader(value = "xAuthToken", required = false) String xAuthToken) {

        Map<String, Object> responseBody = new HashMap<>();

        // 토큰이 존재하는지 확인
        if (xAuthToken != null && !xAuthToken.isEmpty()) {
            String email = jwtTokenUtil.getEmailFromToken(xAuthToken);

            // 토큰 유효성 검사
            if (jwtTokenUtil.validateToken(xAuthToken, email)) {
                // 닉네임 중복 확인
                if (memberService.checkNickname(nickname)) {
                    responseBody.put("status", HttpStatus.OK.value());
                    responseBody.put("message", "사용 가능한 닉네임입니다.");
                    responseBody.put("isSuccess", true);
                    return new ResponseEntity<>(responseBody, HttpStatus.OK);
                } else {
                    responseBody.put("status", HttpStatus.FORBIDDEN.value());
                    responseBody.put("message", "이미 사용 중인 닉네임입니다.");
                    responseBody.put("isSuccess", false);
                    return new ResponseEntity<>(responseBody, HttpStatus.FORBIDDEN);
                }
            } else {
                responseBody.put("status", HttpStatus.FORBIDDEN.value());
                responseBody.put("message", "유효하지 않은 사용자입니다.");
                responseBody.put("isSuccess", false);
                return new ResponseEntity<>(responseBody, HttpStatus.FORBIDDEN);
            }
        } else {
            // 토큰이 없는 경우
            // 닉네임 중복 확인
            if (memberService.checkNickname(nickname)) {
                responseBody.put("status", HttpStatus.OK.value());
                responseBody.put("message", "사용 가능한 닉네임입니다.");
                responseBody.put("isSuccess", true);
                return new ResponseEntity<>(responseBody, HttpStatus.OK);
            } else {
                responseBody.put("status", HttpStatus.FORBIDDEN.value());
                responseBody.put("message", "이미 사용 중인 닉네임입니다.");
                responseBody.put("isSuccess", false);
                return new ResponseEntity<>(responseBody, HttpStatus.FORBIDDEN);
            }
        }
    }


    @PostMapping("/update-favoriteTeams")
    public ResponseEntity<Map<String, Object>> updateTeams(@RequestHeader(value = "xAuthToken") String xAuthToken, @RequestBody FavoriteTeamsDto favoriteTeamsDto) {
        String email = jwtTokenUtil.getEmailFromToken(xAuthToken);
        List<String> favoriteTeams = favoriteTeamsDto.getFavoriteTeams();

        Map<String, Object> responseBody = new HashMap<>();

        if (jwtTokenUtil.validateToken(xAuthToken, email)) {
            memberService.updateTeams(email, favoriteTeams);
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


    @PostMapping("/update-nickname")
    public ResponseEntity<Map<String, Object>> updateNickname(@RequestHeader(value = "xAuthToken") String xAuthToken, @RequestBody NicknameDto nicknameDto) {
        String email = jwtTokenUtil.getEmailFromToken(xAuthToken);
        String nickname = nicknameDto.getNickname();

        Map<String, Object> responseBody = new HashMap<>();

        if (jwtTokenUtil.validateToken(xAuthToken, email)) {
            if (memberService.checkNickname(nickname)) {
                memberService.updateNickname(email, nickname);

                responseBody.put("status", HttpStatus.OK.value());
                responseBody.put("message", "success");
                responseBody.put("isSuccess", true);
                return new ResponseEntity<>(responseBody, HttpStatus.OK);
            } else {
                responseBody.put("status", HttpStatus.FORBIDDEN.value());
                responseBody.put("message", "중복된 닉네임 입니다.");
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

    @GetMapping("/mypage")
    public ResponseEntity<Map<String, Object>> getMypage(@RequestHeader(value = "xAuthToken") String xAuthToken) {
        String email = jwtTokenUtil.getEmailFromToken(xAuthToken);

        Map<String, Object> responseBody = new HashMap<>();

        if (jwtTokenUtil.validateToken(xAuthToken, email)) {
            MypageDto response = memberService.getMypage(email);

            responseBody.put("status", HttpStatus.OK.value());
            responseBody.put("message", "success");
            responseBody.put("data", response);
            responseBody.put("isSuccess", true);
            return new ResponseEntity<>(responseBody, HttpStatus.OK);
        } else {
            responseBody.put("status", HttpStatus.FORBIDDEN.value());
            responseBody.put("message", "이유 작성");
            responseBody.put("isSuccess", false);
            return new ResponseEntity<>(responseBody, HttpStatus.FORBIDDEN);
        }


    }

    @GetMapping("/avgHeartRate")
    public ResponseEntity<Map<String, Object>> getMemberAvgHeartRate(@RequestHeader(value = "xAuthToken") String xAuthToken) {
        String email = jwtTokenUtil.getEmailFromToken(xAuthToken);

        Map<String, Object> responseBody = new HashMap<>();

        int response = memberService.getMemberAvgHeartRate(email);

        if (jwtTokenUtil.validateToken(xAuthToken, email)) {
            responseBody.put("status", HttpStatus.OK.value());
            responseBody.put("message", "success");
            responseBody.put("avgHeartRate", response);
            responseBody.put("isSuccess", true);
            return new ResponseEntity<>(responseBody, HttpStatus.OK);
        } else {
            responseBody.put("status", HttpStatus.FORBIDDEN.value());
            responseBody.put("message", "유효한 토큰이 아닙니다.");
            responseBody.put("isSuccess", false);
            return new ResponseEntity<>(responseBody, HttpStatus.FORBIDDEN);
        }
    }
}
