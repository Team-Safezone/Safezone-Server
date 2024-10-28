package KickIt.server.domain.member.controller;

import KickIt.server.domain.member.dto.FavoriteTeamsDto;
import KickIt.server.domain.member.dto.NicknameDto;
import KickIt.server.jwt.JwtService;
import KickIt.server.domain.member.entity.LoginRequest;
import KickIt.server.domain.member.entity.Member;
import KickIt.server.domain.member.entity.AuthProvider;
import KickIt.server.domain.member.entity.SignupRequest;
import KickIt.server.domain.member.service.MemberService;
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


    @PostMapping("/signup")
    public ResponseEntity<Map<String, Object>> getMember(@RequestParam(value = "loginId") String loginId, @RequestBody SignupRequest signupRequest) {
        Map<String, Object> responseBody = new HashMap<>();

        AuthProvider authProvider = memberService.transAuth(loginId);

        Member member = new Member(signupRequest.getEmail(), signupRequest.getNickname(),
                signupRequest.getFavoriteTeams(), "탱탱볼", signupRequest.isMarketingConsent(), authProvider);

        if (memberService.saveMember(member)) {
            String accessToken = jwtService.createAccessToken(member.getEmail());

            Map<String, Object> data = new HashMap<>();
            data.put("xAuthToken", accessToken);

            responseBody.put("status", HttpStatus.OK.value());
            responseBody.put("message", "success");
            responseBody.put("data", data);
            return new ResponseEntity<>(responseBody, HttpStatus.OK);
        } else {
            responseBody.put("status", HttpStatus.FORBIDDEN.value());
            responseBody.put("message", "이미 가입된 회원입니다.");
            return new ResponseEntity<>(responseBody, HttpStatus.FORBIDDEN);
        }

    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> authLogin(@RequestParam(value = "loginId") String loginId, @RequestBody LoginRequest loginRequest) {
        Map<String, Object> responseBody = new HashMap<>();

        AuthProvider authProvider = memberService.transAuth(loginId);

        if (memberService.isMemberExist(loginRequest.getEmail(), authProvider)) {
            String accessToken = jwtService.createAccessToken(loginRequest.getEmail());

            Map<String, Object> data = new HashMap<>();
            data.put("xAuthToken", accessToken);

            responseBody.put("status", HttpStatus.OK.value());
            responseBody.put("message", "success");
            responseBody.put("data", data);
            return new ResponseEntity<>(responseBody, HttpStatus.OK);
        } else {
            responseBody.put("status", HttpStatus.NOT_FOUND.value());
            responseBody.put("message", "가입된 회원이 아닙니다.");
            return new ResponseEntity<>(responseBody, HttpStatus.NOT_FOUND);
        }

    }

    @GetMapping("/check-nickname")
    public ResponseEntity<Map<String, Object>> checkNickname(@RequestParam(value = "nickname") String nickname) {
        Map<String, Object> responseBody = new HashMap<>();

        if (memberService.checkNickname(nickname)) {
            responseBody.put("status", HttpStatus.OK.value());
            responseBody.put("message", "사용 가능한 닉네임입니다.");
            responseBody.put("data", true);
            return new ResponseEntity<>(responseBody, HttpStatus.OK);
        } else {
            responseBody.put("status", HttpStatus.FORBIDDEN.value());
            responseBody.put("message", "이미 사용 중인 닉네임입니다.");
            responseBody.put("data", false);
            return new ResponseEntity<>(responseBody, HttpStatus.FORBIDDEN);
        }
    }

    @PostMapping("/update-favoriteTeams")
    public ResponseEntity<Map<String, Object>> updateTeams(@RequestParam(value = "xAuthToken") String xAuthToken, @RequestBody FavoriteTeamsDto favoriteTeamsDto) {
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

    @PostMapping("update-nickname")
    public ResponseEntity<Map<String, Object>> updateTeams(@RequestParam(value = "xAuthToken") String xAuthToken, @RequestBody NicknameDto nicknameDto) {
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
}
