package KickIt.server.domain.user.controller;

import KickIt.server.domain.user.AuthTokens;
import KickIt.server.domain.user.OAuthLoginService;
import KickIt.server.domain.user.dto.NaverLoginParams;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {
    private final OAuthLoginService oAuthLoginService;

    @PostMapping("/naver")
    public ResponseEntity<AuthTokens> loginNaver(@RequestBody NaverLoginParams params) {
        return ResponseEntity.ok(oAuthLoginService.login(params));
    }

    @GetMapping("/naver/callback")
    public ResponseEntity<String> naverCallback(@RequestParam String code, @RequestParam String state) {
        // 여기서 code와 state를 사용해 토큰 요청 처리
        return ResponseEntity.ok("Naver callback received. Code: " + code + ", State: " + state);
    }


}